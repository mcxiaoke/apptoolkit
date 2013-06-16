package com.mcxiaoke.apptoolkit;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Project: apptoolkit
 * Package: com.mcxiaoke.apptoolkit
 * User: com.mcxiaoke
 * Date: 13-6-10
 * Time: 下午9:15
 */
public class AppContext extends Application {
    private static final boolean DEBUG = true;

    private static AppContext sInstance;
    private Gson mGson;
    private ExecutorService mExecutor;
    private boolean mRootGranted;
    private boolean mBusyboxInstalled;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mExecutor = Executors.newCachedThreadPool();
    }


    public static boolean isDebug() {
        return DEBUG;
    }

    public static AppContext getApp() {
        return sInstance;
    }

    public static AppContext getApp(Context context) {
        return (AppContext) context.getApplicationContext();
    }

    public Gson getGson() {
        if (mGson == null) {
            mGson = new GsonBuilder()
                    .serializeNulls()
                    .disableHtmlEscaping()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
        }
        return mGson;
    }

    public ExecutorService getExecutor() {
        if (mExecutor == null) {
            mExecutor = Executors.newCachedThreadPool();
        }
        return mExecutor;
    }

    public void setRootGranted(boolean value) {
        mRootGranted = value;
    }

    public boolean isRootGranted(boolean value) {
        return mRootGranted;
    }

    public void setBusyboxInstalled(boolean value) {
        mBusyboxInstalled = value;
    }

    public boolean isBusyboxInstalled() {
        return mBusyboxInstalled;
    }

    public static void showToast(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    public static void showToast(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    private static final String DEFAULT_TAG = "AppToolkit";

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
