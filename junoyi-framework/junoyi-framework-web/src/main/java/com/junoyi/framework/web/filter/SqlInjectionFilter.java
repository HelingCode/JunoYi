package com.junoyi.framework.web.filter;

import com.junoyi.framework.web.properties.SQLInjectionProperties;
import com.junoyi.framework.web.sql.SqlInjectionHttpServletRequestWrapper;
import com.junoyi.framework.web.sql.SqlInjectionUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;

/**
 * SQL 注入防护过滤器
 *
 * @author Fan
 */
public class SqlInjectionFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SqlInjectionFilter.class);

    private final SQLInjectionProperties properties;
    private final PathMatcher pathMatcher = new AntPathMatcher();

    public SqlInjectionFilter(SQLInjectionProperties properties) {
        this.properties = properties;
        // 设置自定义关键词
        if (properties.getCustomKeywords() != null && !properties.getCustomKeywords().isEmpty()) {
            SqlInjectionUtils.setCustomKeywords(new HashSet<>(properties.getCustomKeywords()));
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.debug("[SQL注入防护] 处理请求: {} {}", request.getMethod(), request.getRequestURI());

        // 判断是否需要过滤
        if (shouldSkip(request)) {
            log.debug("[SQL注入防护] 跳过过滤: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("[SQL注入防护] 执行过滤: {}, 模式: {}", request.getRequestURI(), properties.getMode());

        // DETECT 模式：先检测是否包含 SQL 注入
        if (properties.getMode() == SQLInjectionProperties.SQLInjectionMode.DETECT) {
            // 检测请求参数
            if (properties.isFilterParameter()) {
                String detected = checkParameters(request);
                if (detected != null) {
                    log.warn("[SQL注入拦截] 请求地址: {}, 参数包含 SQL 注入: {}", request.getRequestURI(), detected);
                    rejectRequest(response);
                    return;
                }
            }

            // 检测请求头
            if (properties.isFilterHeader()) {
                String detected = checkHeaders(request);
                if (detected != null) {
                    log.warn("[SQL注入拦截] 请求地址: {}, 请求头包含 SQL 注入: {}", request.getRequestURI(), detected);
                    rejectRequest(response);
                    return;
                }
            }

            // 检测请求体（需要包装请求）
            if (properties.isFilterBody() && hasRequestBody(request)) {
                SqlInjectionHttpServletRequestWrapper wrappedRequest = new SqlInjectionHttpServletRequestWrapper(request, properties);
                byte[] body = wrappedRequest.getOriginalBody();
                if (body != null && body.length > 0) {
                    String bodyStr = new String(body, StandardCharsets.UTF_8);
                    if (SqlInjectionUtils.containsSqlInjection(bodyStr)) {
                        log.warn("[SQL注入拦截] 请求地址: {}, 请求体包含 SQL 注入", request.getRequestURI());
                        rejectRequest(response);
                        return;
                    }
                }
                filterChain.doFilter(wrappedRequest, response);
                return;
            }

            filterChain.doFilter(request, response);
            return;
        }

        // CLEAN 模式：使用包装器清理
        SqlInjectionHttpServletRequestWrapper wrappedRequest = new SqlInjectionHttpServletRequestWrapper(request, properties);
        filterChain.doFilter(wrappedRequest, response);
    }

    /**
     * 判断是否跳过检测
     */
    private boolean shouldSkip(HttpServletRequest request) {
        if (!properties.isEnable()) return true;

        // 排除的请求方法
        String method = request.getMethod();
        if (properties.getExcludeMethods().stream().anyMatch(m -> m.equalsIgnoreCase(method))) {
            return true;
        }

        // GET/HEAD/OPTIONS 请求如果不检测参数则跳过
        if (HttpMethod.GET.matches(method) || HttpMethod.HEAD.matches(method) || HttpMethod.OPTIONS.matches(method)) {
            return !properties.isFilterParameter();
        }

        // 排除的 URL
        String uri = request.getRequestURI();
        return properties.getExcludeUrls().stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    /**
     * 判断请求是否有请求体
     */
    private boolean hasRequestBody(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null) return false;
        // 文件上传跳过
        if (contentType.toLowerCase().contains("multipart/form-data")) return false;
        return request.getContentLength() > 0 || request.getContentLengthLong() > 0;
    }

    /**
     * 检测请求参数
     */
    private String checkParameters(HttpServletRequest request) {
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            String[] values = request.getParameterValues(name);
            if (values != null) {
                for (String value : values) {
                    if (SqlInjectionUtils.containsSqlInjection(value)) {
                        return name + "=" + value;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 检测请求头
     */
    private String checkHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (isStandardHeader(name)) continue;

            String value = request.getHeader(name);
            if (SqlInjectionUtils.containsSqlInjection(value)) {
                return name + ": " + value;
            }
        }
        return null;
    }

    /**
     * 拒绝请求
     */
    private void rejectRequest(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"code\":400,\"msg\":\"请求包含非法字符\"}");
    }

    /**
     * 判断是否为标准请求头
     */
    private boolean isStandardHeader(String name) {
        String lowerName = name.toLowerCase();
        return lowerName.equals("host") || lowerName.equals("connection") ||
                lowerName.equals("accept") || lowerName.equals("accept-language") ||
                lowerName.equals("accept-encoding") || lowerName.equals("content-type") ||
                lowerName.equals("content-length") || lowerName.equals("user-agent") ||
                lowerName.equals("authorization") || lowerName.equals("cookie") ||
                lowerName.equals("origin") || lowerName.equals("referer") ||
                lowerName.equals("cache-control") || lowerName.equals("pragma");
    }
}
