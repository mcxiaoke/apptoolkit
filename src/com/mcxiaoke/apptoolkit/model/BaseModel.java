package com.mcxiaoke.apptoolkit.model;

import android.os.Parcelable;
import com.mcxiaoke.apptoolkit.AppContext;

/**
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager.model
 * User: com.mcxiaoke
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

    public String jsonString() {
        return AppContext.getApp().getGson().toJson(this);
    }
}
