package com.mcxiaoke.apptoolkit.task;

import android.content.Context;
import com.mcxiaoke.apptoolkit.model.AppInfo;
import com.mcxiaoke.apptoolkit.util.Utils;

import java.io.File;
import java.util.List;

/**
 * 批量备份应用安装包，不需要ROOT
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager.task
 * User: com.mcxiaoke
 * Date: 13-6-11
 * Time: 下午1:24
 */
public class BackupAppsApkTask extends AsyncTaskBase<List<AppInfo>, AppInfo, Integer> {
    private Context mContext;


    public BackupAppsApkTask(Context context, AsyncTaskCallback<AppInfo, Integer> callback) {
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
        if (!Utils.isSdcardMounted()) {
            throw new NullPointerException("sdcard is not mounted");
        }

        List<AppInfo> apps = params[0];
        File backupDir = checkBackupDir();
        int backupCount = 0;
        for (AppInfo app : apps) {
            if (isUserCancelled()) {
                break;
            }
            String fileName = Utils.buildApkName(app);
            File src = new File(app.sourceDir);
            File dest = new File(backupDir, fileName);
            if (src.exists() && src.canRead()) {
                if (!dest.exists()) {
                    boolean success = Utils.copyFile(src, dest);
                    if (success) {
                        backupCount++;
                    }
                }
                app.backup = true;
            }
            publishProgress(app);
        }
        return backupCount;
    }

    private File checkBackupDir() {
        return Utils.getBackupAppsDir();
    }
}
