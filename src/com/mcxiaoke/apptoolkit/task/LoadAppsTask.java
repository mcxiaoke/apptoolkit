package com.mcxiaoke.apptoolkit.task;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import com.mcxiaoke.apptoolkit.AppConfig;
import com.mcxiaoke.apptoolkit.AppContext;
import com.mcxiaoke.apptoolkit.cache.CacheManager;
import com.mcxiaoke.apptoolkit.db.Database;
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
        List<AppInfo> apps = null;
        List<PackageInfo> installedPackages = mPackageManager.getInstalledPackages(flags);
        if (installedPackages != null && installedPackages.size() > 0) {
            apps = new ArrayList<AppInfo>(installedPackages.size());
            List<String> backupDatas = new ArrayList<String>();

            Database db = AppContext.getApp().getDB();
            List<String> dbBackupDatas = db.getBackupApps();
            if (dbBackupDatas != null) {
                for (String packageName : dbBackupDatas) {
                    if (Utils.isBackupDataExists(packageName)) {
                        backupDatas.add(packageName);
                    } else {
                        db.removeBackup(packageName);
                    }
                }
            }
            List<String> backupApks = Utils.getBackupApkFiles();

            if (AppConfig.TYPE_USER_APP_MANAGER == type) {
                for (PackageInfo info : installedPackages) {
                    if (includeSystemApp || !Utils.isSystemApp(info)) {
                        AddToApps(apps, backupDatas, backupApks, info);
                    }
                }
            } else if (AppConfig.TYPE_SYSTEM_APP_MANAGER == type) {
                for (PackageInfo info : installedPackages) {
                    if (Utils.isSystemApp(info)) {
                        AddToApps(apps, backupDatas, backupApks, info);
                    }
                }
            }
        }
        return apps;
    }

    private void AddToApps(List<AppInfo> apps, List<String> backupDatas, List<String> backupApks, PackageInfo info) {
        AppInfo app = Utils.convert(mPackageManager, info);
        app.apkBackup = backupApks.contains(Utils.buildApkName(app));
        app.dataBackup = backupDatas.contains(app.packageName);
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
