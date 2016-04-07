package com.geckoapps.raaddeplaat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.util.Random;

/**
 * Created by Sjoerd on 19-1-2016.
 */
public class Utils {
    public static String SHARED_SETTINGS = "settings";
    public static String SHARED_COINS = "coins";
    public static String SHARED_FIRSTTIME = "first";
    public static String SHARED_LEVEL = "level";

    public static int PRIZE_HACK = 5;
    public static int PRIZE_SHUFFLE = 10;
    public static int PRIZE_FOR_COIN = 1;
    public static int PRIZE_HINT = 60;
    public static int PRIZE_BOMB = 90;

    public static SharedPreferences getSettings(Context context){
        return context.getSharedPreferences(Utils.SHARED_SETTINGS, Context.MODE_PRIVATE);
    }

    public static boolean isFirstTime(Context context){
        return getSettings(context).getBoolean(SHARED_FIRSTTIME, true);
    }



    public static void setSharedPref(Context context, String name, Boolean value){
        getSettings(context).edit().putBoolean(name, value).commit();
    }

    public static int getSharedPref(Context context, String name){
        return getSettings(context).getInt(name, 0);
    }

    public static void setSharedPref(Context context, String name, int value){
        getSettings(context).edit().putInt(name, value).commit();
    }


    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static int convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) dp;
    }


    public static int getLeftMargin(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        Random random =  new Random();

        return random.nextInt(width-200);
    }

}
