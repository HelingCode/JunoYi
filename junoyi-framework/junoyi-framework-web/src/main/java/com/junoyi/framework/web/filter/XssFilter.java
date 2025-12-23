package com.junoyi.framework.web.filter;

import com.junoyi.framework.web.properties.XssProperties;
import com.junoyi.framework.web.xss.XssHttpServletRequestWrapper;
import com.junoyi.framework.web.xss.XssUtils;
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

/**
 * XSS 过滤器
 *
 * @author Fan
 */
public class XssFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(XssFilter.class);

    private final XssProperties xssProperties;
    private final PathMatcher pathMatcher = new AntPathMatcher();

    public XssFilter(XssProperties xssProperties) {
        this.xssProperties = xssProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 判断是否需要过滤
        if (shouldSkip(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // REJECT 模式：先检测是否包含 XSS
        if (xssProperties.getMode() == XssProperties.XSSMode.REJECT) {
            if (containsXssInRequest(request)) {
                log.warn("[XSS拦截] 请求地址: {}, 检测到 XSS 攻击内容", request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write("{\"code\":400,\"msg\":\"请求包含非法字符\"}");
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }

        // CLEAN / ESCAPE 模式：使用包装器过滤
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(request, xssProperties);
        filterChain.doFilter(xssRequest, response);
    }

    /**
     * 判断是否跳过 XSS 过滤
     */
    private boolean shouldSkip(HttpServletRequest request) {
        // 未启用
        if (!xssProperties.isEnable()) return true;

        // 排除的请求方法
        String method = request.getMethod();
        if (xssProperties.getExcludeMethods().stream()
                .anyMatch(m -> m.equalsIgnoreCase(method))) {
            return true;
        }

        // GET / HEAD / OPTIONS 请求默认跳过
        if (HttpMethod.GET.matches(method) || HttpMethod.HEAD.matches(method) || HttpMethod.OPTIONS.matches(method)) {
            return true;
        }

        // 排除的 Content-Type
        String contentType = request.getContentType();
        if (contentType != null) {
            for (String excludeType : xssProperties.getExcludeContentTypes()) {
                if (contentType.toLowerCase().contains(excludeType.toLowerCase())) {
                    return true;
                }
            }
            // 文件上传默认跳过
            if (contentType.toLowerCase().contains("multipart/form-data")) {
                return true;
            }
        }

        // 排除的 URL
        String uri = request.getRequestURI();
        return xssProperties.getExcludeUrls().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    /**
     * 检测请求中是否包含 XSS 内容（用于 REJECT 模式）
     */
    private boolean containsXssInRequest(HttpServletRequest request) throws IOException {
        // 检测参数
        if (xssProperties.isFilterParameter()) {
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String name = paramNames.nextElement();
                String[] values = request.getParameterValues(name);
                if (values != null) {
                    for (String value : values) {
                        if (XssUtils.containsXss(value)) {
                            log.debug("[XSS检测] 参数 {} 包含 XSS 内容: {}", name, value);
                            return true;
                        }
                    }
                }
            }
        }

        // 检测请求头
        if (xssProperties.isFilterHeader()) {
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                // 跳过一些标准请求头
                if (isStandardHeader(name)) continue;
                String value = request.getHeader(name);
                if (XssUtils.containsXss(value)) {
                    log.debug("[XSS检测] 请求头 {} 包含 XSS 内容: {}", name, value);
                    return true;
                }
            }
        }

        // 检测请求体
        if (xssProperties.isFilterBody()) {
            String body = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (XssUtils.containsXss(body)) {
                log.debug("[XSS检测] 请求体包含 XSS 内容");
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否为标准请求头（不需要 XSS 检测）
     */
    private boolean isStandardHeader(String name) {
        String lowerName = name.toLowerCase();
        return lowerName.equals("host") ||
                lowerName.equals("connection") ||
                lowerName.equals("accept") ||
                lowerName.equals("accept-language") ||
                lowerName.equals("accept-encoding") ||
                lowerName.equals("content-type") ||
                lowerName.equals("content-length") ||
                lowerName.equals("user-agent") ||
                lowerName.equals("authorization") ||
                lowerName.equals("cookie") ||
                lowerName.equals("origin") ||
                lowerName.equals("referer") ||
                lowerName.equals("cache-control") ||
                lowerName.equals("pragma");
    }
}
