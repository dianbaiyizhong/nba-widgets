package com.nntk.nba.widgets;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.transition.MaterialSharedAxis;
import com.nntk.nba.widgets.adapter.NbaLogoAdapter;
import com.nntk.nba.widgets.animation.CustomAnimation1;
import com.nntk.nba.widgets.animation.CustomAnimation2;
import com.nntk.nba.widgets.animation.CustomAnimation3;
import com.nntk.nba.widgets.constant.SettingConst;
import com.nntk.nba.widgets.entity.TeamEntity;
import com.nntk.nba.widgets.util.GridDividerDecoration;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NbaLogoAdapter nbaLogoAdapter;
    private List<TeamEntity> teamEntityList = new ArrayList<>();


    private PlayerView playerView;

    private ImageButton preferencesButton;

    private ConstraintLayout headerContainer;

    private Transition openSearchViewTransition;
    private Transition closeSearchViewTransition;
    private ImageButton searchButton;


    private RecyclerView recyclerView;
    private SearchView searchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 设置logger的tag


        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("nba-widget-log")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        super.onCreate(savedInstanceState);

        if (ObjectUtils.isEmpty(SPStaticUtils.getString(SettingConst.MOVIE_TYPE))) {
            SPStaticUtils.put(SettingConst.MOVIE_TYPE, "nba2k15");
        }
        if (ObjectUtils.isEmpty(SPStaticUtils.getString(SettingConst.SCORE_BOARD_TYPE))) {
            SPStaticUtils.put(SettingConst.SCORE_BOARD_TYPE, "nba2d");
        }
        if (ObjectUtils.isEmpty(SPStaticUtils.getString(SettingConst.LOVE_TEAM))) {
            SPStaticUtils.put(SettingConst.LOVE_TEAM, "rockets");
        }
        if (ObjectUtils.isEmpty(SPStaticUtils.getInt(SettingConst.LIST_TYPE))) {
            SPStaticUtils.put(SettingConst.LIST_TYPE, 2);
        }


        // 布局延伸
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);

        // 初始化头部视频
        initVideo();


        preferencesButton = findViewById(R.id.preferences_button);
        searchButton = findViewById(R.id.cat_toc_search_button);

        headerContainer = findViewById(R.id.cat_toc_header_container);
        searchView = findViewById(R.id.cat_toc_search_view);

        initSearchViewTransitions();
        initSearchView();
        ImageButton gameWidgetButton = findViewById(R.id.btn_game_widget);

        ImageButton listTypeButton = findViewById(R.id.list_type_button);


        listTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type = SPStaticUtils.getInt(SettingConst.LIST_TYPE);

                if (type == 2) {
                    type = 3;
                } else if (type == 3) {
                    type = 1;
                } else if (type == 1) {
                    type = 2;
                }

                initRecyclerView(type);
                SPStaticUtils.put(SettingConst.LIST_TYPE, type);
            }
        });


        gameWidgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGameWidget();
            }
        });

        preferencesButton.setOnClickListener(v -> new PreferencesDialogFragment()
                .show(getSupportFragmentManager(), ""));


        searchButton.setOnClickListener(v -> openSearchView());


        String json = ResourceUtils.readRaw2String(R.raw.logo);

        JSONArray objects = JSON.parseArray(json);
        for (int i = 0; i < objects.size(); i++) {
            teamEntityList.add(TeamEntity.builder()
                    .simpleFrameSize(objects.getJSONObject(i).getInteger("simpleFrameSize"))
                    .teamName(objects.getJSONObject(i).getString("teamName"))
                    .teamNameZh(objects.getJSONObject(i).getString("teamNameZh"))
                    .bgColor(objects.getJSONObject(i).getString("bgColor"))
                    .city(objects.getJSONObject(i).getString("city"))
                    .build());
        }
        recyclerView = findViewById(R.id.rv);

        initRecyclerView(SPStaticUtils.getInt(SettingConst.LIST_TYPE));


    }


    private void initRecyclerView(int layoutType) {
        if (recyclerView.getAdapter() != null) {
            // 销毁gif
            int itemCount = recyclerView.getAdapter().getItemCount();
            for (int i = 0; i < itemCount; i++) {
                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    View itemView = holder.itemView;
                    ImageView imageView = itemView.findViewById(R.id.image);
                    ((Animatable) imageView.getDrawable()).stop();
                }
            }
        }


        for (int i = 0; i < recyclerView.getItemDecorationCount(); i++) {
            recyclerView.removeItemDecorationAt(i);
        }

        if (layoutType == 1) {
            recyclerView.addItemDecoration(
                    new GridDividerDecoration(
                            ConvertUtils.dp2px(1),
                            ContextCompat.getColor(this, R.color.cat_toc_status_wip_background_color),
                            2));
        }
        if (layoutType != 3) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(
                    new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        }
        nbaLogoAdapter = new NbaLogoAdapter(teamEntityList, layoutType);
        nbaLogoAdapter.setAdapterAnimation(new CustomAnimation3());
        nbaLogoAdapter.setAnimationFirstOnly(false);
        recyclerView.setAdapter(nbaLogoAdapter);

        nbaLogoAdapter.setNewInstance(teamEntityList);

        nbaLogoAdapter.setOnItemClickListener((adapter, view, position) -> {
            TeamEntity teamEntity = (TeamEntity) adapter.getData().get(position);


            new MaterialAlertDialogBuilder(this)
                    .setTitle("请选择")
                    .setPositiveButton("设置为比分关注球队", (dialog, which) -> {
                        SPStaticUtils.put(SettingConst.LOVE_TEAM, teamEntity.getTeamName());
                    })
                    .setNegativeButton("在桌面设置一个时钟", (dialog, which) -> {
                        createDeskTopWidget(teamEntity.getTeamName());
                    })
                    .setNeutralButton("取消", null)
                    .show();


        });

    }


    private void createDeskTopWidget(String teamName) {
        ComponentName serviceComponent = new ComponentName(getApplication(), NbaMapWidget.class);
        SPStaticUtils.put("teamName", teamName);
        SPStaticUtils.put("movieType", SPStaticUtils.getString(SettingConst.MOVIE_TYPE));

        AppWidgetManager.getInstance(getApplicationContext())
                .requestPinAppWidget(serviceComponent, null, null);
    }


    private void initVideo() {
        playerView = findViewById(R.id.bg_player);
        VideoPlayer.initVideo(this, playerView);

    }


    private void createGameWidget() {


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(getApplication(), ScoreBoardWidget.class));
        if (ids.length > 0) {
            Toast.makeText(getApplicationContext(), "你当前已经部署过这个插件，请到你的桌面仔细看看", Toast.LENGTH_LONG).show();
            return;
        }

        ComponentName serviceComponent = new ComponentName(getApplication(), ScoreBoardWidget.class);
        AppWidgetManager.getInstance(getApplicationContext())
                .requestPinAppWidget(serviceComponent, null, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        playerView.getPlayer().pause();
    }


    @Override
    protected void onResume() {
        playerView.getPlayer().play();
        super.onResume();
    }


    private void openSearchView() {
        TransitionManager.beginDelayedTransition(headerContainer, openSearchViewTransition);

        headerContainer.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);

        searchView.requestFocus();
    }

    private void initSearchViewTransitions() {
        openSearchViewTransition = createSearchViewTransition(true);
        closeSearchViewTransition = createSearchViewTransition(false);
    }

    @NonNull
    private MaterialSharedAxis createSearchViewTransition(boolean entering) {
        MaterialSharedAxis sharedAxisTransition =
                new MaterialSharedAxis(MaterialSharedAxis.X, entering);

        sharedAxisTransition.addTarget(headerContainer);
        sharedAxisTransition.addTarget(searchView);
        return sharedAxisTransition;
    }

    private void initSearchView() {
        searchView.setOnClickListener(v -> closeSearchView());

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
//                        tocAdapter.getFilter().filter(newText);
                        return false;
                    }
                });
    }


    private void closeSearchView() {
        TransitionManager.beginDelayedTransition(headerContainer, closeSearchViewTransition);

        headerContainer.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.GONE);

        clearSearchView();
    }

    private void clearSearchView() {
        if (searchView != null) {
            searchView.setQuery("", true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearSearchView();

    }
}
