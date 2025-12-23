package com.junoyi.framework.web.xss;

import com.junoyi.framework.web.properties.XssProperties;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * XSS 防护的 HTTP 请求包装器
 *
 * @author Fan
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;
    private final XssProperties xssProperties;

    public XssHttpServletRequestWrapper(HttpServletRequest request, XssProperties xssProperties) throws IOException {
        super(request);
        this.xssProperties = xssProperties;
        // 缓存请求体
        if (xssProperties.isFilterBody()) {
            this.body = request.getInputStream().readAllBytes();
        } else {
            this.body = null;
        }
    }

    @Override
    public String getParameter(String name) {
        if (!xssProperties.isFilterParameter()) return super.getParameter(name);
        String value = super.getParameter(name);
        return filterValue(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        if (!xssProperties.isFilterParameter()) return super.getParameterValues(name);
        String[] values = super.getParameterValues(name);
        if (values == null) return null;

        String[] filteredValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            filteredValues[i] = filterValue(values[i]);
        }
        return filteredValues;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        if (!xssProperties.isFilterParameter()) return super.getParameterMap();
        Map<String, String[]> originalMap = super.getParameterMap();
        Map<String, String[]> filteredMap = new HashMap<>(originalMap.size());

        for (Map.Entry<String, String[]> entry : originalMap.entrySet()) {
            String[] values = entry.getValue();
            if (values != null) {
                String[] filteredValues = new String[values.length];
                for (int i = 0; i < values.length; i++) {
                    filteredValues[i] = filterValue(values[i]);
                }
                filteredMap.put(entry.getKey(), filteredValues);
            } else {
                filteredMap.put(entry.getKey(), null);
            }
        }
        return filteredMap;
    }

    @Override
    public String getHeader(String name) {
        if (!xssProperties.isFilterHeader()) return super.getHeader(name);
        String value = super.getHeader(name);
        return filterValue(value);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (!xssProperties.isFilterBody() || body == null) return super.getInputStream();

        // 过滤请求体中的 XSS
        String bodyStr = new String(body, StandardCharsets.UTF_8);
        String filteredBody = filterValue(bodyStr);
        byte[] filteredBytes = filteredBody.getBytes(StandardCharsets.UTF_8);

        ByteArrayInputStream bais = new ByteArrayInputStream(filteredBytes);
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

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public int getContentLength() {
        if (body == null) return super.getContentLength();
        return body.length;
    }

    @Override
    public long getContentLengthLong() {
        if (body == null) return super.getContentLengthLong();
        return body.length;
    }

    /**
     * 根据配置的模式过滤值
     */
    private String filterValue(String value) {
        if (value == null) return null;

        return switch (xssProperties.getMode()) {
            case CLEAN -> XssUtils.clean(value);
            case ESCAPE -> XssUtils.escape(value);
            case REJECT -> value; // REJECT 模式在 Filter 中处理
        };
    }

    /**
     * 获取原始请求体（用于 REJECT 模式检测）
     */
    public byte[] getOriginalBody() {
        return body;
    }
}
