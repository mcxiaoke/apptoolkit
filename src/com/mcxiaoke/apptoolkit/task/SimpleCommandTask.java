package com.mcxiaoke.apptoolkit.task;

import android.content.Context;
import com.mcxiaoke.apptoolkit.AppConfig;
import com.mcxiaoke.apptoolkit.AppContext;
import com.mcxiaoke.apptoolkit.R;
import com.mcxiaoke.apptoolkit.exception.NoPermissionException;
import com.mcxiaoke.apptoolkit.model.AppInfo;
import com.mcxiaoke.apptoolkit.util.AppUtils;
import com.mcxiaoke.apptoolkit.util.Utils;

/**
 * Project: apptoolkit
 * Package: com.mcxiaoke.apptoolkit.task
 * User: mcxiaoke
 * Date: 13-6-16
 * Time: 下午8:29
 */
public class SimpleCommandTask extends SimpleAsyncTask {
    private Context mContext;

    public SimpleCommandTask(Context context) {
        super();
        mContext = context;
    }

    @Override
    protected Boolean onExecute(TaskMessage... params) throws Exception {
        TaskMessage tm = params[0];
        int cmd = tm.type;
        AppInfo app = (AppInfo) tm.object;
        if (app == null) {
            throw new NullPointerException("app cannot be null.");
        }
        boolean result = true;
        switch (cmd) {
            case AppConfig.CMD_BACKUP_APP_ONE:
                result = doBackupAppApk(app);
                break;
            case AppConfig.CMD_RESTORE_APP_ONE:
                break;
            case AppConfig.CMD_BACKUP_DATA_ONE:
                result = doBackupAppData(app);
                break;
            case AppConfig.CMD_RESTORE_DATA_ONE:
                result = doRestoreAppData(app);
                break;
            case AppConfig.CMD_SILENT_INSTAlL_ONE:
                break;
            case AppConfig.CMD_SILENT_UNINSTALL_ONE:
                break;
            case AppConfig.CMD_CLEAR_CACHE_ONE:
                break;
            case AppConfig.CMD_CLEAR_DATA_ONE:
                break;
            case AppConfig.CMD_INSTALL_SYSTEM_APP_ONE:
                break;
            case AppConfig.CMD_UNINSTALL_SYSTEM_APP_ONE:
                break;
        }
        return result;
    }

    private boolean doBackupAppApk(AppInfo app) throws Exception {
        AppContext.v("doBackupAppApk name=" + app.appName);
        boolean result = AppUtils.backupAppApk(app);
        app.apkBackup = true;
        String text = String.format(mContext.getString(R.string.msg_backup_app_success), app.appName, Utils.getBackupAppsDir());
        AppContext.postShowToast(text);
        return result;
    }

    private boolean doBackupAppData(AppInfo app) throws Exception {
        AppContext.v("doBackupAppData name=" + app.appName);

        boolean result = AppUtils.backupAppData(app);
        if (result) {
            AppContext.getApp().getDB().addBackup(app.packageName);
            app.dataBackup = true;
            String text = String.format(mContext.getString(R.string.msg_backup_data_success), app.appName, Utils.getBackupDataDir());
            AppContext.postShowToast(text);
        } else {
            throw new NoPermissionException();
        }
        return result;
    }

    private boolean doRestoreAppData(AppInfo app) throws Exception {
        AppContext.v("doRestoreAppData name=" + app.appName);

        boolean result = AppUtils.restoreAppData(app);
        if (result) {
            String text = String.format(mContext.getString(R.string.msg_restore_data_success), app.appName);
            AppContext.postShowToast(text);
        } else {
            throw new NoPermissionException();
        }
        return result;
    }

    @Override
    protected void onPostExecuteSuccess(Boolean aBoolean) {
        super.onPostExecuteSuccess(aBoolean);
        AppContext.v("onPostExecuteSuccess result=" + aBoolean);
    }

    @Override
    protected void onPostExecuteFailure(Throwable exception) {
        super.onPostExecuteFailure(exception);
        AppContext.v("onPostExecuteFailure exception=" + exception.toString());
        exception.printStackTrace();
        if (exception instanceof NoPermissionException) {
            AppContext.showToast(mContext, R.string.msg_backup_failed_no_permission);
        }
    }
}
