package com.mcxiaoke.apptoolkit.task;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Project: apptoolkit
 * Package: com.mcxiaoke.apptoolkit.task
 * User: mcxiaoke
 * Date: 13-6-16
 * Time: 下午1:58
 */
public final class TaskMessage implements Parcelable {
    public int id;
    public int type;
    public int arg1;
    public int arg2;
    public boolean flag;
    public String message;
    public Bundle extras;
    public Object object;

    public TaskMessage() {
    }

    public TaskMessage(Parcel in) {
        this.id = in.readInt();
        this.type = in.readInt();
        this.arg1 = in.readInt();
        this.arg2 = in.readInt();
        this.flag = (in.readInt() == 1);
        this.message = in.readString();
        this.extras = in.readBundle();

        if (in.readInt() != 0) {
            object = in.readParcelable(getClass().getClassLoader());
        }

    }

    public TaskMessage(int type, boolean flag) {
        this.type = type;
        this.flag = flag;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AsyncTaskParam{");
        sb.append(", id=").append(id);
        sb.append(", type=").append(type);
        sb.append(", flag=").append(flag);
        sb.append(", arg1=").append(arg1);
        sb.append(", arg2=").append(arg2);
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.type);
        dest.writeInt(this.arg1);
        dest.writeInt(this.arg2);
        dest.writeInt(this.flag ? 1 : 0);
        dest.writeString(this.message);
        dest.writeBundle(this.extras);

        if (object != null) {
            try {
                Parcelable p = (Parcelable) object;
                dest.writeInt(1);
                dest.writeParcelable(p, flags);
            } catch (ClassCastException e) {
                throw new RuntimeException(
                        "Can't marshal non-Parcelable objects across processes.");
            }
        } else {
            dest.writeInt(0);
        }
    }

    public static final Creator<TaskMessage> CREATOR = new Creator<TaskMessage>() {
        @Override
        public TaskMessage createFromParcel(Parcel source) {
            return new TaskMessage(source);
        }

        @Override
        public TaskMessage[] newArray(int size) {
            return new TaskMessage[size];
        }
    };
}
