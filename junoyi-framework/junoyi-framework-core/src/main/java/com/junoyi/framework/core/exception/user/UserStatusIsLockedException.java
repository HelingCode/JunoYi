package com.junoyi.framework.core.exception.user;

/**
 * 用户状态被锁定异常类
 * 当用户账户状态为锁定状态时抛出此异常
 *
 * @author Fan
 */
public class UserStatusIsLockedException extends UserException {

    /**
     * 构造函数，使用指定的错误码、消息和域创建异常
     *
     * @param code 错误码
     * @param message 错误消息
     * @param domain 错误域
     */
    public UserStatusIsLockedException(int code, String message, String domain){
        super(code, message, domain);
    }

    /**
     * 构造函数，使用指定的错误码和消息创建异常，默认域为null
     *
     * @param code 错误码
     * @param message 错误消息
     */
    public UserStatusIsLockedException(int code, String message){
        super(code, message,null);
    }

    /**
     * 构造函数，使用指定的消息创建异常，默认错误码为403，域为"STATUS_IS_LOCKED"
     *
     * @param message 错误消息
     */
    public UserStatusIsLockedException(String message){
        super(403, message, "STATUS_IS_LOCKED");
    }

    /**
     * 获取域前缀
     *
     * @return 域前缀字符串
     */
    @Override
    public String getDomainPrefix() {
        return super.getDomainPrefix();
    }
}
