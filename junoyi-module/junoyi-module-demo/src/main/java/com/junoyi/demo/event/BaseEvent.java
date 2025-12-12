package com.junoyi.demo.event;


import com.junoyi.framework.event.core.Event;
import lombok.Data;

@Data
public class BaseEvent implements Event {

    private String eventName;

    public BaseEvent(String eventName){
        this.eventName = eventName;
    }

    public BaseEvent(){
        this.eventName = this.getClass().getSimpleName();
    }
}