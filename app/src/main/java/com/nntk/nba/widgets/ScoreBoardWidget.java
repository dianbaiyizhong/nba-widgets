package com.nntk.nba.widgets;

import static com.nntk.nba.widgets.constant.SettingConst.CURRENT_INFO;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.RemoteViews;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.VolumeUtils;
import com.nntk.nba.widgets.constant.SettingConst;
import com.nntk.nba.widgets.entity.CurrentInfo;
import com.nntk.nba.widgets.entity.GameInfo;
import com.nntk.nba.widgets.entity.TeamEntity;
import com.nntk.nba.widgets.util.BatteryHelper;
import com.orhanobut.logger.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Implementation of App Widget functionality.
 */
public class ScoreBoardWidget extends AppWidgetProvider {
    private static final String LOGO_CLICK = "LOGO_CLICK";
    public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";


    public static final String ACTION_LAUNCH_APP = "ACTION_LAUNCH_APP";

    private static List<TeamEntity> teamEntityListCache = new ArrayList<>();


    private static List<TeamEntity> teamEntityList() {

        if (teamEntityListCache.isEmpty()) {
            String json = ResourceUtils.readRaw2String(R.raw.logo);
            JSONArray objects = JSON.parseArray(json);
            for (int i = 0; i < objects.size(); i++) {
                teamEntityListCache.add(TeamEntity.builder()
                        .simpleFrameSize(objects.getJSONObject(i).getInteger("simpleFrameSize"))
                        .teamName(objects.getJSONObject(i).getString("teamName"))
                        .teamNameZh(objects.getJSONObject(i).getString("teamNameZh"))
                        .bgColor(objects.getJSONObject(i).getString("bgColor"))
                        .scoreBoardColor(objects.getJSONObject(i).getString("scoreBoardColor"))
                        .build());
            }
        }
        return teamEntityListCache;

    }


    private static CurrentInfo getCurrentInfo() {
        if (ObjectUtils.isEmpty(SPStaticUtils.getString(SettingConst.CURRENT_INFO))) {
            return new CurrentInfo();
        }

        return JSON.parseObject(SPStaticUtils.getString(CURRENT_INFO), CurrentInfo.class);
    }

