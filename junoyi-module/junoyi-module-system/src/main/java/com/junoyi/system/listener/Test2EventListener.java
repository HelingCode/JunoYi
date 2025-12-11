package com.junoyi.system.listener;

import com.junoyi.framework.event.annotation.EventHandler;
import com.junoyi.framework.event.core.Listener;
import com.junoyi.system.event.Test2Event;
import com.junoyi.system.event.TestEvent;

public class Test2EventListener implements Listener {

    @EventHandler
    public void onTestEvent(TestEvent event){

        System.out.println("测试事件被触发 2");
    }
}