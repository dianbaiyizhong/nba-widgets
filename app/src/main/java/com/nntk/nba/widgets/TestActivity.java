package com.nntk.nba.widgets;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.SPStaticUtils;
import com.nntk.nba.widgets.constant.SettingConst;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

public class TestActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 设置logger的tag
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("nba-widget-log")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        super.onCreate(savedInstanceState);
        // 布局延伸
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.test);

        createDeskTopWidget("76ers");

    }

    @Override
    protected void onStart() {
        super.onStart();

        createDeskTopWidget("76ers");
    }

    private void createDeskTopWidget(String teamName) {



        ComponentName serviceComponent = new ComponentName(getApplication(), ScoreBoardWidget.class);
        SPStaticUtils.put("teamName", teamName);
        SPStaticUtils.put("movieType", SPStaticUtils.getString(SettingConst.MOVIE_TYPE, "2015"));

        AppWidgetManager.getInstance(getApplicationContext())
                .requestPinAppWidget(serviceComponent, null, null);
    }


}
