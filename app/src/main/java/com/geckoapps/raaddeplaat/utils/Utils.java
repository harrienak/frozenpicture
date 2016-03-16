package com.geckoapps.raaddeplaat.utils;

import android.content.Context;
import android.content.SharedPreferences;

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

}
