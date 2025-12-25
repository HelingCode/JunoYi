package com.junoyi.framework.permission.core;

/**
 * 权限上下文持有者
 * <p>
 * 使用 ThreadLocal 存储当前线程的权限上下文
 *
 * @author Fan
 */
public class PermissionContextHolder {

    private static final ThreadLocal<PermissionContext> CONTEXT_HOLDER = new ThreadLocal<>();

    private PermissionContextHolder() {
    }

    /**
     * 设置权限上下文
     *
     * @param context 权限上下文
     */
    public static void setContext(PermissionContext context) {
        CONTEXT_HOLDER.set(context);
    }

    /**
     * 获取权限上下文
     *
     * @return 权限上下文，如果未设置返回 null
     */
    public static PermissionContext getContext() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 获取权限上下文，如果未设置返回空上下文
     *
     * @return 权限上下文
     */
    public static PermissionContext getContextOrEmpty() {
        PermissionContext context = CONTEXT_HOLDER.get();
        return context != null ? context : PermissionContext.empty();
    }

    /**
     * 清除权限上下文
     */
    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }
}
