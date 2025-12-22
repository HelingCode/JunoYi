package com.junoyi.framework.web.xss;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * XSS防护的HTTP请求包装器类
 * 该类继承自HttpServletRequestWrapper，用于包装原始的HttpServletRequest对象，
 * 提供XSS攻击防护功能，主要通过重写相关方法来实现输入数据的过滤和验证
 *
 * @author Fan
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 构造函数，使用指定的HttpServletRequest对象创建包装器实例
     * @param request 原始的HttpServletRequest对象，用于获取请求信息
     */
    public XssHttpServletRequestWrapper(HttpServletRequest request){
        super(request);
    }

    /**
     * 获取请求的输入流
     * 重写父类方法，返回ServletInputStream对象用于读取请求体数据
     * @return ServletInputStream对象，表示请求的输入流
     * @throws IOException 当发生IO异常时抛出
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return super.getInputStream();
    }


    /**
     * 获取请求的字符读取器
     * 重写父类方法，返回BufferedReader对象用于读取请求体的字符数据
     * @return BufferedReader对象，用于读取请求体内容
     * @throws IOException 当发生IO异常时抛出
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return super.getReader();
    }

    /**
     * 获取请求内容的长度
     * 重写父类方法，返回请求体内容的字节长度
     * @return int类型的内容长度，如果长度未知则返回-1
     */
    @Override
    public int getContentLength() {
        return super.getContentLength();
    }

    /**
     * 获取请求内容的长整型长度
     * 重写父类方法，返回请求体内容的字节长度（长整型格式）
     * @return long类型的内容长度，如果长度未知则返回-1
     */
    @Override
    public long getContentLengthLong() {
        return super.getContentLengthLong();
    }

}
