package com.augmentum.minote.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static String getTime(long milliseconds) {

        // 12 hours.System.currentTimeMillis()
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String formatted = simpleDateFormat.format(new Date(milliseconds));
        return formatted;
    }

    public static String getYearAndDate(long milliseconds) {

        // 12 hours.System.currentTimeMillis()
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String formatted = simpleDateFormat.format(new Date(milliseconds));
        return formatted;
    }

    public static String getMonthAndTime(long milliseconds) {

        // 12 hours.System.currentTimeMillis()
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日 HH:mm");
        String formatted = simpleDateFormat.format(new Date(milliseconds));
        return formatted;
    }

    public static String getWeek(long milliseconds) {

        // 12 hours.System.currentTimeMillis()
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
        String formatted = simpleDateFormat.format(new Date(milliseconds));
        return formatted;
    }

    public static String getDateAndWeek(long milliseconds) {

        // 12 hours.System.currentTimeMillis()
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 EEEE");
        String formatted = simpleDateFormat.format(new Date(milliseconds));
        return formatted;
    }

    public static long getMillisecond(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute) {

        Date date = new Date(year, monthOfYear, dayOfMonth, hourOfDay, minute);
        return date.getTime();
    }

    public static String whenRemind(long milliseconds) {

        Date date = new Date(milliseconds);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DAY_OF_YEAR);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minite = c.get(Calendar.MINUTE);
        if (day > 1) {
            return day + "天";
        } else if (hour > 0) {
            return hour+"小时";
        } else {
            return (minite+1)+"分";
        }
    }
}
