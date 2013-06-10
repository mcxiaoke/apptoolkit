package com.mcxiaoke.appmanager.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.mcxiaoke.appmanager.AppContext;

/**
 * Project: filemanager
 * Package: com.mcxiaoke.appmanager.model
 * User: mcxiaoke
 * Date: 13-6-10
 * Time: 下午9:21
 */
public abstract class BaseModel implements Parcelable {

    public long getId() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public String jsonString() {
        return AppContext.getGson().toJson(this);
    }
}
