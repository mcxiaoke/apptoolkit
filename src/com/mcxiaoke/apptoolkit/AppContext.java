package com.mcxiaoke.apptoolkit;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
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
    private static final boolean DEBUG = false;

    private static AppContext sInstance;
    private Handler mUiHandler;
    private Gson mGson;
    private ExecutorService mExecutor;
    private boolean mRootGranted;
    private boolean mBusyboxInstalled;
    private static String sVersionName;
    private static int sVersionCode;
    private static String sPackageName;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mUiHandler = new Handler(Looper.getMainLooper());
        mExecutor = Executors.newCachedThreadPool();

        PackageManager pm = getPackageManager();
        try {
            sPackageName = getPackageName();
            PackageInfo info = pm.getPackageInfo(sPackageName, 0);
            sVersionName = info.versionName;
            sVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static String getVersionName() {
        return sVersionName;
    }

    public static int getVersionCode() {
        return sVersionCode;
    }

    public static String getPackage() {
        return sPackageName;
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

    public static void postShowToast(final int resId) {
        final AppContext app = getApp();
        app.mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                showToast(app, resId);
            }
        });
    }

    public static void postShowToast(final String text) {
        final AppContext app = getApp();
        app.mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                showToast(app, text);
            }
        });
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

    public static void v(String tag, String message) {
        Log.v(tag, message);
    }

    public static void e(String tag, String message) {
        Log.e(tag, message);
    }

    public static void e(String tag, Throwable t) {
        Log.e(tag, "Exception: " + t);
    }

    public static void v(String... messages) {
        for (String message : messages) {
            Log.v(DEFAULT_TAG, message);
        }
    }


}
