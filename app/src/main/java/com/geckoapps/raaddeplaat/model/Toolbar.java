package com.geckoapps.raaddeplaat.model;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geckoapps.raaddeplaat.R;
import com.geckoapps.raaddeplaat.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Sjoerd on 21-1-2016.
 */
public class Toolbar extends RelativeLayout {
    @Bind(R.id.toolbar_coins) TextView coins;
    @Bind(R.id.toolbar_progressbar)ProgressBar progressBar;
    private Context context;
    private SharedPreferences settings;

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
        settings = context.getSharedPreferences(Utils.SHARED_SETTINGS, Context.MODE_PRIVATE);

        inflate(getContext(), R.layout.toolbar, this);
        ButterKnife.bind(this, this);
        setCoins();
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
}
