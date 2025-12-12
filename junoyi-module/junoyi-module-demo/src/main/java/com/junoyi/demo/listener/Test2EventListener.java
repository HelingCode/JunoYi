package com.junoyi.demo.listener;

import com.junoyi.demo.event.TestEvent;
import com.junoyi.framework.event.annotation.EventHandler;
import com.junoyi.framework.event.core.Listener;
import com.junoyi.framework.event.enums.EventPriority;

public class Test2EventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTestEven32t(TestEvent event){
        System.out.println("优先级 最高");
    }
}