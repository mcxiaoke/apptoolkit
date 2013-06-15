package com.mcxiaoke.apptoolkit.model;

import android.os.Parcel;
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
    public String mainName;
    @Expose
    public String appName;
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

    public AppInfo() {
    }

    public AppInfo(Parcel in) {
        this.mainName = in.readString();
        this.appName = in.readString();
        this.processName = in.readString();
        this.packageName = in.readString();
        this.versionName = in.readString();
        this.versionCode = in.readInt();
        this.sourceDir = in.readString();
        this.publicSourceDir = in.readString();
        this.dataDir = in.readString();
        this.uid = in.readInt();
        this.enabled = (in.readByte() == 1);
        this.system = (in.readByte() == 1);
        this.size = in.readLong();
        this.createdAt = in.readLong();
        this.updatedAt = in.readLong();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mainName);
        dest.writeString(this.appName);
        dest.writeString(this.processName);
        dest.writeString(this.packageName);
        dest.writeString(this.versionName);
        dest.writeInt(this.versionCode);
        dest.writeString(this.sourceDir);
        dest.writeString(this.publicSourceDir);
        dest.writeString(this.dataDir);
        dest.writeInt(this.uid);
        dest.writeByte((byte) (this.enabled ? 1 : 0));
        dest.writeByte((byte) (this.enabled ? 1 : 0));
        dest.writeLong(this.size);
        dest.writeLong(this.createdAt);
        dest.writeLong(this.updatedAt);
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel source) {
            return new AppInfo(source);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

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
