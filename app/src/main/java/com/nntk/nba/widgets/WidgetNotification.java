package com.nntk.nba.widgets;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

import java.util.Calendar;
import java.util.Date;

public class WidgetNotification {

    public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";

    private static int[] getActiveWidgetIds(Context context, Class clazz) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, clazz));
        return ids;
    }

    public static void scheduleWidgetUpdate(Context context, Class clazz) {

        if (getActiveWidgetIds(context, clazz) != null && getActiveWidgetIds(context, clazz).length > 0) {

            setNextOneMin(context, clazz);
        }
    }


    @SuppressLint("ScheduleExactAlarm")
    public static void setNextOneMin(Context context, Class clazz) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MINUTE, 1);
        Logger.i("alarm设置，下一个分钟的触发时间是%s", calendar.getTime());
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTime().getTime(), createClockTickIntent(context, clazz));
    }


    private static PendingIntent createClockTickIntent(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setAction(ACTION_AUTO_UPDATE);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public static void clearWidgetUpdate(Context context, Class clazz) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(createClockTickIntent(context, clazz));
    }
}
