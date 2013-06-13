package com.mcxiaoke.appmanager.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import com.mcxiaoke.appmanager.model.AppInfo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager.util
 * User: com.mcxiaoke
 * Date: 13-6-10
 * Time: 下午5:29
 */
public final class Utils {

    public static Bitmap getAppIcon(Context context, String appPath) {
        File file = new File(appPath);
        if (file.getPath().endsWith(".apk")) {
            String filePath = file.getPath();
            PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                if (Build.VERSION.SDK_INT >= 8) {
                    appInfo.sourceDir = filePath;
                    appInfo.publicSourceDir = filePath;
                }
                Drawable icon = appInfo.loadIcon(context.getPackageManager());
                return ((BitmapDrawable) icon).getBitmap();
            }
        }
        return null;
    }

    public static Drawable getAppIcon(PackageManager pm, PackageInfo info) {
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            return pm.getApplicationIcon(appInfo);
//            Drawable icon = appInfo.loadIcon(context.getPackageManager());
        }
        return null;
    }

    public static AppInfo convert(PackageManager pm, String packageName) {
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            return convert(pm, packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static AppInfo convert(PackageManager pm, PackageInfo info) {
        AppInfo app = new AppInfo();
        ApplicationInfo ainfo = info.applicationInfo;

        Intent intent = pm.getLaunchIntentForPackage(info.packageName);
        if (intent != null) {
            ComponentName cn = intent.getComponent();
            try {
                ActivityInfo ai = pm.getActivityInfo(cn, 0);
                if (ai != null) {
                    app.mainName = ai.loadLabel(pm).toString();
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        app.appName = pm.getApplicationLabel(ainfo).toString();
        app.processName = ainfo.processName;
        app.sourceDir = ainfo.sourceDir;
        app.publicSourceDir = ainfo.publicSourceDir;
        app.dataDir = ainfo.dataDir;
        app.uid = ainfo.uid;
        app.packageName = info.packageName;
        app.versionCode = info.versionCode;
        app.versionName = info.versionName;
        app.createdAt = info.firstInstallTime;
        app.updatedAt = info.lastUpdateTime;

        app.system = app.sourceDir.startsWith(AppInfo.SYSTEM_PATH_PREFIX);
        app.size = new File(app.sourceDir).length();

        return app;

    }

    public static StringBuilder dumpPackageInfo(PackageManager pm, final PackageInfo info) {
        StringBuilder builder = new StringBuilder();
        ApplicationInfo app = info.applicationInfo;

        Intent intent = pm.getLaunchIntentForPackage(info.packageName);
        if (intent != null) {
            ComponentName cn = intent.getComponent();
            try {
                ActivityInfo ai = pm.getActivityInfo(cn, 0);
                builder.append("mainName: ").append(ai.loadLabel(pm)).append("\n");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        builder.append("appName: ").append(pm.getApplicationLabel(app)).append("\n");
        builder.append("processName: ").append(app.processName).append("\n");
        builder.append("sourceDir: ").append(app.sourceDir).append("\n");
        builder.append("dataDir: ").append(app.dataDir).append("\n");
        builder.append("uid: ").append(app.uid).append("\n");

        builder.append("packageName: ").append(info.packageName).append("\n");
        builder.append("versionCode: ").append(info.versionCode).append("\n");
        builder.append("versionName: ").append(info.versionName).append("\n");
//        builder.append("sharedUserId: ").append(info.sharedUserId).append("\n");
//        builder.append("firstInstallTime: ").append(new Date(info.firstInstallTime)).append("\n");
//        builder.append("lastUpdateTime: ").append(new Date(info.lastUpdateTime)).append("\n");

//        builder.append("requestedPermissions: ").append(dumpPermissions(info)).append("\n");
//        builder.append("permissions: ").append(info.permissions).append("\n");
//        builder.append("gids: ").append(info.gids).append("\n");
//        builder.append("activities: ").append(dumpActivityInfos(info)).append("\n");
//        builder.append("receivers: ").append(dumpReceiverInfos(info)).append("\n");
//        builder.append("services: ").append(dumpServiceInfos(info)).append("\n");
//        builder.append("providers: ").append(dumpProviderInfos(info)).append("\n");
//        builder.append("signatures: ").append(dumpSignatures(info)).append("\n");
//        builder.append("configPreferences: ").append(info.configPreferences).append("\n");
//        builder.append("reqFeatures: ").append(info.reqFeatures).append("\n");

        return builder;
    }

    public static String dumpPermissions(PackageInfo info) {
        String[] permissions = info.requestedPermissions;
        StringBuilder builder = new StringBuilder();
        if (permissions != null && permissions.length > 0) {
            for (String permission : permissions) {
                builder.append("Permission: ").append(permission).append("\n");
            }
        }
        return builder.toString();
    }

    public static String dumpActivityInfos(PackageInfo info) {
        ComponentInfo[] activities = info.activities;
        StringBuilder builder = new StringBuilder();
        if (activities != null && activities.length > 0) {
            for (ComponentInfo a : activities) {
                builder.append("Activity: ").append(a.packageName).append(".").append(a.name).append("\n");
            }
        }
        return builder.toString();
    }

    public static String dumpReceiverInfos(PackageInfo info) {
        ComponentInfo[] receivers = info.receivers;
        StringBuilder builder = new StringBuilder();
        if (receivers != null && receivers.length > 0) {
            for (ComponentInfo a : receivers) {
                builder.append("Receiver: ").append(a.packageName).append(".").append(a.name).append("\n");
            }
        }
        return builder.toString();
    }

    public static String dumpServiceInfos(PackageInfo info) {
        ComponentInfo[] services = info.services;
        StringBuilder builder = new StringBuilder();
        if (services != null && services.length > 0) {
            for (ComponentInfo a : services) {
                builder.append("Service: ").append(a.packageName).append(".").append(a.name).append("\n");
            }
        }
        return builder.toString();
    }

    public static String dumpProviderInfos(PackageInfo info) {
        ComponentInfo[] services = info.providers;
        StringBuilder builder = new StringBuilder();
        if (services != null && services.length > 0) {
            for (ComponentInfo a : services) {
                builder.append("Provider: ").append(a.packageName).append(".").append(a.name).append("\n");
            }
        }
        return builder.toString();
    }

    public static String dumpSignatures(PackageInfo info) {
        Signature[] signatures = info.signatures;
        StringBuilder builder = new StringBuilder();
        if (signatures != null && signatures.length > 0) {
            for (Signature a : signatures) {
                builder.append("Signature: ").append(getSignatureString(a)).append("\n");
            }
        }
        return builder.toString();
    }

    public static String getSignatureString(Signature signature) {
        Certificate certificate = getCertificate(signature);
        StringBuilder builder = new StringBuilder();
        if (certificate != null) {
            PublicKey key = certificate.getPublicKey();
            builder.append("Type: ").append(certificate.getType()).append(" Algorithm: ").append(key.getAlgorithm()).append(" Format: ").append(key.getFormat());
        }
        return builder.toString();

    }

    public static Certificate getCertificate(Signature signature) {
        try {
            byte[] bytes = signature.toByteArray();
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            final Certificate cert = certFactory.generateCertificate(bais);
            return cert;
        } catch (CertificateException e) {
            return null;
        }
    }

    public static PublicKey getPublicKey(Signature signature) {
        Certificate certificate = getCertificate(signature);
        if (certificate != null) {
            return certificate.getPublicKey();
        }
        return null;
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static String formatDate(long time) {
        Date date = new Date(time);
        return DATE_FORMAT.format(date);
    }

    public static String getHumanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static boolean copyFile(File src, File dest) {
        try {
            FileChannel srcChannel = new FileInputStream(src).getChannel();
            FileChannel destChannel = new FileOutputStream(dest).getChannel();
            srcChannel.transferTo(0, srcChannel.size(), destChannel);
            srcChannel.close();
            destChannel.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

        }
        return false;
    }

    public static String buildApkName(AppInfo app) {
        return new StringBuilder().append(app.appName).append(".").append(app.versionName).append(".apk").toString();
    }

    public static boolean isPackageAlreadyInstalled(Activity context, String pkgName) {
        List<PackageInfo> installedList = context.getPackageManager().getInstalledPackages(
                PackageManager.GET_UNINSTALLED_PACKAGES);
        int installedListSize = installedList.size();
        for (int i = 0; i < installedListSize; i++) {
            PackageInfo tmp = installedList.get(i);
            if (pkgName.equalsIgnoreCase(tmp.packageName)) {
                return true;
            }
        }
        return false;
    }


}
