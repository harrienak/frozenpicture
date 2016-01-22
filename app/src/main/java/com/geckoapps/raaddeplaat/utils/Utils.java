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

    public static int PRIZE_HACK = 5;
    public static int PRIZE_SHUFFLE = 10;
    public static int PRIZE_FOR_COIN = 1;


    public static SharedPreferences getSettings(Context context){
        return context.getSharedPreferences(Utils.SHARED_SETTINGS, Context.MODE_PRIVATE);
    }

    public static boolean isFirstTime(Context context){
        return getSettings(context).getBoolean(SHARED_FIRSTTIME, true);
    }

    public static void setSharedPref(Context context, String name, Boolean value){
        getSettings(context).edit().putBoolean(name, value).commit();
    }
}
