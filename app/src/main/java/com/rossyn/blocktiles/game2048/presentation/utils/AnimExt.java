package com.rossyn.blocktiles.game2048.presentation.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.rossyn.blocktiles.game2048.R;

public class AnimExt {

    public static void scaleAnim(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.scale_animation);
        view.startAnimation(animation);
    }

    public static Animation animLoad(Context context, int res) {
        return AnimationUtils.loadAnimation(context, res);
    }

    public static void onEnd(Animation animation, Runnable callback) {
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                callback.run();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}
