package com.junoyi.framework.core.exception.auth;

/**
 * 登录失败账户锁定异常类
 * 当用户账户被锁定时抛出此异常
 *
 * @author Fan
 */
public class LoginFailedAccountLockedException extends LoginException {

    /**
     * 构造函数
     * @param message 异常信息描述
     */
    public LoginFailedAccountLockedException(String message){
        super(401, message, "FAIL_ACCOUNT_LOCKED");
    }

    /**
     * 获取域前缀
     * @return 域前缀字符串
     */
    @Override
    public String getDomainPrefix() {
        return super.getDomainPrefix();
    }
}
