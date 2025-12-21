package com.junoyi.framework.security.crypto;

/**
 * CryptoContext类用于提供加密操作的上下文环境。
 * 该类封装了加密相关的配置信息和状态管理，
 * 为加密解密操作提供必要的上下文支持。
 *
 * @author Fan
 */
public class CryptoContext {

    private static final ThreadLocal<byte[]> AES_KEY = new ThreadLocal<>();

    /**
     * 设置当前线程的AES密钥
     * @param key 要设置的AES密钥字节数组
     */
    public static void setAesKey(byte[] key) {
        AES_KEY.set(key);
    }

    /**
     * 获取当前线程的AES密钥
     * @return 当前线程的AES密钥字节数组，如果未设置则返回null
     */
    public static byte[] getAesKey() {
        return AES_KEY.get();
    }

    /**
     * 清除当前线程的AES密钥
     * 释放ThreadLocal变量占用的资源
     */
    public static void clear() {
        AES_KEY.remove();
    }
}
