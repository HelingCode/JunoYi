package com.junoyi.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统参数分组枚举
 *
 * @author Fan
 */
@Getter
@AllArgsConstructor
public enum ConfigGroup {

    /**
     * 基础配置
     */
    BASIC("basic", "基础配置"),

    /**
     * 系统配置
     */
    SYSTEM("system", "系统配置"),

    /**
     * 安全配置
     */
    SECURITY("security", "安全配置"),

    /**
     * 上传配置
     */
    UPLOAD("upload", "上传配置"),

    /**
     * 默认分组
     */
    DEFAULT("default", "默认分组");

    /**
     * 分组代码
     */
    private final String code;

    /**
     * 分组描述
     */
    private final String desc;

    /**
     * 根据代码获取枚举
     *
     * @param code 分组代码
     * @return 配置分组枚举，如果不存在则返回null
     */
    public static ConfigGroup fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (ConfigGroup group : values()) {
            if (group.code.equals(code)) {
                return group;
            }
        }
        return null;
    }

    /**
     * 验证代码是否有效
     *
     * @param code 分组代码
     * @return 是否有效
     */
    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }
}
