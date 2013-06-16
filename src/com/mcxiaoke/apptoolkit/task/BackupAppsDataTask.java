package com.mcxiaoke.apptoolkit.task;

import android.content.Context;
import android.os.Environment;
import com.mcxiaoke.apptoolkit.AppConfig;
import com.mcxiaoke.apptoolkit.model.AppInfo;
import com.mcxiaoke.apptoolkit.util.Utils;

import java.io.File;
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

        List<AppInfo> apps = params[0];
        File sdcard = Environment.getExternalStorageDirectory();
        File backupDir = new File(sdcard, AppConfig.BACKUP_DIR);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        int backupCount = 0;
        for (AppInfo app : apps) {
            if (isUserCancelled()) {
                break;
            }
            String fileName = Utils.buildApkName(app);
            File src = new File(app.sourceDir);
            File dest = new File(backupDir, fileName);
            if (src.exists() && src.canRead() && !dest.exists()) {
                boolean success = Utils.copyFile(src, dest);
                if (success) {
                    backupCount++;
                }
            }
            publishProgress(app);
        }
        return backupCount;
    }

    private String buildProgressText(AppInfo app) {
        return new StringBuilder().append(app.appName).append(" v").append(app.versionName).append("\n").append(app.sourceDir).toString();

    }
}
