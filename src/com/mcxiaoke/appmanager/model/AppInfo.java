package com.mcxiaoke.appmanager.model;

import com.google.gson.annotations.Expose;

/**
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager.model
 * User: com.mcxiaoke
 * Date: 13-6-10
 * Time: 下午9:20
 */
public class AppInfo extends BaseModel {
    public static final String SYSTEM_PATH_PREFIX = "/system/";
    @Expose
    public CharSequence mainName;
    @Expose
    public CharSequence appName;
    @Expose
    public String processName;
    @Expose
    public String packageName;
    @Expose
    public String versionName;
    @Expose
    public int versionCode;
    @Expose
    public String sourceDir;
    @Expose
    public String publicSourceDir;
    @Expose
    public String dataDir;
    @Expose
    public int uid;
    @Expose
    public boolean enabled;
    @Expose
    public boolean system;
    @Expose
    public long size;
    @Expose
    public long createdAt;
    @Expose
    public long updatedAt;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AppInfo{");
        sb.append("appName='").append(appName).append('\'');
        sb.append(", mainName='").append(mainName).append('\'');
        sb.append(", processName='").append(processName).append('\'');
        sb.append(", packageName='").append(packageName).append('\'');
        sb.append(", versionName='").append(versionName).append('\'');
        sb.append(", versionCode=").append(versionCode);
        sb.append(", sourceDir='").append(sourceDir).append('\'');
        sb.append(", publicSourceDir='").append(publicSourceDir).append('\'');
        sb.append(", dataDir='").append(dataDir).append('\'');
        sb.append(", uid=").append(uid);
        sb.append(", enabled=").append(enabled);
        sb.append(", system=").append(system);
        sb.append(", size=").append(size);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append('}');
        return sb.toString();
    }
}
