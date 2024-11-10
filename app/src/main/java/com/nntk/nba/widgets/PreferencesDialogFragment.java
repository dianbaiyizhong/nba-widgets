package com.nntk.nba.widgets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nntk.nba.widgets.constant.SettingConst;

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


        radioGroup.check(ResourceUtils.getIdByName(SPStaticUtils.getString(SettingConst.MOVIE_TYPE, "2015")));

        return container;
    }
}
