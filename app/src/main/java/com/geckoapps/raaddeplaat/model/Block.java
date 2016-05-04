package com.geckoapps.raaddeplaat.model;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.geckoapps.raaddeplaat.R;

/**
 * Created by Sjoerd on 15-1-2016.
 */
public class Block extends ImageView{
    private boolean hasCoin = false;
    private boolean selected = false;

    public Block(Context context) {
        super(context);
        init();
    }

    public Block(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Block(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setBackgroundResource(R.drawable.block);
    }

    public void fadeInBlock(){
        selected = true;
        if(hasCoin){
            fadeInCoin();
        } else {
            fadeInSweep();
        }
    }

    public void fadeInCoin(){
        setBackgroundResource(R.drawable.coin1);
      //  ((AnimationDrawable) this.getBackground()).start();
    }

    public void fadeInSweep(){
        setBackgroundResource(R.drawable.axe1);
     //   ((AnimationDrawable) this.getBackground()).start();
    }

    public void breakBlock(){
        if(hasCoin){
            setBackgroundResource(R.drawable.anim_block_break_coin);
        } else{
            setBackgroundResource(R.drawable.anim_block_break);
        }
        ((AnimationDrawable) this.getBackground()).start();
    }

    public boolean isHasCoin(){
        return hasCoin;
    }
    public void setHasCoin(boolean hasCoin){
        this.hasCoin = hasCoin;
    }
    public boolean isSelected(){
        return selected;
    }
    public void setSelected(boolean selected){
        if(!selected){
            setBackgroundResource(R.drawable.block);
        }
        this.selected = selected;
    }
}
