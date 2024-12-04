package com.nntk.nba.widgets.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.animation.BaseAnimation;


/**
 * 自定义动画1
 */
public class CustomAnimation1 implements BaseAnimation {
    @NonNull
    @Override
    public Animator[] animators(@NonNull View view) {
        Animator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.3f, 1);
        Animator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.3f, 1);
        Animator alpha = ObjectAnimator.ofFloat(view, "alpha", 0, 1f);

        scaleY.setDuration(350);
        scaleX.setDuration(350);
        alpha.setDuration(350);

        scaleY.setInterpolator(new DecelerateInterpolator());
        scaleX.setInterpolator(new DecelerateInterpolator());

        return new Animator[]{scaleY, scaleX, alpha};
    }
}