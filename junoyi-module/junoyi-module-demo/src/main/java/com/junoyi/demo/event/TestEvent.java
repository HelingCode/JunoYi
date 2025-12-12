package com.junoyi.demo.event;

import lombok.Data;

/**
 * 自定义Test事件
 *
 * @author Fan
 */
public class TestEvent extends BaseEvent{

    private String test;


    public TestEvent(String test){
        super();
        this.test = test;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test){
        this.test = test;
    }
}