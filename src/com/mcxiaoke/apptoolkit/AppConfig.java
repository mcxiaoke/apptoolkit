package com.mcxiaoke.apptoolkit;

/**
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager
 * User: com.mcxiaoke
 * Date: 13-6-11
 * Time: 下午1:20
 */
public class AppConfig {

    public static final String APP_DIR = "AppToolKit";
    public static final String BACKUP_DIR = "apps";

    public static final String EXTRA_APPINFO = "extra_appinfo";
    public static final String EXTRA_ID = "extra_ID";
    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_TEXT = "extra_text";
    public static final String EXTRA_COUNT = "extra_count";
    public static final String EXTRA_FLAG = "extra_flag";
    public static final String EXTRA_ADVANCED = "extra_advanced";
    public static final String EXTRA_SYSTEM = "extra_system";
    public static final String EXTRA_PACKAGE = "extra_package";
    public static final String EXTRA_STRING_LIST = "extra_string_list";
    public static final String EXTRA_CACHE_ID = "extra_cache_id";

    public static final int TYPE_USER_APP_MANAGER = 0;
    public static final int TYPE_SYSTEM_APP_MANAGER = 2;
    public static final int TYPE_PROCESS_MANAGER = 4;
    public static final int TYPE_DATA_MANAGER = 6;
    public static final int TYPE_CACHE_MANAGER = 8;
    public static final int TYPE_COMPONENT_MANAGER = 10;
    public static final int TYPE_FILE_MANAGER = 12;

    public static final int DOMAIN_ANDROID = 101;
    public static final int DOMAIN_GOOGLE = 102;
    public static final int DOMAIN_WHITELIST = 103;
    public static final int DOMAIN_BLACKLIST = 104;
    public static final int DOMAIN_NORMAL = 105;

    public static final String DOMAIN_NAME_ANDROID = "Android";
    public static final String DOMAIN_NAME_GOOGLE = "Google";
    public static final String DOMAIN_NAME_WHITELIST = "Whitelist";
    public static final String DOMAIN_NAME_BLACKLIST = "Blacklist";
    public static final String DOMAIN_NAME_NORMAL = "Normal";


    /**
     * The patch for some android version and devices. Install may fail without
     * this patch.
     */
    public static final String COMMAND_INSTALL_PATCH = "LD_LIBRARY_PATH=/vendor/lib:/system/lib ";

    public static final String SETTINGS_PACKAGE = "com.android.settings";

    public static final String SYSTEM_APP_PATH = "/system/app/";
    public static final String USER_APP_PATH = "/data/app/";
    public static final String APP_DATA_PATH = "/data/data/";
    public static final String BUSYBOX_PATH = "/system/xbin/busybox";
    public static final String ANDROID_APP_PACKAGE_PREFIX = "com.android.";
    public static final String GOOGLE_APP_PACKAGE_PREFIX = "com.google.android";

    public static final int UID_ROOT = 0;
    public static final int UID_SYSTEM = 1000;
}
