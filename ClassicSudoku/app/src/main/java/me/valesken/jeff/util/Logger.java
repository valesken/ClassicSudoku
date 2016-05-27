package me.valesken.jeff.util;

import android.util.Log;

/**
 * Created by jeff on 2/16/2016.
 * Last updated by jeff on 2/16/2016.
 */
public class Logger {

    public void logDebugMessage(String message) {
        Log.d("Debug Info", message);
    }

    public void logErrorMessage(String message) {
        Log.e("Error Info", message);
    }

}
