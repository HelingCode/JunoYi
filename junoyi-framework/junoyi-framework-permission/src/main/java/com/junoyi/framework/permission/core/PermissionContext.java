package com.junoyi.framework.permission.core;

import lombok.Data;

import java.util.Set;

/**
 * 权限上下文
 * <p>
 * 存储当前请求用户的权限信息，包括权限节点集合、权限组集合等
 *
 * @author Fan
 */
@Data
public class PermissionContext {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 权限节点集合
     */
    private Set<String> permissions;

    /**
     * 权限组编码集合
     */
    private Set<String> groups;

    /**
     * 是否为超级管理员
     */
    private boolean superAdmin;

    /**
     * 创建空上下文
     */
    public static PermissionContext empty() {
        PermissionContext context = new PermissionContext();
        context.setPermissions(Set.of());
        context.setGroups(Set.of());
        context.setSuperAdmin(false);
        return context;
    }

    /**
     * 创建超级管理员上下文
     */
    public static PermissionContext superAdmin(Long userId, String username) {
        PermissionContext context = new PermissionContext();
        context.setUserId(userId);
        context.setUsername(username);
        context.setPermissions(Set.of("*"));
        context.setGroups(Set.of("super_admin"));
        context.setSuperAdmin(true);
        return context;
    }
}
