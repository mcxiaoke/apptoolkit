package com.mcxiaoke.appmanager.util;

import android.content.Context;
import com.mcxiaoke.appmanager.model.AppInfo;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager.util
 * User: com.mcxiaoke
 * Date: 13-6-11
 * Time: 上午11:41
 */
public class RootUtils {

    private static boolean makeReadable(String path) {
        boolean result = false;
        CommandCapture chmodCmd = new CommandCapture(0, "chmod 777 " + path);
        try {
            RootTools.getShell(true).add(chmodCmd).waitForFinish();
            result = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (RootDeniedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void silentInstall(Context context, AppInfo app) {

    }

    public static void silentUninstall(Context context, AppInfo app) {

    }
}
