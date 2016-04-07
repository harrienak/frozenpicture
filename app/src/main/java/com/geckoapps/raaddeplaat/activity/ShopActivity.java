package com.geckoapps.raaddeplaat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.geckoapps.raaddeplaat.R;
import com.geckoapps.raaddeplaat.model.Toolbar;
import com.tapjoy.TJActionRequest;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJEarnedCurrencyListener;
import com.tapjoy.TJError;
import com.tapjoy.TJGetCurrencyBalanceListener;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.TJSpendCurrencyListener;
import com.tapjoy.Tapjoy;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ShopActivity extends Activity implements TJPlacementListener{
    private final String TAG = "tapjoy";
    private TJPlacement p;
    private TJPlacementListener placementListener;
    @Bind(R.id.level_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        init();

        Tapjoy.connect(this.getApplicationContext(), "EEpJAFIgTZWSVZveVAHrSgECm666elrEzU3MlirgWcDNCmcezIWExKy-XaLu", null, new TJConnectListener() {
            // called when Tapjoy connect call succeed
            @Override
            public void onConnectSuccess() {
                Log.d(TAG, "Tapjoy connect Succeeded");
                p = new TJPlacement(getApplicationContext(), "InsufficientCurrency", placementListener);
                if (Tapjoy.isConnected()) {
                    p.requestContent();
                } else {
                    Log.d("%s", "Tapjoy SDK must finish connecting before requesting content.");
                }
            }

            // called when Tapjoy connect call failed
            @Override
            public void onConnectFailure() {
                Log.d(TAG, "Tapjoy connect Failed");
            }
        });
        Tapjoy.setDebugEnabled(true);

        tapjoyShit();

    }

    private void init(){
        ButterKnife.bind(this);
        toolbar.setShopToolbar();
    }


    private void tapjoyShit() {
        Tapjoy.getCurrencyBalance(new TJGetCurrencyBalanceListener() {
            @Override
            public void onGetCurrencyBalanceResponse(String currencyName, int balance) {
                Log.i(TAG, "getCurrencyBalance returned " + currencyName + ":" + balance);
                if (balance > 0) {
                    Tapjoy.spendCurrency(balance, new TJSpendCurrencyListener() {
                        @Override
                        public void onSpendCurrencyResponse(String currencyName, int balance) {
                            Log.i("Tapjoy", currencyName + ": " + balance);
                            toolbar.spendCoins(balance);
                        }

                        @Override
                        public void onSpendCurrencyResponseFailure(String error) {
                            Log.i("Tapjoy", "spendCurrency error: " + error);
                        }
                    });
                }
            }

            @Override
            public void onGetCurrencyBalanceResponseFailure(String error) {
                Log.i("Tapjoy", "getCurrencyBalance error: " + error);
            }
        });

        // Get notifications whenever Tapjoy currency is earned.
        Tapjoy.setEarnedCurrencyListener(new TJEarnedCurrencyListener() {
            @Override
            public void onEarnedCurrency(String currencyName, int amount) {
                Log.i("Tapjoy", "You've just earned " + amount + " " + currencyName);
                if (amount > 0) {
                    Tapjoy.spendCurrency(amount, new TJSpendCurrencyListener() {
                        @Override
                        public void onSpendCurrencyResponse(String currencyName, int balance) {
                            Log.i("Tapjoy", currencyName + ": " + balance);
                        }

                        @Override
                        public void onSpendCurrencyResponseFailure(String error) {
                            Log.i("Tapjoy", "spendCurrency error: " + error);
                        }
                    });
                }
            }
        });
    }

    @OnClick(R.id.button_tapjoy)
    public void openTapjoyOfferWall(){
        if (p.isContentReady()) {
            p.showContent();
        } else {
            //handle situation where there is no content to show, or it has not yet downloaded.
        }
    }

    //session start
    @Override
    protected void onStart() {
        super.onStart();
        Tapjoy.onActivityStart(this);
    }

    //session end
    @Override
    protected void onStop() {
        Tapjoy.onActivityStop(this);
        super.onStop();
    }

    @Override
    public void onRequestSuccess(TJPlacement tjPlacement) {

    }

    @Override
    public void onRequestFailure(TJPlacement tjPlacement, TJError tjError) {

    }

    @Override
    public void onContentReady(TJPlacement tjPlacement) {

    }

    @Override
    public void onContentShow(TJPlacement tjPlacement) {

    }

    @Override
    public void onContentDismiss(TJPlacement tjPlacement) {

    }

    @Override
    public void onPurchaseRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s) {

    }

    @Override
    public void onRewardRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s, int i) {

    }
}
