package com.junoyi.framework.web.config;

import com.junoyi.framework.web.filter.SqlInjectionFilter;
import com.junoyi.framework.web.filter.XssFilter;
import com.junoyi.framework.web.properties.SQLInjectionProperties;
import com.junoyi.framework.web.properties.XssProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Web 模块配置
 *
 * @author Fan
 */
@Configuration
@EnableConfigurationProperties({XssProperties.class, SQLInjectionProperties.class})
public class WebConfiguration {

    private static final Logger log = LoggerFactory.getLogger(WebConfiguration.class);

    /**
     * XSS 过滤器
     */
    @Bean
    @ConditionalOnProperty(prefix = "junoyi.web.xss", name = "enable", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<XssFilter> xssFilterRegistration(XssProperties xssProperties) {
        log.info("[XSS] XSS过滤器已启用, 模式: {}", xssProperties.getMode());
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new XssFilter(xssProperties));
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        registration.setOrder(1);
        return registration;
    }

    /**
     * SQL 注入防护过滤器
     */
    @Bean
    @ConditionalOnProperty(prefix = "junoyi.web.sql-injection", name = "enable", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<SqlInjectionFilter> sqlInjectionFilterRegistration(SQLInjectionProperties properties) {
        log.info("[SQL注入防护] SQL注入防护过滤器已启用, 模式: {}", properties.getMode());
        FilterRegistrationBean<SqlInjectionFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SqlInjectionFilter(properties));
        registration.addUrlPatterns("/*");
        registration.setName("sqlInjectionFilter");
        registration.setOrder(2); // 在 XSS 过滤器之后执行
        return registration;
    }
}
