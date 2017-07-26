package com.example.anish.mapsdemo.models;

import java.util.List;

public class OpeningHours
{
    public boolean open_now ;
    public List<Object> weekday_text ;

    public boolean isOpen_now() {
        return open_now;
    }

    public void setOpen_now(boolean open_now) {
        this.open_now = open_now;
    }

    public List<Object> getWeekday_text() {
        return weekday_text;
    }

    public void setWeekday_text(List<Object> weekday_text) {
        this.weekday_text = weekday_text;
    }
}