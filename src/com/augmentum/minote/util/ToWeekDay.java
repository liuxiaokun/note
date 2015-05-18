package com.augmentum.minote.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ToWeekDay {

    public static String toWeekDay(int year, int monthOfYear, int dayOfMonth) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyƒÍMM‘¬dd»’ EEEE");
        String s = simpleDateFormat.format(calendar.getTime());
        
        return s;
    }
}
