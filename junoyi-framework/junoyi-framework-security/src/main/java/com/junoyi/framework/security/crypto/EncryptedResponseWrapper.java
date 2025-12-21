package com.junoyi.framework.security.crypto;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 加密响应包装器
 * 用于捕获响应内容，以便后续进行加密处理
 *
 * @author Fan
 */
public class EncryptedResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream buffer;
    private ServletOutputStream outputStream;
    private PrintWriter writer;
    private final HttpServletResponse originalResponse;

    public EncryptedResponseWrapper(HttpServletResponse response) {
        super(response);
        this.originalResponse = response;
        this.buffer = new ByteArrayOutputStream();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null)
            throw new IllegalStateException("getWriter() has already been called");
        if (outputStream == null)
            outputStream = new CachedServletOutputStream(buffer);
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputStream != null)
            throw new IllegalStateException("getOutputStream() has already been called");
        if (writer == null)
            writer = new PrintWriter(new OutputStreamWriter(buffer, StandardCharsets.UTF_8));
        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (writer != null)
            writer.flush();
        if (outputStream != null)
            outputStream.flush();
    }

    /**
     * 获取捕获的响应内容
     */
    public String getCapturedContent() throws IOException {
        flushBuffer();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    /**
     * 获取捕获的响应字节
     */
    public byte[] getCapturedBytes() throws IOException {
        flushBuffer();
        return buffer.toByteArray();
    }

    /**
     * 将加密后的内容写入原始响应
     */
    public void writeEncryptedContent(String encryptedContent) throws IOException {
        byte[] encryptedBytes = encryptedContent.getBytes(StandardCharsets.UTF_8);
        originalResponse.setContentLength(encryptedBytes.length);
        originalResponse.setContentType("application/json;charset=UTF-8");
        originalResponse.setHeader("X-Encrypted", "true");
        originalResponse.getOutputStream().write(encryptedBytes);
        originalResponse.getOutputStream().flush();
    }

    /**
     * 缓存输出流
     */
    private static class CachedServletOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream buffer;

        public CachedServletOutputStream(ByteArrayOutputStream buffer) {
            this.buffer = buffer;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            // 不需要实现
        }

        @Override
        public void write(int b) throws IOException {
            buffer.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            buffer.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            buffer.write(b, off, len);
        }
    }
}
