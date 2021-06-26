package com.example.passwordmanager;

import java.util.ArrayList;

public class EventAdd {
    public String time, content;


    public EventAdd(String time , String content) {
        this.time = time;
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }
}
