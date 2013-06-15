package com.mcxiaoke.apptoolkit.service;

import android.content.Intent;

/**
 * Project: apptoolkit
 * Package: com.mcxiaoke.apptoolkit.service
 * User: mcxiaoke
 * Date: 13-6-15
 * Time: 下午5:57
 */
public final class BackgroundService extends BaseService {
    public static final int CMD_BACKUP_APPS = 1001;  // 备份应用
    public static final int CMD_BACKUP_DATA = 1002;  // 备份数据 ROOT

    public static final int CMD_RESTORE_APPS = 1011; // 恢复应用 ROOT
    public static final int CMD_RESTORE_DATA = 1012; // 恢复数据 ROOT

    public static final int CMD_INSTALL_APPS = 1021; // 批量安装 ROOT
    public static final int CMD_UNINSTALL_APPS = 1022;// 批量卸载 ROOT

    public static final int CMD_CLEAR_APPCACHE = 1031; // 清除缓存 ROOT
    public static final int CMD_CLEAR_APPDATA = 1032; // 清除数据 ROOT

    public static final int CMD_COPY_FILES = 1041; // 复制文件 ROOT*
    public static final int CMD_MOVE_FILES = 1042; // 移动文件 ROOT*
    public static final int CMD_DELETE_FILES = 1043; // 删除文件 ROOT*


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(long taskId, Intent intent) {
    }

    @Override
    protected boolean isDebug() {
        return false;
    }
}
