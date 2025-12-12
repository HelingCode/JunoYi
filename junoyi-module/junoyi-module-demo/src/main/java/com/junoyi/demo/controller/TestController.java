package com.junoyi.demo.controller;

import com.junoyi.demo.event.TestEvent;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class TestController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(TestController.class);

    @GetMapping("/event")
    public void testEvent(){
        EventBus.get().callEvent(new TestEvent("测试事件"));
    }
}