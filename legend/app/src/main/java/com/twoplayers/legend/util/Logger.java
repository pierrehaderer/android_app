package com.twoplayers.legend.util;

import android.util.Log;

public class Logger {

    public static void debug(String message) {
        Log.d("DEBUG", message);
    }

    public static void info(String message) {
        Log.i("INFO ", message);
    }

    public static void warn(String message) {
        Log.w("WARN ", message);
    }

    public static void error(String message) {
        Log.e("ERROR", message);
    }
}
