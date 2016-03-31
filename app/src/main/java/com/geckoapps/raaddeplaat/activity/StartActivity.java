package com.geckoapps.raaddeplaat.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geckoapps.raaddeplaat.R;
import com.geckoapps.raaddeplaat.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends Activity {
    @Bind(R.id.start_play)
    RelativeLayout playScreen;
    @Bind(R.id.start_load)
    LinearLayout loadScreen;
    @Bind(R.id.start_guy)ImageView guy;
    @Bind(R.id.start_logo) ImageView logo;
    @Bind(R.id.start_cloud) TextView cloud;
    @Bind(R.id.button_play) ImageView play;

    Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ButterKnife.bind(this);
        typeface = Typeface.createFromAsset(getAssets(),
                "font/PAYTONEONE.TTF");
        cloud.setTypeface(typeface);

        loadLevels();
    }

    private void startAnimations() {
        animateLogo();
    }

    private void animateCloud(){
        cloud.setVisibility(View.VISIBLE);
    }

    private void animateButton(){
        play.setVisibility(View.VISIBLE);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_playbutton);
        play.setAnimation(anim);
        anim.setFillAfter(true);
        anim.setFillEnabled(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                play.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim.start();
    }

    private void animateLogo(){
        Animation animLogo = AnimationUtils.loadAnimation(this, R.anim.anim_logo);
        logo.setAnimation(animLogo);
        animLogo.setFillAfter(true);
        animLogo.setFillEnabled(true);
        animLogo.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                logo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animateGuy();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animLogo.start();
    }

    private void animateGuy(){
        Animation animGuy = AnimationUtils.loadAnimation(this, R.anim.anim_guy);
        animGuy.setFillAfter(true);
        animGuy.setFillEnabled(true);

        guy.setAnimation(animGuy);
        animGuy.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                guy.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((AnimationDrawable) guy.getBackground()).start();
                animateCloud();
                animateButton();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animGuy.start();
    }


    private void loadLevels() {
        if (Utils.isFirstTime(this)) {
            Utils.setSharedPref(this, Utils.SHARED_LEVEL, 1);
            Utils.setSharedPref(this, Utils.SHARED_FIRSTTIME, false);
            shuffleLevels();
        } else {
            showPlayScreen();
        }
    }

    private void shuffleLevels() {
        new Thread(new Runnable() {
            @Override
            public void run() {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        showPlayScreen();
                    }
                });
            }
        }).start();
    }

    private void showPlayScreen() {
        loadScreen.setVisibility(View.GONE);
        playScreen.setVisibility(View.VISIBLE);

        guy.setBackgroundResource(R.drawable.anim_oaken);
        startAnimations();
    }

    @OnClick(R.id.button_play)
    public void play() {
        Intent intent= new Intent(StartActivity.this, LevelActivity.class);
        startActivity(intent);
    }
}
