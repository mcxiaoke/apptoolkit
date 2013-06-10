package com.mcxiaoke.appmanager;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Project: filemanager
 * Package: com.mcxiaoke.appmanager
 * User: mcxiaoke
 * Date: 13-6-10
 * Time: 下午9:15
 */
public class AppContext extends Application {
    private static final boolean DEBUG = true;

    private static Gson sGson;


    @Override
    public void onCreate() {
        super.onCreate();
    }


    public static boolean isDebug() {
        return DEBUG;
    }

    public static Gson getGson() {
        if (sGson == null) {
            sGson = new GsonBuilder()
                    .serializeNulls()
                    .disableHtmlEscaping()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
        }
        return sGson;
    }

    public static void showToast(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    private static final String DEFAULT_TAG = "LOG";

    public static void d(String message) {
        Log.d(DEFAULT_TAG, message);
    }

    public static void v(String message) {
        Log.v(DEFAULT_TAG, message);
    }

    public static void e(String message) {
        Log.e(DEFAULT_TAG, message);
    }

    public static void e(Throwable t) {
        Log.e(DEFAULT_TAG, "Exception: " + t);
    }
}
