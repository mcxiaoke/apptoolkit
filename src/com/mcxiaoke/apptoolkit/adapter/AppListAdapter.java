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
import com.mcxiaoke.apptoolkit.AppConfig;
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
    private boolean mAdvancedMode;

    public AppListAdapter(Context context, List<AppInfo> objects) {
        super(context, objects);
        mPackageManager = context.getPackageManager();
        mIconCache = CacheManager.getInstance();
        mActivateBgResId = R.drawable.list_item_activated;
        mAdvancedMode = false;
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
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final int index = position;
        final AppInfo app = getItem(position);
        final boolean checked = isChecked(position);
        final View view = convertView;

        if (mAdvancedMode) {
            holder.subtitle.setText(app.domain == AppConfig.DOMAIN_GOOGLE ? "Google" : "");
            holder.subtitle.setVisibility(View.VISIBLE);

            if (app.system) {
                holder.text2.setText("system");
                holder.text2.setVisibility(View.VISIBLE);
            } else {
                holder.text2.setVisibility(View.GONE);
            }

            holder.info1.setText("package:" + app.packageName);
            holder.info1.setVisibility(View.VISIBLE);

            if (app.apkBackup) {
                holder.info2.setText("Backup");
                holder.info2.setVisibility(View.VISIBLE);
            } else {
                holder.info2.setVisibility(View.GONE);
            }

        } else {
            holder.subtitle.setVisibility(View.GONE);
            holder.text2.setVisibility(View.GONE);
            holder.info1.setVisibility(View.GONE);
            holder.info2.setVisibility(View.GONE);
        }

        holder.title.setText(app.appName);
        holder.text1.setText(Utils.getHumanReadableByteCount(app.size) + " | v" + app.versionName);

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
        TextView title;
        TextView subtitle;
        TextView text1;
        TextView text2;
        TextView info1;
        TextView info2;
        CheckBox checkBox;

        ViewHolder(View convertView) {
            icon = (ImageView) convertView.findViewById(R.id.icon);
            title = (TextView) convertView.findViewById(R.id.title);
            subtitle = (TextView) convertView.findViewById(R.id.subtitle);
            text1 = (TextView) convertView.findViewById(R.id.text1);
            text2 = (TextView) convertView.findViewById(R.id.text2);
            info1 = (TextView) convertView.findViewById(R.id.info1);
            info2 = (TextView) convertView.findViewById(R.id.info2);
            checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
        }
    }


}
