package com.nntk.nba.widgets.adapter;

import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nntk.nba.widgets.R;
import com.nntk.nba.widgets.entity.TeamEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NbaLogoAdapter extends BaseQuickAdapter<TeamEntity, BaseViewHolder> {


    private int layoutType = 1;

    static Map<Integer, Integer> layoutMap = new HashMap<>();

    static {
        layoutMap.put(1, R.layout.item_team_layout);
        layoutMap.put(2, R.layout.item_team_album_layout);
        layoutMap.put(3, R.layout.item_team_album_list_layout);
    }

    public NbaLogoAdapter(@Nullable List<TeamEntity> data, int layoutType) {
        super(layoutMap.get(layoutType), data);
        this.layoutType = layoutType;
    }


    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, TeamEntity teamEntity) {
        baseViewHolder.setIsRecyclable(false);
        baseViewHolder.setText(R.id.cat_toc_title, teamEntity.getTeamNameZh());

        boolean isLightColor = ColorUtils.isLightColor(ColorUtils.string2Int(teamEntity.getBgColor()));

        if (layoutType == 1) {
            baseViewHolder.setTextColor(R.id.cat_toc_title, isLightColor ? Color.BLACK : Color.WHITE);
        } else {
            baseViewHolder.setText(R.id.sub_title, teamEntity.getTeamName());

        }
        ImageView imageView = baseViewHolder.getView(R.id.image);

        //利用Android9.0新增的ImageDecoder读取gif动画
        ImageDecoder.Source source = ImageDecoder.createSource(getContext().getResources(), ResourceUtils.getMipmapIdByName("gif_" + teamEntity.getTeamName()));
        //从数据源中解码得到gif图形数据
        try {
            Drawable drawable = ImageDecoder.decodeDrawable(source);
            //设置图像视图的图形为gif图片
            imageView.setImageDrawable(drawable);
            ((Animatable) imageView.getDrawable()).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}

