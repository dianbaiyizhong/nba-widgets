package com.nntk.nba.widgets;

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
import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.nntk.nba.widgets.entity.CurrentInfo;
import com.nntk.nba.widgets.entity.TeamEntity;
import com.orhanobut.logger.Logger;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class ScoreBoardWidget extends AppWidgetProvider {
    private static final String LOGO_CLICK = "LOGO_CLICK";
    public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";


    private static List<TeamEntity> teamEntityList = new ArrayList<>();


    private static Map<Integer, CurrentInfo> appMap = new HashMap<>();


    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId) {

        TeamEntity hourEntity = teamEntityList.get(new Random().nextInt(teamEntityList.size()));
        TeamEntity minEntity = teamEntityList.get(new Random().nextInt(teamEntityList.size()));

        appMap.put(appWidgetId, CurrentInfo.builder()
                .currentHourTeam(hourEntity)
                .currentMinTeam(minEntity)
                .build());

        Logger.i("当前随机到两个球队:%s,%s", hourEntity.getTeamName(), minEntity.getTeamName());
        changeSimpleLayout(context, appWidgetId, minEntity, hourEntity);


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Logger.i("onReceive:%s", intent.getAction());


        if (Objects.equals(intent.getAction(), ACTION_AUTO_UPDATE)) {
            if (ScreenUtils.isScreenLock()) {
                Logger.i("由于当前处在锁屏，忽略步骤");
                WidgetNotification.setNextOneMin(context, ScoreBoardWidget.class);
                return;
            }
            doInEveryMin(context);
        }


        if (Objects.requireNonNull(intent.getAction()).contains("CLICK")) {


        }

    }

    private static boolean isOnTheHour(LocalTime time) {
        return time.getMinute() == 59 && time.getSecond() < 10;

    }


    private void doInEveryMin(Context context) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, ScoreBoardWidget.class));

        for (int appId : ids) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.nba_scoreboard_anim_layout);
            TeamEntity minEntity = teamEntityList.get(new Random().nextInt(teamEntityList.size()));
            TeamEntity hourEntity;
            Logger.i("当前分配给min的球队:%s", minEntity.getTeamName());
            RemoteViews animViews = new RemoteViews(context.getPackageName(), ResourceUtils.getLayoutIdByName(String.format("espn_anim_layout_%s", minEntity.getTeamName())));
            remoteViews.addView(R.id.min_view_frame_layout, animViews);
            boolean changeHourView = isOnTheHour(LocalTime.now());
            if (changeHourView) {
                Logger.i("当前分配给hour的球队:%s", minEntity.getTeamName());
                hourEntity = teamEntityList.get(new Random().nextInt(teamEntityList.size()));
                RemoteViews animHourViews = new RemoteViews(context.getPackageName(), ResourceUtils.getLayoutIdByName(String.format("espn_anim_layout_%s", hourEntity.getTeamName())));
                remoteViews.addView(R.id.hour_view_frame_layout, animHourViews);
            } else {
                remoteViews.setInt(R.id.hour_view, "setBackgroundColor", ColorUtils.string2Int(appMap.get(appId).getCurrentHourTeam().getScoreBoardColor()));
                hourEntity = null;
            }
            appWidgetManager.updateAppWidget(appId, remoteViews);
            new Handler().postDelayed(() -> changeSimpleLayout(context, appId, minEntity, hourEntity), 2000);
        }
        WidgetNotification.setNextOneMin(context, ScoreBoardWidget.class);

    }


    protected void changeSimpleLayout(Context context, int appId, TeamEntity minTeamEntity, TeamEntity hourTeamEntity) {
        Logger.i("changeSimpleLayout");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.nba_scoreboard_origin_layout);
        if (hourTeamEntity != null) {
            remoteViews.setInt(R.id.hour_view, "setBackgroundColor", ColorUtils.string2Int(hourTeamEntity.getScoreBoardColor()));
            // 记下当前队伍
            appMap.get(appId).setCurrentHourTeam(hourTeamEntity);
        } else {
            hourTeamEntity = appMap.get(appId).getCurrentHourTeam();
            remoteViews.setInt(R.id.hour_view, "setBackgroundColor", ColorUtils.string2Int(hourTeamEntity.getScoreBoardColor()));
        }
        remoteViews.setInt(R.id.min_view, "setBackgroundColor", ColorUtils.string2Int(minTeamEntity.getScoreBoardColor()));
        remoteViews.setOnClickPendingIntent(ResourceUtils.getIdByName("btn"), getPendingSelfIntent(context, LOGO_CLICK, appId));
        appWidgetManager.updateAppWidget(appId, remoteViews);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action, int appWidgetId) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        intent.putExtra("appId", appWidgetId);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        if (teamEntityList.isEmpty()) {
            String json = ResourceUtils.readRaw2String(R.raw.logo);
            JSONArray objects = JSON.parseArray(json);
            for (int i = 0; i < objects.size(); i++) {
                teamEntityList.add(TeamEntity.builder()
                        .simpleFrameSize(objects.getJSONObject(i).getInteger("simpleFrameSize"))
                        .teamName(objects.getJSONObject(i).getString("teamName"))
                        .teamNameZh(objects.getJSONObject(i).getString("teamNameZh"))
                        .bgColor(objects.getJSONObject(i).getString("bgColor"))
                        .scoreBoardColor(objects.getJSONObject(i).getString("scoreBoardColor"))
                        .build());
            }
        }


        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // 如果onEnable不触发，那就卸载重装
        super.onEnabled(context);
        Logger.i("onEnabled:%s", "onEnabled");
        WidgetNotification.scheduleWidgetUpdate(context, ScoreBoardWidget.class);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Logger.i("onDeleted");
        WidgetNotification.clearWidgetUpdate(context, ScoreBoardWidget.class);
        super.onDeleted(context, appWidgetIds);


    }

    @Override
    public void onDisabled(Context context) {
        Logger.i("onDisabled");
        WidgetNotification.clearWidgetUpdate(context, ScoreBoardWidget.class);
        super.onDisabled(context);

    }

}