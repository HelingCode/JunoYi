package com.junoyi.framework.permission.helper;

import com.junoyi.framework.permission.core.PermissionContext;
import com.junoyi.framework.permission.core.PermissionContextHolder;
import com.junoyi.framework.permission.enums.Logical;
import com.junoyi.framework.permission.matcher.PermissionMatcher;

import java.util.Collection;
import java.util.Set;

/**
 * 权限工具类
 * <p>
 * 提供静态方法用于权限校验，简化业务代码中的权限判断
 * <p>
 * 使用示例：
 * <pre>
 * // 判断是否有权限
 * if (PermissionHelper.hasPermission("system.user.delete")) {
 *     // 执行删除操作
 * }
 *
 * // 判断是否有任意一个权限
 * if (PermissionHelper.hasAnyPermission("system.user.view", "system.admin")) {
 *     // 执行查看操作
 * }
 * </pre>
 *
 * @author Fan
 */
public class PermissionHelper {

    private PermissionHelper() {
    }

    /**
     * 判断当前用户是否拥有指定权限
     *
     * @param permission 权限节点
     * @return true 有权限，false 无权限
     */
    public static boolean hasPermission(String permission) {
        Set<String> userPermissions = getCurrentUserPermissions();
        return PermissionMatcher.hasPermission(userPermissions, permission);
    }

    /**
     * 判断当前用户是否拥有所有指定权限（AND 逻辑）
     *
     * @param permissions 权限节点数组
     * @return true 拥有所有权限，false 缺少权限
     */
    public static boolean hasAllPermissions(String... permissions) {
        Set<String> userPermissions = getCurrentUserPermissions();
        return PermissionMatcher.hasAllPermissions(userPermissions, permissions);
    }

    /**
     * 判断当前用户是否拥有任意一个指定权限（OR 逻辑）
     *
     * @param permissions 权限节点数组
     * @return true 拥有任意一个权限，false 无任何权限
     */
    public static boolean hasAnyPermission(String... permissions) {
        Set<String> userPermissions = getCurrentUserPermissions();
        return PermissionMatcher.hasAnyPermission(userPermissions, permissions);
    }

    /**
     * 根据逻辑类型判断权限
     *
     * @param permissions 权限节点数组
     * @param logical     逻辑类型
     * @return true 满足条件，false 不满足
     */
    public static boolean hasPermissions(String[] permissions, Logical logical) {
        if (permissions == null || permissions.length == 0) {
            return true;
        }
        if (logical == Logical.AND) {
            return hasAllPermissions(permissions);
        } else {
            return hasAnyPermission(permissions);
        }
    }

    /**
     * 判断当前用户是否为超级管理员
     *
     * @return true 是超级管理员，false 不是
     */
    public static boolean isSuperAdmin() {
        PermissionContext context = PermissionContextHolder.getContext();
        return context != null && context.isSuperAdmin();
    }

    /**
     * 判断当前用户是否在指定权限组中
     *
     * @param groupCode 权限组编码
     * @return true 在权限组中，false 不在
     */
    public static boolean inGroup(String groupCode) {
        PermissionContext context = PermissionContextHolder.getContext();
        if (context == null || context.getGroups() == null) {
            return false;
        }
        return context.getGroups().contains(groupCode);
    }

    /**
     * 判断当前用户是否在任意一个指定权限组中
     *
     * @param groupCodes 权限组编码数组
     * @return true 在任意一个权限组中，false 不在
     */
    public static boolean inAnyGroup(String... groupCodes) {
        if (groupCodes == null || groupCodes.length == 0) {
            return true;
        }
        for (String groupCode : groupCodes) {
            if (inGroup(groupCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前用户的权限集合
     *
     * @return 权限集合，如果未登录返回空集合
     */
    public static Set<String> getCurrentUserPermissions() {
        PermissionContext context = PermissionContextHolder.getContext();
        if (context == null || context.getPermissions() == null) {
            return Set.of();
        }
        return context.getPermissions();
    }

    /**
     * 获取当前用户的权限组集合
     *
     * @return 权限组集合，如果未登录返回空集合
     */
    public static Set<String> getCurrentUserGroups() {
        PermissionContext context = PermissionContextHolder.getContext();
        if (context == null || context.getGroups() == null) {
            return Set.of();
        }
        return context.getGroups();
    }

    /**
     * 获取当前用户ID
     *
     * @return 用户ID，如果未登录返回 null
     */
    public static Long getCurrentUserId() {
        PermissionContext context = PermissionContextHolder.getContext();
        return context != null ? context.getUserId() : null;
    }

    /**
     * 获取当前用户部门ID
     *
     * @return 部门ID，如果未登录返回 null
     */
    public static Long getCurrentDeptId() {
        PermissionContext context = PermissionContextHolder.getContext();
        return context != null ? context.getDeptId() : null;
    }

    /**
     * 检查指定用户是否拥有权限
     *
     * @param userPermissions 用户权限集合
     * @param permission      需要的权限
     * @return true 有权限，false 无权限
     */
    public static boolean checkPermission(Collection<String> userPermissions, String permission) {
        return PermissionMatcher.hasPermission(userPermissions, permission);
    }
}