    private static void setCurrentInfo(TeamEntity hourEntity, TeamEntity minEntity) {
        if (hourEntity != null) {
            CurrentInfo currentInfo = getCurrentInfo();
            currentInfo
                    .setCurrentHourTeam(hourEntity);
            SPStaticUtils.put(CURRENT_INFO, JSON.toJSONString(currentInfo));
        }

        if (minEntity != null) {
            CurrentInfo currentInfo = getCurrentInfo();
            currentInfo
                    .setCurrentMinTeam(hourEntity);
            SPStaticUtils.put(CURRENT_INFO, JSON.toJSONString(currentInfo));
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId) {

        TeamEntity hourEntity = teamEntityList().get(new Random().nextInt(teamEntityList().size()));
        TeamEntity minEntity = teamEntityList().get(new Random().nextInt(teamEntityList().size()));


        setCurrentInfo(hourEntity, minEntity);

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

            if ((System.currentTimeMillis() - SPStaticUtils.getLong(SettingConst.LAST_PLAT_TIME)) > 1000 * 120) {
                Logger.i("由于相隔时间相差较大，取消执行");
                WidgetNotification.setNextOneMin(context, ScoreBoardWidget.class);
                return;
            }
            doInEveryMin(context);

        }


        if (Objects.requireNonNull(intent.getAction()).contains("CLICK")) {

            ThreadUtils.getIoPool().execute(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .get()
                            .url("https://nba.hupu.com/games")
                            .build();
                    Call call = client.newCall(request);
                    try {
                        //同步发送请求
                        Response response = call.execute();
                        if (response.isSuccessful()) {
                            String html = response.body().string();

                            Elements cardTeams = Jsoup.parse(html).select("div.gamecenter_content   div.list_box  div.team_vs_a");
                            List<GameInfo> gameInfoList = new ArrayList<>();
                            for (Element card : cardTeams) {


                                Elements game = card.select("div.txt");
                                String guestTeam = null;
                                String guestRate = null;
                                String homaTeam = null;
                                String homaRate = null;


                                if (game.get(0).select("span").size() == 1) {
                                    guestTeam = game.get(0).select("span").get(0).text();
                                    guestRate = "-";
                                } else {
                                    guestTeam = game.get(0).select("span").get(1).text();
                                    guestRate = game.get(0).select("span").get(0).text();
                                }
                                if (game.get(1).select("span").size() == 1) {
                                    homaTeam = game.get(1).select("span").get(0).text();
                                    homaRate = "-";
                                } else {
                                    homaTeam = game.get(1).select("span").get(1).text();
                                    homaRate = game.get(1).select("span").get(0).text();
                                }

                                gameInfoList.add(GameInfo.builder()
                                        .guestRate(guestRate)
                                        .guestTeam(guestTeam)
                                        .homeRate(homaRate)
                                        .homeTeam(homaTeam)
                                        .build());
                            }
                            String loveTeam = SPStaticUtils.getString(SettingConst.LOVE_TEAM);
                            TeamEntity teamEntity = teamEntityList().stream().filter(new Predicate<TeamEntity>() {
                                @Override
                                public boolean test(TeamEntity teamEntity) {
                                    return teamEntity.getTeamName().contains(loveTeam);
                                }
                            }).findFirst().get();


                            GameInfo gameInfo = gameInfoList.stream().filter(new Predicate<GameInfo>() {
                                @Override
                                public boolean test(GameInfo gameInfo) {
                                    return gameInfo.getGuestTeam().equals(teamEntity.getTeamNameZh()) || gameInfo.getHomeTeam().equals(teamEntity.getTeamNameZh());
                                }
                            }).findFirst().get();


                            gameInfo.setGuestTeamEntity(teamEntityList().stream().filter(new Predicate<TeamEntity>() {
                                @Override
                                public boolean test(TeamEntity teamEntity) {
                                    return teamEntity.getTeamNameZh().equals(gameInfo.getGuestTeam());
                                }
                            }).findFirst().get());

                            gameInfo.setHomeTeamEntity(teamEntityList().stream().filter(new Predicate<TeamEntity>() {
                                @Override
                                public boolean test(TeamEntity teamEntity) {
                                    return teamEntity.getTeamNameZh().equals(gameInfo.getHomeTeam());
                                }
                            }).findFirst().get());


                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    changeGameAnimLayout(context, gameInfo);
                                }
                            });

                        } else {
                            System.out.println("请求失败");
                        }
                    } catch (IOException e) {
                        System.out.println("error");
                        e.printStackTrace();
                    }
                }
            });


        }

    }

    private static boolean isOnTheHour(LocalTime time) {
        return (time.getMinute() == 59 && time.getSecond() > 50) || (time.getMinute() == 0 && time.getSecond() < 10);
    }


    private void loadBatteryView(Context context, RemoteViews remoteViews) {

        int batteryLevel = BatteryHelper.getBatteryLevel(context);

        if (batteryLevel > 0) {
            remoteViews.setInt(R.id.battery_01, "setBackgroundColor", ColorUtils.string2Int("#FEFDFE"));
        }
        if (batteryLevel > 14) {
            remoteViews.setInt(R.id.battery_02, "setBackgroundColor", ColorUtils.string2Int("#FEFDFE"));
        }
        if (batteryLevel > 28) {
            remoteViews.setInt(R.id.battery_03, "setBackgroundColor", ColorUtils.string2Int("#FEFDFE"));
        }
        if (batteryLevel > 45) {
            remoteViews.setInt(R.id.battery_04, "setBackgroundColor", ColorUtils.string2Int("#FEFDFE"));
        }
        if (batteryLevel > 56) {
            remoteViews.setInt(R.id.battery_05, "setBackgroundColor", ColorUtils.string2Int("#FEFDFE"));
        }
        if (batteryLevel > 70) {
            remoteViews.setInt(R.id.battery_06, "setBackgroundColor", ColorUtils.string2Int("#FEFDFE"));
        }
        if (batteryLevel > 84) {
            remoteViews.setInt(R.id.battery_07, "setBackgroundColor", ColorUtils.string2Int("#FEFDFE"));
        }


        int maxVolume = VolumeUtils.getMaxVolume(10);
        float floor = maxVolume / 8f;
        int currentVolume = VolumeUtils.getVolume(10);


        if (currentVolume > floor) {
            remoteViews.setInt(R.id.wifi_01, "setBackgroundColor", ColorUtils.string2Int("#FEFDFE"));
        }

        if (currentVolume > 2 * floor) {
            remoteViews.setInt(R.id.wifi_02, "setBackgroundColor", ColorUtils.string2Int("#FEFDFE"));
        }
        if (currentVolume > 3 * floor) {
            remoteViews.setInt(R.id.wifi_03, "setBackgroundColor", ColorUtils.string2Int("#FEFDFE"));
        }
        if (currentVolume > 4 * floor) {
            remoteViews.setInt(R.id.wifi_04, "setBackgroundColor", ColorUtils.string2Int("#FEFDFE"));
        }
        if (currentVolume > 5 * floor) {
            remoteViews.setInt(R.id.wifi_05, "setBackgroundColor", ColorUtils.string2Int("#FEFDFE"));
        }
        if (currentVolume > 6 * floor) {
            remoteViews.setInt(R.id.wifi_06, "setBackgroundColor", ColorUtils.string2Int("#FEFDFE"));
        }

        if (currentVolume > 7 * floor) {
            remoteViews.setInt(R.id.wifi_07, "setBackgroundColor", ColorUtils.string2Int("#FEFDFE"));
        }


    }

    private void doInEveryMin(Context context) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, ScoreBoardWidget.class));

        for (int appId : ids) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.nba_scoreboard_anim_layout);
            TeamEntity minEntity = teamEntityList().get(getMinIndex());
            TeamEntity hourEntity;
            Logger.i("当前分配给min的球队:%s", minEntity.getTeamName());
            RemoteViews animViews = new RemoteViews(context.getPackageName(), ResourceUtils.getLayoutIdByName(String.format("espn_anim_layout_%s", minEntity.getTeamName())));
            remoteViews.addView(R.id.min_view_frame_layout, animViews);
            boolean changeHourView = isOnTheHour(LocalTime.now());
            if (changeHourView) {
                Logger.i("当前分配给hour的球队:%s", minEntity.getTeamName());
                hourEntity = teamEntityList().get(getHourIndex());
                RemoteViews animHourViews = new RemoteViews(context.getPackageName(), ResourceUtils.getLayoutIdByName(String.format("espn_anim_layout_%s", hourEntity.getTeamName())));
                remoteViews.addView(R.id.hour_view_frame_layout, animHourViews);
            } else {
                remoteViews.setInt(R.id.hour_view, "setBackgroundColor", ColorUtils.string2Int(getCurrentInfo().getCurrentHourTeam().getScoreBoardColor()));
                hourEntity = null;
            }
            loadBatteryView(context, remoteViews);
            appWidgetManager.updateAppWidget(appId, remoteViews);
            new Handler().postDelayed(() -> changeSimpleLayout(context, appId, minEntity, hourEntity), 1800);
        }
        WidgetNotification.setNextOneMin(context, ScoreBoardWidget.class);


    }


    private int getHourIndex() {
        int currentIndex = SPStaticUtils.getInt(SettingConst.CURRENT_HOUR_TEAM_INDEX, 0);
        if (currentIndex >= 29) {
            currentIndex = 0;
        } else {
            currentIndex = currentIndex + 1;
        }
        SPStaticUtils.put(SettingConst.CURRENT_HOUR_TEAM_INDEX, currentIndex);
        return currentIndex;
    }

    private int getMinIndex() {
        int currentIndex = SPStaticUtils.getInt(SettingConst.CURRENT_MIN_TEAM_INDEX, 0);
        if (currentIndex >= 29) {
            currentIndex = 0;
        } else {
            currentIndex = currentIndex + 1;
        }
        SPStaticUtils.put(SettingConst.CURRENT_MIN_TEAM_INDEX, currentIndex);
        return currentIndex;
    }


    protected void changeSimpleLayout(Context context, int appId, TeamEntity minTeamEntity, TeamEntity hourTeamEntity) {
        Logger.i("changeSimpleLayout");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.nba_scoreboard_origin_layout);
        if (hourTeamEntity != null) {
            remoteViews.setInt(R.id.hour_view, "setBackgroundColor", ColorUtils.string2Int(hourTeamEntity.getScoreBoardColor()));
            // 记下当前队伍
            setCurrentInfo(hourTeamEntity, null);
        } else {
            hourTeamEntity = getCurrentInfo().getCurrentHourTeam();
            remoteViews.setInt(R.id.hour_view, "setBackgroundColor", ColorUtils.string2Int(hourTeamEntity.getScoreBoardColor()));
        }

        if (minTeamEntity != null) {
            remoteViews.setInt(R.id.min_view, "setBackgroundColor", ColorUtils.string2Int(minTeamEntity.getScoreBoardColor()));
            // 记下当前队伍
            setCurrentInfo(null, minTeamEntity);

        } else {
            minTeamEntity = getCurrentInfo().getCurrentMinTeam();
            remoteViews.setInt(R.id.hour_view, "setBackgroundColor", ColorUtils.string2Int(minTeamEntity.getScoreBoardColor()));
        }
        loadBatteryView(context, remoteViews);
        remoteViews.setOnClickPendingIntent(ResourceUtils.getIdByName("btn_look_game"), getPendingSelfIntent(context, LOGO_CLICK, appId));

        try {
            remoteViews.setOnClickPendingIntent(ResourceUtils.getIdByName("btn_launch"), getPendingSelfIntent(context, ACTION_LAUNCH_APP, appId));
        } catch (Exception e) {
            e.printStackTrace();
        }

        appWidgetManager.updateAppWidget(appId, remoteViews);
    }

    protected void changeGameAnimLayout(Context context, GameInfo gameInfo) {


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, ScoreBoardWidget.class));

        for (int appId : ids) {
            // 先清除定时器
            WidgetNotification.clearWidgetUpdate(context, ScoreBoardWidget.class);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.nba_scoreboard_anim_layout);


            TeamEntity minEntity = gameInfo.getHomeTeamEntity();
            TeamEntity hourEntity = gameInfo.getGuestTeamEntity();


            RemoteViews animViews = new RemoteViews(context.getPackageName(), ResourceUtils.getLayoutIdByName(String.format("espn_anim_layout_%s", minEntity.getTeamName())));
            remoteViews.addView(R.id.min_view_frame_layout, animViews);
            RemoteViews animHourViews = new RemoteViews(context.getPackageName(), ResourceUtils.getLayoutIdByName(String.format("espn_anim_layout_%s", hourEntity.getTeamName())));
            remoteViews.addView(R.id.hour_view_frame_layout, animHourViews);
            loadBatteryView(context, remoteViews);
            appWidgetManager.updateAppWidget(appId, remoteViews);

            new Handler().postDelayed(() -> {
                changeGameLayout(context, appId, gameInfo);
            }, 1800);
        }


    }

    protected void changeGameLayout(Context context, int appId, GameInfo gameInfo) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.nba_scoreboard_game_layout);


        String scoreBoardType = SPStaticUtils.getString(SettingConst.SCORE_BOARD_TYPE);
        String logoPrefix = "logo_";
        if (scoreBoardType.contains("3")) {
            logoPrefix = "logo_3d_";
        }

        remoteViews.setImageViewResource(R.id.iv_min_logo, ResourceUtils.getMipmapIdByName(logoPrefix + gameInfo.getHomeTeamEntity().getTeamName()));
        remoteViews.setImageViewResource(R.id.iv_hour_logo, ResourceUtils.getMipmapIdByName(logoPrefix + gameInfo.getGuestTeamEntity().getTeamName()));
        remoteViews.setViewVisibility(R.id.iv_min_logo, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.iv_hour_logo, View.VISIBLE);

        remoteViews.setViewVisibility(R.id.tv_hour, View.GONE);
        remoteViews.setViewVisibility(R.id.tv_min, View.GONE);

        remoteViews.setViewVisibility(R.id.tc_hour, View.GONE);
        remoteViews.setViewVisibility(R.id.tc_min, View.GONE);

        remoteViews.setViewVisibility(R.id.tv_home_team_rate, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.tv_guest_team_rate, View.VISIBLE);

        remoteViews.setTextViewText(R.id.tv_home_team_rate, gameInfo.getHomeRate());
        remoteViews.setTextViewText(R.id.tv_guest_team_rate, gameInfo.getGuestRate());

        int guestDrawableId = ResourceUtils.getDrawableIdByName(String.format("gradient_%s_background", gameInfo.getGuestTeamEntity().getTeamName()));
        if (guestDrawableId != 0) {
            remoteViews.setInt(R.id.hour_view, "setBackgroundResource", guestDrawableId);
        } else {
            remoteViews.setInt(R.id.hour_view, "setBackgroundColor", ColorUtils.string2Int(gameInfo.getGuestTeamEntity().getScoreBoardColor()));
        }
        int homeDrawableId = ResourceUtils.getDrawableIdByName(String.format("gradient_%s_background", gameInfo.getHomeTeamEntity().getTeamName()));
        if (homeDrawableId != 0) {
            remoteViews.setInt(R.id.min_view, "setBackgroundResource", homeDrawableId);
        } else {
            remoteViews.setInt(R.id.min_view, "setBackgroundColor", ColorUtils.string2Int(gameInfo.getHomeTeamEntity().getScoreBoardColor()));
        }


        loadBatteryView(context, remoteViews);

        appWidgetManager.updateAppWidget(appId, remoteViews);
        // 2秒恢复原状态，并重新开启定时器
        new Handler().postDelayed(() -> {
            changeSimpleLayout(context, appId, gameInfo.getHomeTeamEntity(), gameInfo.getGuestTeamEntity());
            WidgetNotification.scheduleWidgetUpdate(context, ScoreBoardWidget.class);

        }, 3000);
    }


    protected PendingIntent getPendingSelfIntent(Context context, String action, int appWidgetId) {
        if (action.equals(ACTION_LAUNCH_APP)) {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.hupu.games");
            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);

        } else {
            Intent intent = new Intent(context, getClass());
            intent.setAction(action);
            intent.putExtra("appId", appWidgetId);
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

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