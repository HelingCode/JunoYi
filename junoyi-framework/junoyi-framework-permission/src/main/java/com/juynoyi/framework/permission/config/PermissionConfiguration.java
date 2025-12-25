package com.juynoyi.framework.permission.config;

import com.juynoyi.framework.permission.properties.PermissionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * 权限配置类
 * 用于定义和配置应用程序的权限相关设置，包括权限验证规则、访问控制策略等
 * 该类作为Spring配置类，提供权限管理相关的Bean定义和配置
 *
 * @author Fan
 */
@Configuration
@EnableConfigurationProperties(PermissionProperties.class)
public class PermissionConfiguration {


}
