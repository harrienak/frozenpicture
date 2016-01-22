package com.geckoapps.raaddeplaat.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import com.geckoapps.raaddeplaat.R;
import com.geckoapps.raaddeplaat.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends Activity {
    @Bind(R.id.start_play)
    LinearLayout playScreen;
    @Bind(R.id.start_load)
    LinearLayout loadScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ButterKnife.bind(this);

        loadLevels();
    }

    private void loadLevels() {
        if (Utils.isFirstTime(this)) {
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

            }
        }).start();
    }

    private void showPlayScreen() {
        loadScreen.setVisibility(View.GONE);
        playScreen.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.button_play)
    public void play() {

    }
}
