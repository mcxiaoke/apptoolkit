package com.mcxiaoke.apptoolkit.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcxiaoke.apptoolkit.AppContext;
import com.mcxiaoke.apptoolkit.R;
import com.mcxiaoke.apptoolkit.cache.AppIconCache;
import com.mcxiaoke.apptoolkit.model.AppInfo;
import com.mcxiaoke.apptoolkit.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager.adapter
 * User: com.mcxiaoke
 * Date: 13-6-10
 * Time: 下午5:48
 */
public class AppListAdapter extends BaseArrayAdapter<AppInfo> {
    private PackageManager mPackageManager;
    private AppIconCache mIconCache;
    private SparseBooleanArray mCheckState;
    private boolean mInActionMode;
    private int mActivateColor;
    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;

    public AppListAdapter(Context context, List<AppInfo> objects) {
        super(context, objects);
        mPackageManager = context.getPackageManager();
        mIconCache = AppIconCache.getInstance();
        mCheckState = new SparseBooleanArray();
        mActivateColor = mContext.getResources().getColor(R.color.holo_primary_transparent);
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    @Override
    public void clear() {
        super.clear();
        mCheckState.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_app, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.appName = (TextView) convertView.findViewById(R.id.app_name);
            holder.sourceDir = (TextView) convertView.findViewById(R.id.source_dir);
            holder.version = (TextView) convertView.findViewById(R.id.version);
            holder.size = (TextView) convertView.findViewById(R.id.size);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final int index = position;
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setChecked(index, isChecked);
                onCheckBoxClicked(buttonView, isChecked);
            }
        });
        final AppInfo app = getItem(position);
        final boolean checked = mCheckState.get(position);
        holder.appName.setText(app.appName);
        holder.sourceDir.setText(app.sourceDir);
        holder.version.setText(app.versionName);
        holder.size.setText(Utils.getHumanReadableByteCount(app.size));
        holder.time.setText(Utils.formatDate(app.createdAt));
        holder.checkBox.setChecked(checked);
        Drawable icon = getIcon(app);
        if (icon != null) {
            holder.icon.setImageDrawable(icon);
        }

        convertView.setBackgroundColor(checked ? mActivateColor : 0);
        return convertView;
    }

    private void printChecked() {
        int size = getCount();
        for (int i = 0; i < size; i++) {
            AppInfo app = getItem(i);
            boolean checked = mCheckState.get(i);
            if (checked) {
                AppContext.v(" index: " + i + " checked: " + checked + " app: " + app.appName);
            }
        }
    }

    private void setChecked(int position, boolean checked) {
        AppContext.v("toggleChecked() position=" + position + " checked=" + checked);
        mCheckState.append(position, checked);
        notifyDataSetChanged();
        printChecked();
    }

    private void onCheckBoxClicked(CompoundButton buttonView, boolean isChecked) {
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(buttonView, isChecked);
        }
    }

    public void toggleChecked(int position) {
        boolean checked = mCheckState.get(position);
        AppContext.v("toggleChecked() position=" + position + " original checked=" + checked);
        mCheckState.append(position, !checked);
        notifyDataSetChanged();
        printChecked();
    }

    public void clearChecked() {
        AppContext.v("clearChecked()");
        mCheckState.clear();
        notifyDataSetChanged();
        printChecked();
    }

    public void setActionMode(boolean actionMode) {
        AppContext.v("setActionMode() actionMode=" + actionMode);
        mInActionMode = actionMode;
        if (!mInActionMode) {
            clearChecked();
        }
    }

    public List<AppInfo> getCheckedItems() {
        List<AppInfo> apps = new ArrayList<AppInfo>();
        int size = getCount();
        for (int i = 0; i < size; i++) {
            AppInfo app = getItem(i);
            boolean checked = mCheckState.get(i);
            if (checked) {
                apps.add(app);
            }
        }
        return apps;
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

    private Drawable getIcon(final AppInfo app) {
        return mIconCache.get(app.packageName);
    }

    private static class ViewHolder {
        ImageView icon;
        TextView appName;
        TextView sourceDir;
        TextView version;
        TextView size;
        TextView time;
        CheckBox checkBox;
    }


}
