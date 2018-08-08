package com.patientz.utils;

public class Log {


    public static final String env = "Production";

    //public static final String env = "STAGE";

    // public static final String env = "QA";


    //public static final String env = "DEMO";

    public static void d(String tag, String msg) {
        if (!"Production".equals(env)) {
            android.util.Log.d(env + "-" + tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (!"Production".equals(env)) {
            android.util.Log.e(env + "-" + tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (!"Production".equals(env)) {
            android.util.Log.i(env + "-" + tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (!"Production".equals(env)) {
            android.util.Log.v(env + "-" + tag, msg);
        }
    }
}
