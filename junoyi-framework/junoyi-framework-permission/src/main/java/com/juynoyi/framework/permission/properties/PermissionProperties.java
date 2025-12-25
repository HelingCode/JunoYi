package com.juynoyi.framework.permission.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 权限配置属性类，用于配置系统权限相关参数
 * 通过@ConfigurationProperties注解绑定配置文件中以"junoyi.permission"为前缀的属性
 *
 * @author Fan
 */
@Data
@ConfigurationProperties(prefix = "junoyi.permission")
public class PermissionProperties {

    /**
     * 是否启用权限控制功能
     */
    private boolean enable;

    /**
     * 权限缓存配置
     */
    private Cache cache;

    /**
     * 超级管理员配置
     */
    private SuperAdmin superAdmin;

    /**
     * 默认用户组列表
     */
    private List<String> defaultGroups;

    /**
     * 权限缓存配置类
     */
    @Data
    public static class Cache {

        /**
         * 是否启用缓存功能
         */
        private boolean enable;

        /**
         * 缓存过期时间（单位：毫秒）
         */
        private Long expire;
    }

    /**
     * 超级管理员配置类
     */
    @Data
    public static class SuperAdmin {

        /**
         * 是否启用超级管理员功能
         */
        private boolean enable;

        /**
         * 超级管理员用户ID数组
         */
        private Long[] userIds;

        /**
         * 超级管理员权限标识
         */
        private String permission;

    }
}
