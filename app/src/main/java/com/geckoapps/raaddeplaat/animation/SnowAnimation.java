package com.geckoapps.raaddeplaat.animation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by Sjoerd on 6-4-2016.
 */
public class SnowAnimation extends AnimationSet {
    private Context context;
    private Random r;


    public SnowAnimation(Context context) {
        super(context, null);
        this.context = context;
        init();

    }

    public SnowAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SnowAnimation(boolean shareInterpolator) {
        super(shareInterpolator);
    }

    private int getRandom(int i) {
        return r.nextInt(i);
    }

    private int getRandom(int x, int y) {
        return (r.nextInt(y - x) + x);
    }

    private boolean getRandomBoolean() {
        return r.nextBoolean();
    }

    private void init() {
        r = new Random();

        //settings
       // setStartOffset(getRandom(3000));
        setInterpolator(new LinearInterpolator());

        //rotate
        RotateAnimation rotateAnimation;
        if (getRandomBoolean()) {
            rotateAnimation = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        } else {
            rotateAnimation = new RotateAnimation(0f, -359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        }
        rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setDuration(getRandom(5000, 7000));
        rotateAnimation.setFillAfter(true);
        this.addAnimation(rotateAnimation);

        //scale
        if(getRandomBoolean()){
            ScaleAnimation scaleAnimation = new ScaleAnimation(0.4f, 1.2f, 0.4f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(getRandom(7500, 10000));
            scaleAnimation.setRepeatMode(Animation.REVERSE);
            scaleAnimation.setRepeatCount(Animation.INFINITE);
            scaleAnimation.setFillAfter(true);
            this.addAnimation(scaleAnimation);
        }

        //x movement
        TranslateAnimation translateAnimationX = new TranslateAnimation(Animation.ABSOLUTE, -100, Animation.ABSOLUTE, 100, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0);
        translateAnimationX.setRepeatMode(Animation.REVERSE);
        translateAnimationX.setRepeatCount(Animation.INFINITE);
        translateAnimationX.setDuration(getRandom(8000, 10000));
        translateAnimationX.setFillAfter(true);
        this.addAnimation(translateAnimationX);

        //y movement
        TranslateAnimation translateAnimationY = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 2500);
        translateAnimationY.setRepeatMode(Animation.RESTART);
        translateAnimationY.setRepeatCount(Animation.INFINITE);
        translateAnimationY.setDuration(getRandom(14000, 28000));
        translateAnimationY.setFillAfter(true);
        this.addAnimation(translateAnimationY);
    }
}
