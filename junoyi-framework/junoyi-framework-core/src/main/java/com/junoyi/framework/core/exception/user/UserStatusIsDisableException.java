package com.junoyi.framework.core.exception.user;

/**
 * 用户状态被禁用异常类
 * 当用户状态为禁用状态时抛出此异常
 *
 * @author Fan
 */
public class UserStatusIsDisableException extends UserException{

    /**
     * 构造函数
     * @param code 异常码
     * @param message 异常信息
     * @param domain 异常域
     */
    public UserStatusIsDisableException(int code, String message, String domain){
        super(code, message, domain);
    }

    /**
     * 构造函数
     * @param code 异常码
     * @param message 异常信息
     */
    public UserStatusIsDisableException(int code, String message){
        super(code, message, null);
    }

    /**
     * 构造函数
     * @param message 异常信息
     */
    public UserStatusIsDisableException(String message){
        super(403,message,"STATUS_IS_DISABLE");
    }

    /**
     * 获取域前缀
     * @return 域前缀
     */
    @Override
    public String getDomainPrefix() {
        return super.getDomainPrefix();
    }
}
