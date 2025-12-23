package com.junoyi.framework.web.xss;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * XSS 防护的 HTTP 请求包装器
 *
 * @author Fan
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private byte[] body;

    /**
     * 构造函数，创建XSS防护的HTTP请求包装器
     *
     * @param request 原始HTTP请求对象
     * @throws IOException 读取请求体时可能抛出的IO异常
     */
    public XssHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // 缓存请求体
        body = request.getInputStream().readAllBytes();
    }

    /**
     * 获取经过XSS过滤的请求参数值
     *
     * @param name 参数名称
     * @return 经过XSS清理的参数值
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return XssUtils.clean(value);
    }

    /**
     * 获取经过XSS过滤的请求参数值数组
     *
     * @param name 参数名称
     * @return 经过XSS清理的参数值数组
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) return null;

        String[] cleanValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cleanValues[i] = XssUtils.clean(values[i]);
        }
        return cleanValues;
    }

    /**
     * 获取经过XSS过滤的请求头值
     *
     * @param name 请求头名称
     * @return 经过XSS清理的请求头值
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return XssUtils.clean(value);
    }

    /**
     * 获取经过XSS过滤的输入流
     *
     * @return 经过XSS清理的Servlet输入流
     * @throws IOException IO异常
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 过滤请求体中的 XSS
        String bodyStr = new String(body, StandardCharsets.UTF_8);
        String cleanBody = XssUtils.clean(bodyStr);
        byte[] cleanBytes = cleanBody.getBytes(StandardCharsets.UTF_8);

        ByteArrayInputStream bais = new ByteArrayInputStream(cleanBytes);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return bais.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                // 不支持异步
            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }

    /**
     * 获取经过XSS过滤的字符读取器
     *
     * @return 经过XSS清理的字符读取器
     * @throws IOException IO异常
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    /**
     * 获取请求体内容长度
     *
     * @return 请求体内容长度
     */
    @Override
    public int getContentLength() {
        return body.length;
    }

    /**
     * 获取请求体内容长度（长整型）
     *
     * @return 请求体内容长度
     */
    @Override
    public long getContentLengthLong() {
        return body.length;
    }
}

