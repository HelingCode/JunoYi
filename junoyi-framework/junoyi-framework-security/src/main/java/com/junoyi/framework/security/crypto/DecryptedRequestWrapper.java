package com.junoyi.framework.security.crypto;

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
 * 解密请求包装器
 * 用于包装解密后的请求体，使其可以被多次读取
 *
 * @author Fan
 */
public class DecryptedRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public DecryptedRequestWrapper(HttpServletRequest request, String decryptedBody) {
        super(request);
        this.body = decryptedBody != null ? decryptedBody.getBytes(StandardCharsets.UTF_8) : new byte[0];
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // 不需要实现
            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public int getContentLength() {
        return body.length;
    }

    @Override
    public long getContentLengthLong() {
        return body.length;
    }

    /**
     * 获取解密后的请求体
     */
    public String getBody() {
        return new String(body, StandardCharsets.UTF_8);
    }
}
