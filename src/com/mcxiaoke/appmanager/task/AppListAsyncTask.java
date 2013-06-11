package com.mcxiaoke.appmanager.task;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import com.mcxiaoke.appmanager.cache.AppIconCache;
import com.mcxiaoke.appmanager.model.AppInfo;
import com.mcxiaoke.appmanager.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: filemanager
 * Package: com.mcxiaoke.appmanager.task
 * User: mcxiaoke
 * Date: 13-6-11
 * Time: 上午10:58
 */
public class AppListAsyncTask extends AsyncTaskBase<Boolean, Void, List<AppInfo>> {
    private Context mContext;

    public AppListAsyncTask(Context context, AsyncTaskCallback<Void, List<AppInfo>> callback) {
        super(callback);
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<AppInfo> onExecute(Boolean... params) throws Exception {
        PackageManager pm = mContext.getPackageManager();
        int flags = PackageManager.GET_META_DATA;
//        flags |= PackageManager.GET_SIGNATURES;
//            flags |= PackageManager.GET_CONFIGURATIONS;
//            flags |= PackageManager.GET_DISABLED_COMPONENTS;
//            flags |= PackageManager.GET_GIDS;
//            flags |= PackageManager.GET_INSTRUMENTATION;
//            flags |= PackageManager.GET_INTENT_FILTERS;
//            flags |= PackageManager.GET_PERMISSIONS;
//            flags |= PackageManager.GET_ACTIVITIES;
//            flags |= PackageManager.GET_SERVICES;
//            flags |= PackageManager.GET_RECEIVERS;
//            flags |= PackageManager.GET_PROVIDERS;
        List<AppInfo> apps = null;
        List<PackageInfo> installedPackages = pm.getInstalledPackages(flags);
        if (installedPackages != null && installedPackages.size() > 0) {
            apps = new ArrayList<AppInfo>(installedPackages.size());
            for (PackageInfo info : installedPackages) {
                AppInfo app = Utils.convert(pm, info);
                if (info != null) {
                    apps.add(app);
                }
                ApplicationInfo ainfo = info.applicationInfo;
                Drawable icon = pm.getApplicationIcon(ainfo);
                AppIconCache.getInstance().put(info.packageName, icon);
            }
        }
        return apps;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecuteSuccess(List<AppInfo> appInfos) {
        super.onPostExecuteSuccess(appInfos);
    }

    @Override
    protected void onPostExecuteFailure(Throwable exception) {
        super.onPostExecuteFailure(exception);
    }
}
