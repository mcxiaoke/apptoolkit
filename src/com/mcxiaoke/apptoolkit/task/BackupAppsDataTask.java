package com.mcxiaoke.apptoolkit.task;

import android.content.Context;
import com.mcxiaoke.apptoolkit.AppContext;
import com.mcxiaoke.apptoolkit.db.Database;
import com.mcxiaoke.apptoolkit.exception.NoPermissionException;
import com.mcxiaoke.apptoolkit.model.AppInfo;
import com.mcxiaoke.apptoolkit.util.Utils;
import com.mcxiaoke.shell.Shell;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量备份应用数据，需要ROOT
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager.task
 * User: com.mcxiaoke
 * Date: 13-6-11
 * Time: 下午1:24
 */
public class BackupAppsDataTask extends AsyncTaskBase<List<AppInfo>, AppInfo, Integer> {
    private Context mContext;


    public BackupAppsDataTask(Context context, AsyncTaskCallback<AppInfo, Integer> callback) {
        super(callback);
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(AppInfo... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecuteSuccess(Integer integer) {
        super.onPostExecuteSuccess(integer);
    }

    @Override
    protected void onPostExecuteFailure(Throwable exception) {
        super.onPostExecuteFailure(exception);
    }

    @Override
    protected Integer onExecute(List<AppInfo>... params) throws Exception {
        if (params == null || params.length == 0) {
            throw new NullPointerException("params is null");
        }

        if (!Shell.isRootAccessAvailable()) {
            throw new NoPermissionException();
        }

        List<AppInfo> apps = params[0];
        List<String> backupPackages = new ArrayList<String>();
        File backupDir = Utils.getBackupDataDir();
        int backupCount = 0;
        for (AppInfo app : apps) {
            if (isUserCancelled()) {
                break;
            }
            AppContext.v("BackupAppsDataTask processing name=" + app.appName);
            File src = new File(app.dataDir);
            File dest = new File(backupDir, app.packageName);
            boolean success = Shell.copyAppData(src.getPath(), dest.getPath(), false, true);
            if (success) {
                backupPackages.add(app.packageName);
                backupCount++;
            } else {
                AppContext.e("BackupAppsDataTask processing backup failed app: name=" + app.appName + " src=" + src);
//                break;
            }
            publishProgress(app);
        }
        Database db = AppContext.getApp().getDB();
        db.addBackups(backupPackages);
        return backupCount;
    }

    private String buildProgressText(AppInfo app) {
        return new StringBuilder().append(app.appName).append(" v").append(app.versionName).append("\n").append(app.sourceDir).toString();

    }
}
