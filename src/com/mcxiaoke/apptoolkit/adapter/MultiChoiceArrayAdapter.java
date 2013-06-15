package com.mcxiaoke.apptoolkit.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import com.mcxiaoke.apptoolkit.AppContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: apptoolkit
 * Package: com.mcxiaoke.apptoolkit.adapter
 * User: mcxiaoke
 * Date: 13-6-15
 * Time: 下午10:23
 */
public abstract class MultiChoiceArrayAdapter<T> extends BaseArrayAdapter<T> {

    public interface OnCheckedListener {

        public void onCheckedChanged(int position, boolean isChecked);

    }

    private boolean mInActionMode;
    private OnCheckedListener mOnCheckedListener;
    private SparseBooleanArray mCheckState;

    public MultiChoiceArrayAdapter(Context context, List<T> objects) {
        super(context, objects);
        initialize();
    }

    public MultiChoiceArrayAdapter(Context context, List<T> objects, OnCheckedListener mOnCheckedListener) {
        super(context, objects);
        this.mOnCheckedListener = mOnCheckedListener;
        initialize();
    }

    private void initialize() {
        mCheckState = new SparseBooleanArray();
    }

    public void setOnCheckedListener(OnCheckedListener listener) {
        this.mOnCheckedListener = listener;
    }

    protected void onChecked(int position, boolean isChecked) {
        if (mOnCheckedListener != null) {
            mOnCheckedListener.onCheckedChanged(position, isChecked);
        }
    }

    public void setAllChecked() {
        int count = getCount();
        for (int i = 0; i < count; i++) {
            mCheckState.put(i, true);
        }
        notifyDataSetChanged();
        printChecked();
    }

    public boolean isChecked(int position) {
        return mCheckState.get(position);
    }


    protected void setChecked(int position, boolean checked) {
        AppContext.v("setChecked() position=" + position + " checked=" + checked);
        mCheckState.put(position, checked);
        notifyDataSetChanged();
    }

    public void clearChecked() {
        mCheckState.clear();
        notifyDataSetChanged();
        printChecked();
    }

    public void toggleChecked(int position) {
        boolean checked = mCheckState.get(position);
        AppContext.v("toggleChecked() position=" + position + " original checked=" + checked);
        mCheckState.put(position, !checked);
        notifyDataSetChanged();
    }

    public void setActionMode(boolean actionMode) {
        AppContext.v("setActionMode() actionMode=" + actionMode);
        mInActionMode = actionMode;
        if (!mInActionMode) {
            clearChecked();
        }
    }

    protected boolean isInActionMode() {
        return mInActionMode;
    }

    public List<T> getCheckedItems() {
        List<T> items = new ArrayList<T>();
        int count = getCount();
        for (int i = 0; i < count; i++) {
            T item = getItem(i);
            boolean checked = mCheckState.get(i);
            if (checked) {
                items.add(item);
            }
        }
        printChecked();
        return items;
    }

    public List<Integer> getCheckedPositions() {
        int count = getCount();
        List<Integer> positions = new ArrayList<Integer>();
        for (int i = 0; i < count; i++) {
            boolean checked = mCheckState.get(i);
            if (checked) {
                positions.add(i);
            }
        }
        return positions;
    }

    public int getCheckedCount() {
        int count = getCount();
        int checkedCount = 0;
        for (int i = 0; i < count; i++) {
            if (mCheckState.get(i)) {
                checkedCount++;
            }
        }
        return checkedCount;
    }


    private void printChecked() {
        int size = getCount();
        for (int i = 0; i < size; i++) {
            T item = getItem(i);
            boolean checked = isChecked(i);
            if (checked) {
                AppContext.v(" index: " + i + " checked: " + checked + " item: " + item);
            }
        }
    }

}
