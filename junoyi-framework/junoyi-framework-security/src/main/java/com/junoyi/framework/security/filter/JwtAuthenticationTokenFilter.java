package com.junoyi.framework.security.filter;

import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.security.helper.TokenHelper;
import com.junoyi.framework.security.module.LoginUser;
import com.junoyi.framework.security.properties.SecurityProperties;
import com.junoyi.framework.security.utils.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 认证过滤器
 * 用于拦截请求并验证 Token 的有效性
 * 继承 OncePerRequestFilter 确保每个请求只执行一次过滤
 *
 * @author Fan
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(JwtAuthenticationTokenFilter.class);

    private final TokenHelper tokenHelper;

    private final SecurityProperties securityProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 执行过滤逻辑
     *
     * @param request     HTTP 请求对象
     * @param response    HTTP 响应对象
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        
        // 检查是否在白名单中
        if (isWhitelisted(requestURI)) {
            log.debug("WhitelistAccess", "URI: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        
        // 从请求头中获取 Token
        String token = getTokenFromRequest(request);
        
        if (StringUtils.isBlank(token)) {
            log.warn("TokenMissing", "URI: " + requestURI);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未提供认证令牌\"}");
            return;
        }
        
        try {
            // 验证 Token 有效性
            if (!tokenHelper.validateAccessToken(token)) {
                log.warn("TokenInvalid", "URI: " + requestURI + " | Token: " + maskToken(token));
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"msg\":\"认证令牌无效或已过期\"}");
                return;
            }
            
            // 解析 Token 获取用户信息
            LoginUser loginUser = tokenHelper.paresAccessToken(token);
            
            if (loginUser == null) {
                log.warn("TokenParseError", "URI: " + requestURI);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"msg\":\"认证令牌解析失败\"}");
                return;
            }
            
            // 将用户信息存储到上下文中
            SecurityUtils.setLoginUser(loginUser);
            
            log.debug("TokenValidated", "User: " + loginUser.getUserName() + " | URI: " + requestURI);
            
            // 继续执行过滤器链
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            log.error("TokenValidationError", "URI: " + requestURI, e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"认证失败\"}");
        } finally {
            // 清理上下文
            SecurityUtils.clearLoginUser();
        }
    }

    /**
     * 从请求中获取 Token
     *
     * @param request HTTP 请求对象
     * @return Token 字符串，如果不存在则返回 null
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 从请求头中获取
        String token = request.getHeader(securityProperties.getToken().getHeader());
        
        if (StringUtils.isNotBlank(token)) {
            // 移除请求头token前缀（如果存在）
            if (token.startsWith(securityProperties.getToken().getHeader() + " "))
                token = token.substring(7);
            return token;
        }
        
        // 从请求参数中获取（备用方案）
        token = request.getParameter("token");
        if (StringUtils.isNotBlank(token))
            return token;

        return null;
    }

    /**
     * 检查请求路径是否在白名单中
     *
     * @param requestURI 请求 URI
     * @return true=在白名单中，false=不在白名单中
     */
    private boolean isWhitelisted(String requestURI) {
        List<String> whitelist = securityProperties.getWhitelist();
        if (whitelist == null || whitelist.isEmpty())
            return false;

        
        for (String pattern : whitelist) {
            if (pathMatcher.match(pattern, requestURI))
                return true;
        }
        
        return false;
    }

    /**
     * 脱敏 Token（用于日志输出）
     *
     * @param token 原始 Token
     * @return 脱敏后的 Token
     */
    private String maskToken(String token) {
        if (StringUtils.isBlank(token) || token.length() < 10)
            return "***";
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }
}
