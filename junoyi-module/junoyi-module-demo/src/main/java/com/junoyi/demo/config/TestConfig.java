package com.junoyi.demo.config;

import com.junoyi.demo.listener.Test2EventListener;
import com.junoyi.demo.listener.TestEventListener;
import com.junoyi.framework.event.core.EventBus;
import org.springframework.stereotype.Component;

@Component
public class TestConfig {
    public TestConfig(){
        // 手动注册事件监听器
        EventBus.get().registerListener(new TestEventListener());
        EventBus.get().registerListener(new Test2EventListener());
        System.out.println("事件监听器已经注册");
    }
}