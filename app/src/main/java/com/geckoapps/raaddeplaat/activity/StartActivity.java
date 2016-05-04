package com.geckoapps.raaddeplaat.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geckoapps.raaddeplaat.R;
import com.geckoapps.raaddeplaat.animation.SnowAnimation;
import com.geckoapps.raaddeplaat.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends Activity {
    @Bind(R.id.start_play)
    RelativeLayout playScreen;
    @Bind(R.id.start_load)
    LinearLayout loadScreen;
    @Bind(R.id.start_guy) ImageView guy;
    @Bind(R.id.start_logo) ImageView logo;
    @Bind(R.id.start_cloud) TextView cloud;
    @Bind(R.id.button_play) ImageView play;
    @Bind(R.id.snow1) ImageView snow1;
    @Bind(R.id.snow2) ImageView snow2;
    @Bind(R.id.play_rising_sun) ImageView playSun;

    @Bind(R.id.snowflakeContainer) RelativeLayout snowflakeContainer;

    private Typeface typeface;
    private AnimationSet animSnow1;
    private boolean loadingLevels = true;

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

    @Override
    public void onResume() {
        super.onResume();
        guy.setVisibility(View.INVISIBLE);
        cloud.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.INVISIBLE);
        play.setVisibility(View.INVISIBLE);
        playSun.setVisibility(View.INVISIBLE);

        if (animSnow1 != null) {
            animSnow1.reset();
            animSnow1.start();
        }
        if (!loadingLevels) {
            startAnimations();
            startSnowAnimation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (animSnow1 != null) {
            animSnow1.cancel();
        }
        guy.clearAnimation();
        cloud.clearAnimation();
        playSun.clearAnimation();
        play.clearAnimation();
    }

    private void startAnimations() {
        animateSnow();
        animateLogo();
    }

    private void animateCloud() {
        Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
        cloud.setAnimation(fadein);
        fadein.start();
    }

    Animation anim;

    private void animateButton() {
        anim = AnimationUtils.loadAnimation(this, R.anim.anim_playbutton);
        play.setAnimation(anim);
        anim.setFillAfter(true);
        anim.setFillEnabled(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Animation sun = AnimationUtils.loadAnimation(StartActivity.this, R.anim.fadein_playsun);
                playSun.setAnimation(sun);
                sun.start();
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

    private void animateLogo() {
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

    private void animateSnow() {
        Animation fadein1 = AnimationUtils.loadAnimation(this, R.anim.fadein);
        snow1.setAnimation(fadein1);
        fadein1.start();

        Animation fadein2 = AnimationUtils.loadAnimation(this, R.anim.fadein);
        snow2.setAnimation(fadein2);
        fadein2.start();
    }

    private void animateGuy() {
        Animation animGuy = AnimationUtils.loadAnimation(this, R.anim.anim_guy);
        animGuy.setFillAfter(true);
        animGuy.setFillEnabled(true);

        guy.setAnimation(animGuy);
        animGuy.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                guy.setVisibility(View.VISIBLE);
                animateCloud();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((AnimationDrawable) guy.getBackground()).start();
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
            loadingLevels = false;
            showPlayScreen();
        }
    }

    private void shuffleLevels() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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

        startSnowAnimation();
    }

    private void startSnowAnimation() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (snowflakeContainer.getChildCount() < 20) {
                    StartActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //create image add to layout
                            ImageView snowFlake = new ImageView(StartActivity.this);
                            snowFlake.setBackgroundResource(R.drawable.fp_snowflake1);
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(Utils.getLeftMargin(StartActivity.this), Utils.convertDpToPixel(-100, StartActivity.this), 0, 0);
                            snowFlake.setLayoutParams(lp);
                            snowflakeContainer.addView(snowFlake);

                            animSnow1 = new SnowAnimation(StartActivity.this);
                            snowFlake.setAnimation(animSnow1);
                            animSnow1.start();
                        }
                    });
                }
            }
        }, 0, 2000);

    }

    @OnClick(R.id.button_play)
    public void play() {
        Animation animGuy = AnimationUtils.loadAnimation(StartActivity.this, R.anim.anim_guy_down);
        guy.clearAnimation();
        guy.setAnimation(animGuy);


        animGuy.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Animation fadeOut = AnimationUtils.loadAnimation(StartActivity.this, R.anim.fadeout_textcloud);
                cloud.setAnimation(fadeOut);
                fadeOut.start();
                play.clearAnimation();
                play.setVisibility(View.INVISIBLE);
                playSun.clearAnimation();
                playSun.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                guy.setVisibility(View.INVISIBLE);


                startPlay();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animGuy.start();
    }


    private void startPlay() {
        Intent intent = new Intent(StartActivity.this, LevelActivity.class);
        startActivity(intent);
    }

}
