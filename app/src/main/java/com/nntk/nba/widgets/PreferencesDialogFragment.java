package com.nntk.nba.widgets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nntk.nba.widgets.constant.SettingConst;
import com.nntk.nba.widgets.entity.TeamEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PreferencesDialogFragment extends BottomSheetDialogFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View container = layoutInflater.inflate(R.layout.preferences_dialog, viewGroup, false);
        LinearLayout preferencesLayout = container.findViewById(R.id.preferences_layout);


        RadioGroup radioGroup = container.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton selectedRadioButton = container.findViewById(checkedId);
                SPStaticUtils.put(SettingConst.MOVIE_TYPE, selectedRadioButton.getTag().toString());
            }
        });


        radioGroup.check(ResourceUtils.getIdByName(SPStaticUtils.getString(SettingConst.MOVIE_TYPE)));




        RadioGroup radioGroup2 = container.findViewById(R.id.radio_group_2);
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton selectedRadioButton = container.findViewById(checkedId);
                SPStaticUtils.put(SettingConst.SCORE_BOARD_TYPE, selectedRadioButton.getTag().toString());
            }
        });


        radioGroup2.check(ResourceUtils.getIdByName(SPStaticUtils.getString(SettingConst.SCORE_BOARD_TYPE)));






        Spinner spinner = container.findViewById(R.id.sp_team);

        String json = ResourceUtils.readRaw2String(R.raw.logo);

        List<TeamEntity> teamEntityList = new ArrayList<>();
        List<String> datalist = new ArrayList<>();

        JSONArray objects = JSON.parseArray(json);


        for (int i = 0; i < objects.size(); i++) {
            teamEntityList.add(TeamEntity.builder()
                    .simpleFrameSize(objects.getJSONObject(i).getInteger("simpleFrameSize"))
                    .teamName(objects.getJSONObject(i).getString("teamName"))
                    .teamNameZh(objects.getJSONObject(i).getString("teamNameZh"))
                    .bgColor(objects.getJSONObject(i).getString("bgColor"))
                    .build());

            datalist.add(objects.getJSONObject(i).getString("teamNameZh"));
        }

        Map<String, TeamEntity> teamMap = teamEntityList.stream()
                .collect(Collectors.toMap(TeamEntity::getTeamNameZh, Function.identity()));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, datalist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Optional<Integer> index = teamEntityList.stream()
                // 步骤3: 使用filter方法对每个对象进行筛选，只保留符合条件的对象
                .filter(item -> item.getTeamName().equals(SPStaticUtils.getString(SettingConst.LOVE_TEAM)))
                // 步骤4: 使用findFirst方法获取第一个符合条件的对象的索引
                .findFirst()
                // 步骤5: 输出结果
                .map(fruit -> datalist.indexOf(fruit.getTeamNameZh()));


        spinner.setSelection(index.get());

        // 添加监听器来获取选中的项
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 获取选中的项
                String selected = parent.getItemAtPosition(position).toString();
                // 处理选中事件
                // Toast.makeText(parent.getContext(), "Selected: " + selected, Toast.LENGTH_LONG).show();
                TeamEntity teamEntity = teamMap.get(selected);
                SPStaticUtils.put(SettingConst.LOVE_TEAM, teamEntity.getTeamName());


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 无选项被选中时的处理
            }
        });


        return container;
    }
}
