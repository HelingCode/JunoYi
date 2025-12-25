package com.junoyi.framework.permission.exception;

/**
 * 权限异常
 *
 * @author Fan
 */
public class PermissionException extends RuntimeException {

    /**
     * 缺少的权限节点
     */
    private final String[] requiredPermissions;

    public PermissionException(String message) {
        super(message);
        this.requiredPermissions = new String[0];
    }

    public PermissionException(String message, String... requiredPermissions) {
        super(message);
        this.requiredPermissions = requiredPermissions;
    }

    public String[] getRequiredPermissions() {
        return requiredPermissions;
    }

    /**
     * 创建无权限异常
     */
    public static PermissionException noPermission(String... permissions) {
        String msg = "没有访问权限";
        if (permissions != null && permissions.length > 0) {
            msg = "缺少权限: " + String.join(", ", permissions);
        }
        return new PermissionException(msg, permissions);
    }

    /**
     * 创建未登录异常
     */
    public static PermissionException notLogin() {
        return new PermissionException("请先登录");
    }
}
