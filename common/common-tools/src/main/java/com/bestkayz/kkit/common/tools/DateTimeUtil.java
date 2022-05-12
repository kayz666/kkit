package com.bestkayz.kkit.common.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author: Kayz
 * @create: 2020-07-27
 **/
public class DateTimeUtil {

    public final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date formatTime(String time) throws ParseException {
        return format.parse(time);
    }

    public static String formatTime(Date time){
        return format.format(time);
    }

    public static Integer convertSchemeTime(){
        return convertSchemeTime(null);
    }

    public static Integer convertSchemeTime(Date time){
        Calendar cal = Calendar.getInstance();
        if (time != null) cal.setTime(time);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int mm = cal.get(Calendar.MINUTE);
        int ss = cal.get(Calendar.SECOND);
        return hour * 10000 + mm * 100 + ss;
    }


    public static Date getTodayStartTime(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        return cal.getTime();
    }

    public static Date getTodayEndTime(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);
        cal.set(Calendar.MILLISECOND,999);
        return cal.getTime();
    }
}
