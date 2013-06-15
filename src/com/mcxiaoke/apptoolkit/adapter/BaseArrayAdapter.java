package com.mcxiaoke.apptoolkit.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Project: filemanager
 * Package: com.mcxiaoke.appmanager.adapter
 * User: mcxiaoke
 * Date: 13-6-12
 * Time: 下午11:00
 */
public abstract class BaseArrayAdapter<T> extends ArrayAdapterCompat<T> {
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected Handler mUiHandler;

    public BaseArrayAdapter(Context context, List<T> objects) {
        super(context, objects);
        initialize(context);
    }

    private void initialize(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mUiHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);


}
