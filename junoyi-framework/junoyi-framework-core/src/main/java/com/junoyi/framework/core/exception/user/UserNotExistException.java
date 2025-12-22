package com.junoyi.framework.core.exception.user;


/**
 * 用户不存在异常类
 * 用于表示用户不存在的业务异常情况
 *
 * @author Fan
 */
public class UserNotExistException extends UserException {

    /**
     * 构造函数
     * @param code 异常码
     * @param message 异常信息
     * @param domain 异常域
     */
    public UserNotExistException(int code, String message, String domain){
        super(code,message,domain);
    }

    /**
     * 构造函数
     * @param code 异常码
     * @param message 异常信息
     */
    public UserNotExistException(int code, String message){
        super(code,message);
    }

    /**
     * 构造函数
     * @param message 异常信息
     */
    public UserNotExistException(String message){
        super(404, message, "NOT_EXIST");
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
