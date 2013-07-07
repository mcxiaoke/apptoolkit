package com.mcxiaoke.apptoolkit.util;

import android.content.Context;
import com.mcxiaoke.apptoolkit.model.AppInfo;
import com.mcxiaoke.shell.Shell;

import java.io.File;
import java.io.IOException;

/**
 * Project: filemanager
 * Package: com.mcxiaoke.appmanager.util
 * User: mcxiaoke
 * Date: 13-6-11
 * Time: 下午10:53
 */
public final class AppUtils {

    public static void installApp(Context context, AppInfo app) {

    }

    public static void installAsSystemApp(Context context, AppInfo app) {

    }

    public static void moveToSystem(Context context, AppInfo app) {

    }

    public static void clearAppData(Context context, AppInfo app) {

    }

    public static void uninstallApp(Context context, AppInfo app) {

    }

    public static void showDetail(Context context, AppInfo app) {

    }

    public static void copyPackageName(Context context, AppInfo app) {

    }

    public static void killAppProcess(Context context, AppInfo app) {

    }

    public static void showApp(Context context, AppInfo app) {


    }

    public static boolean backupAppApk(AppInfo app) throws IOException {
        boolean result = false;
        File src = new File(app.sourceDir);
        File dest = new File(Utils.getBackupAppsDir(), Utils.buildApkName(app));
        if (src.exists() && !dest.exists()) {
            result = IOHelper.copyFile(src, dest);
        }
        return result;
    }

    public static boolean backupAppData(AppInfo app) throws Exception {
        String src = app.dataDir;
        String dest = new File(Utils.getBackupDataDir(), app.packageName).getPath();
        return Shell.backupAppData(src, dest, false, true);

    }

    public static boolean restoreAppData(AppInfo app) throws Exception {
        File backupDataFile = new File(Utils.getBackupDataDir(), app.packageName);
        String src = backupDataFile.getPath();
        String dest = app.dataDir;
        return Shell.restoreAppData(String.valueOf(app.uid), src, dest, false, false);
    }

    public static void showDataDir(Context context, AppInfo app) {

    }

    public static void createShortcut(Context context, AppInfo app) {

    }

    public static void viewManifestFile(Context context, AppInfo app) {

    }

    public static void gotoPlayStore(Context context, AppInfo app) {

    }

    // TODO todos
    // show services/providers/receivers/permissions/activities
    // kill process, show memory, show cpuinfo
    // install binary, move to system, uninstall system
    // apkBackup app data, batch install /uninstall
    // shutdown, restart, remount, screenshot
    // kill notifications, kill auto start
    // disable component
    // restart package / real kill: kill process
    //Process.sendSignal(pid, Process.SIGNAL_KILL);
    //ActivityManager.killBackgroundProcesses(PackageName)

    // android:name="android.permission.GET_TASKS"
    //android:name="android.permission.KILL_BACKGROUND_PROCESSES"

    // 删除系统程序，三步：删除apk/odex，删除数据，删除dalvikcache


}
