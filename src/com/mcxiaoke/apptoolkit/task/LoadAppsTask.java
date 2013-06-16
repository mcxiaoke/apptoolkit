package com.mcxiaoke.apptoolkit.task;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import com.mcxiaoke.apptoolkit.AppConfig;
import com.mcxiaoke.apptoolkit.AppContext;
import com.mcxiaoke.apptoolkit.cache.CacheManager;
import com.mcxiaoke.apptoolkit.model.AppInfo;
import com.mcxiaoke.apptoolkit.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager.task
 * User: com.mcxiaoke
 * Date: 13-6-11
 * Time: 上午10:58
 */
public class LoadAppsTask extends AsyncTaskBase<TaskMessage, Pair<Integer, Integer>, List<AppInfo>> {
    private Context mContext;
    private PackageManager mPackageManager;

    public LoadAppsTask(Context context, AsyncTaskCallback<Pair<Integer, Integer>, List<AppInfo>> callback) {
        super(callback);
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        AppContext.v("LoadAppsTask()");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<AppInfo> onExecute(TaskMessage... params) throws Exception {
        TaskMessage param = params[0];
        int type = param.type;
        boolean includeSystemApp = param.flag;

        AppContext.v("LoadAppsTask.onExecute() type=" + type + " includeSystemApp=" + includeSystemApp);

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
        List<PackageInfo> installedPackages = mPackageManager.getInstalledPackages(flags);
        if (installedPackages != null && installedPackages.size() > 0) {
            apps = new ArrayList<AppInfo>(installedPackages.size());

            if (AppConfig.TYPE_USER_APP_MANAGER == type) {
                for (PackageInfo info : installedPackages) {
                    if (includeSystemApp || !Utils.isSystemApp(info)) {
                        AddToApps(apps, info);
                    }
                }
            } else if (AppConfig.TYPE_SYSTEM_APP_MANAGER == type) {
                for (PackageInfo info : installedPackages) {
                    if (Utils.isSystemApp(info)) {
                        AddToApps(apps, info);
                    }
                }
            }
        }
        return apps;
    }

    private void AddToApps(List<AppInfo> apps, PackageInfo info) {
        AppInfo app = Utils.convert(mPackageManager, info);
        apps.add(app);
        Drawable icon = info.applicationInfo.loadIcon(mPackageManager);
        CacheManager.getInstance().putIcon(info.packageName, icon);
    }

    @Override
    protected void onProgressUpdate(Pair<Integer, Integer>... values) {
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
