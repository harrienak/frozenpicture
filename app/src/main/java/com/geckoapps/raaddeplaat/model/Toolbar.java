package com.geckoapps.raaddeplaat.model;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geckoapps.raaddeplaat.R;
import com.geckoapps.raaddeplaat.activity.LevelActivity;
import com.geckoapps.raaddeplaat.activity.ShopActivity;
import com.geckoapps.raaddeplaat.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Sjoerd on 21-1-2016.
 */
public class Toolbar extends RelativeLayout {
    @Bind(R.id.toolbar_level_title) TextView levelTitle;
    @Bind(R.id.toolbar_level) TextView level;
    @Bind(R.id.toolbar_coins)
    TextView coins;
    @Bind(R.id.toolbar_progressbar)ProgressBar progressBar;
    @Bind(R.id.toolbar_back)Button back;
    private Context context;
    private SharedPreferences settings;
    private Typeface typeface;

    public Toolbar(Context context) {
        super(context);
        init(context);
    }

    public Toolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Toolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        settings = context.getSharedPreferences(Utils.SHARED_SETTINGS, Context.MODE_PRIVATE);
        typeface = Typeface.createFromAsset(context.getAssets(),
                "font/PAYTONEONE.TTF");

        inflate(getContext(), R.layout.toolbar, this);
        ButterKnife.bind(this, this);
        levelTitle.setTypeface(typeface);
        level.setTypeface(typeface);
        coins.setTypeface(typeface);

        setCoins();
        setLevel();
    }

    public void setCoins(){
        coins.setText("" + getCoins());
    }

    public int getCoins(){
        return settings.getInt(Utils.SHARED_COINS,0);
    }

    public void addCoins(int coins){
        settings.edit().putInt(Utils.SHARED_COINS, (coins + getCoins())).commit();
        setCoins();
    }

    public boolean spendCoins(int coins){
        if(coins <= getCoins() ){
            settings.edit().putInt(Utils.SHARED_COINS, (getCoins() -coins )).commit();
            setCoins();
            return true;
        } else {
            return false;
        }
    }

    public void setLevel(){
        level.setText("" + Utils.getSharedPref(context, Utils.SHARED_LEVEL));
    }

    public void addLevel(int i){
        Utils.setSharedPref(context, Utils.SHARED_LEVEL, (Utils.getSharedPref(context, Utils.SHARED_LEVEL) + i));
        setLevel();
    }

    public void initProgressBar(int max){
        progressBar.setMax(max);
        progressBar.setProgress(max);
    }

    public void animateProgressBar(final int progress){
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), (progressBar.getProgress() - progress) );
        progressAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //progressBar.setProgress( (progressBar.getProgress() - progress)  );
                LevelActivity.progressGoing = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        progressAnimator.setDuration(1000);
        progressAnimator.setInterpolator(new AccelerateInterpolator());
        progressAnimator.start();
    }

    @OnClick(R.id.toolbar_coins)
    public void openShop(){
        Intent i = new Intent(context, ShopActivity.class);
        context.startActivity(i);
    }

    public void setShopToolbar() {
        level.setVisibility(View.GONE);
        levelTitle.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        coins.setClickable(false);
    }

    @OnClick(R.id.toolbar_back)
    public void back(){
        ((Activity)context).finish();
    }
}
