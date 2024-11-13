package com.nntk.nba.widgets;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.RemoteViews;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.nntk.nba.widgets.constant.SettingConst;
import com.nntk.nba.widgets.entity.TeamEntity;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NbaMapWidget extends AppWidgetProvider {
    private static final String LOGO_CLICK = "LOGO_CLICK";
    private static final String MOVIE_CLICK = "MOVIE_CLICK";

    private static Map<Integer, String> appMap = new HashMap<>();

    @Override
    public void onEnabled(Context context) {
        Logger.i("onEnabled:%s", "onEnabled");
        WidgetNotification.scheduleWidgetUpdate(context, NbaMapWidget.class);
    }


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        WidgetNotification.clearWidgetUpdate(context, NbaMapWidget.class);

    }

    @Override
    public void onDisabled(Context context) {

        WidgetNotification.clearWidgetUpdate(context, NbaMapWidget.class);

    }


    @SuppressLint("ScheduleExactAlarm")
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Logger.i("onUpdate:%s", "onUpdate");

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {

            String teamName = SPStaticUtils.getString("teamName");
            changeSimpleLayout(context, teamName, appWidgetId);

            appMap.put(appWidgetId, teamName);
        }


    }


    private int getPlayTime(String teamName, String type) {

        String json = ResourceUtils.readRaw2String(R.raw.logo);
        JSONArray objects = JSON.parseArray(json);
        List<TeamEntity> teamEntityList = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            teamEntityList.add(TeamEntity.builder()
                    .teamName(objects.getJSONObject(i).getString("teamName"))
                    .movie2015FrameSize(objects.getJSONObject(i).getInteger("movie2015FrameSize"))
                    .movie2016FrameSize(objects.getJSONObject(i).getInteger("movie2016FrameSize"))
                    .build());
        }

        TeamEntity teamEntity = teamEntityList.stream().filter(teamEntity1 -> teamEntity1.getTeamName().equals(teamName)).findFirst().get();


        if (type.contains("15")) {
            return teamEntity.getMovie2015FrameSize() * 40;
        } else {
            return teamEntity.getMovie2016FrameSize() * 20;
        }
    }


    protected PendingIntent getPendingSelfIntent(Context context, String action, String teamName, int appWidgetId, String movieType) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action + teamName);
        intent.putExtra("teamName", teamName);
        intent.putExtra("movieType", movieType);
        intent.putExtra("playTime", getPlayTime(teamName, movieType));
        intent.putExtra("appId", appWidgetId);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private void doInEveryMin(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, NbaMapWidget.class));
        for (int i = 0; i < ids.length; i++) {
            int finalI = i;
            String teamName = appMap.get(ids[finalI]);
            int playTime = getPlayTime(teamName, SPStaticUtils.getString(SettingConst.MOVIE_TYPE));
            changeMovieLayout(context, appMap.get(ids[finalI]), ids[finalI]);
            new Handler().postDelayed(() -> changeSimpleLayout(context, appMap.get(ids[finalI]), ids[finalI]), playTime);
        }
        WidgetNotification.setNextOneMin(context, NbaMapWidget.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);


        Logger.i("onReceive:%s", intent.getAction());
        String teamName = intent.getStringExtra("teamName");
        int appId = intent.getIntExtra("appId", 0);
        if (Objects.equals(intent.getAction(), WidgetNotification.ACTION_AUTO_UPDATE)) {
            if (ScreenUtils.isScreenLock()) {
                Logger.i("由于当前处在锁屏，忽略步骤");
                WidgetNotification.setNextOneMin(context, NbaMapWidget.class);
                return;
            }
            doInEveryMin(context);
            return;
        }

        if (!Objects.requireNonNull(intent.getAction()).contains("CLICK")) {
            return;
        }


        if (Objects.requireNonNull(intent.getAction()).startsWith(LOGO_CLICK)) {
            changeMovieLayout(context, teamName, appId);

        } else if (Objects.requireNonNull(intent.getAction()).startsWith(MOVIE_CLICK)) {
            changeSimpleLayout(context, teamName, appId);
        }


    }


    private void changeMovieLayout(Context context, String teamName, int appId) {
        String movieType = SPStaticUtils.getString(SettingConst.MOVIE_TYPE);
        if (movieType.equals("nba2k15")) {
            movieType = "2015";
        } else {
            movieType = "2016";
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), ResourceUtils.getLayoutIdByName("movie_layout_" + movieType + "_" + teamName));
        remoteViews.setOnClickPendingIntent(ResourceUtils.getIdByName("vf_logo_player"), getPendingSelfIntent(context, MOVIE_CLICK, teamName, appId, movieType));
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(appId, remoteViews);
    }


    private void changeSimpleLayout(Context context, String teamName, int appId) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), ResourceUtils.getLayoutIdByName("widget_layout_" + teamName));
        remoteViews.setOnClickPendingIntent(ResourceUtils.getIdByName("vf_simple_logo"), getPendingSelfIntent(context, LOGO_CLICK, teamName, appId, SPStaticUtils.getString(SettingConst.MOVIE_TYPE)));
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(appId, remoteViews);

    }


}
