package com.mcxiaoke.apptoolkit.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcxiaoke.apptoolkit.R;
import com.mcxiaoke.apptoolkit.cache.CacheManager;
import com.mcxiaoke.apptoolkit.model.AppInfo;
import com.mcxiaoke.apptoolkit.util.Utils;

import java.util.List;

/**
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager.adapter
 * User: com.mcxiaoke
 * Date: 13-6-10
 * Time: 下午5:48
 */
public class AppListAdapter extends MultiChoiceArrayAdapter<AppInfo> {
    private PackageManager mPackageManager;
    private CacheManager mIconCache;
    private int mActivateBgResId;

    public AppListAdapter(Context context, List<AppInfo> objects) {
        super(context, objects);
        mPackageManager = context.getPackageManager();
        mIconCache = CacheManager.getInstance();
        mActivateBgResId = R.drawable.list_item_activated;
    }

    @Override
    public void clear() {
        super.clear();
        uncheckAll();
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
        final AppInfo app = getItem(position);
        final boolean checked = isChecked(position);
        final View view = convertView;

        holder.appName.setText(app.appName);
        holder.sourceDir.setText(app.sourceDir);
        holder.version.setText(app.versionName);
        holder.size.setText(Utils.getHumanReadableByteCount(app.size));
        holder.time.setText(Utils.formatDate(app.createdAt));
        Drawable icon = getIcon(app);
        if (icon != null) {
            holder.icon.setImageDrawable(icon);
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setChecked(index, isChecked);
                onChecked(index, isChecked);
                changeBackground(view, isChecked);
            }
        });
        holder.checkBox.setChecked(checked);

        changeBackground(convertView, checked);

        return convertView;
    }

    private void changeBackground(View view, boolean checked) {
        if (checked) {
            view.setBackgroundResource(mActivateBgResId);
        } else {
            view.setBackgroundResource(0);
        }
    }

    private Drawable getIcon(final AppInfo app) {
        return mIconCache.getIcon(app.packageName);
    }

    static class ViewHolder {
        ImageView icon;
        TextView appName;
        TextView sourceDir;
        TextView version;
        TextView size;
        TextView time;
        CheckBox checkBox;
    }


}
