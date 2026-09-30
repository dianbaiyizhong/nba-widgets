package com.nntk.nba.widgets;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.SPStaticUtils;
import com.nntk.nba.widgets.constant.SettingConst;
import com.orhanobut.logger.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class WidgetNotification {

    public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";
    public static final String ACTION_RESTORE_SIMPLE = "RESTORE_SIMPLE";

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
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.set(Calendar.SECOND, 0);
//        calendar.add(Calendar.MINUTE, 1);

        // 获取当前时间
        Calendar calendar = Calendar.getInstance();

        // 当前时间的秒数
        int currentSecond = calendar.get(Calendar.SECOND);

        // 判断是否当前时间小于30秒
        if (currentSecond < 30) {
            // 设置当前分钟的第59秒
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 0); // 清除毫秒，确保精准
        } else {
            // 设置下一分钟的第59秒
            calendar.add(Calendar.MINUTE, 1); // 加一分钟
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 0); // 清除毫秒，确保精准
        }
        Logger.i("alarm设置，下一个分钟的触发时间是%s", calendar.getTime());
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTime().getTime(), createClockTickIntent(context, clazz));
    }


    private static PendingIntent createClockTickIntent(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setAction(ACTION_AUTO_UPDATE);
        SPStaticUtils.put(SettingConst.LAST_PLAT_TIME, System.currentTimeMillis());
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE);
    }


    public static void clearWidgetUpdate(Context context, Class clazz) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(createClockTickIntent(context, clazz));
    }

    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleRestoreSimple(Context context, Class clazz, int playTime, String teamName, int appWidgetId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = createRestoreSimpleIntent(context, clazz, teamName, appWidgetId);
        alarmManager.cancel(pendingIntent);
        Logger.i("animation播放结束，回到静态布局的时间是%s", new Date(System.currentTimeMillis() + playTime));
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + playTime, pendingIntent);
    }

    public static void clearRestoreSimple(Context context, Class clazz, int appWidgetId) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(createRestoreSimpleIntent(context, clazz, null, appWidgetId));
    }

    private static PendingIntent createRestoreSimpleIntent(Context context, Class clazz, String teamName, int appWidgetId) {
        Intent intent = new Intent(context, clazz);
        intent.setAction(ACTION_RESTORE_SIMPLE);
        intent.putExtra("teamName", teamName);
        intent.putExtra("appId", appWidgetId);
        return PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_MUTABLE);
    }
}
