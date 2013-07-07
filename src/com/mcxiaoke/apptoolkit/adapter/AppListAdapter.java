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
    private CacheManager mCacheManager;
    private int mActivateBgResId;

    public AppListAdapter(Context context, List<AppInfo> objects) {
        super(context, objects);
        mPackageManager = context.getPackageManager();
        mCacheManager = CacheManager.getInstance();
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
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final int index = position;
        final AppInfo app = getItem(position);
        final boolean checked = isChecked(position);
        final View view = convertView;

        holder.subtitle.setText(app.domain == AppConfig.DOMAIN_GOOGLE ? "Google" : "");
        holder.subtitle.setVisibility(View.GONE);

        holder.info1.setText("package:" + app.packageName);
        holder.info1.setVisibility(View.GONE);

        holder.label1.setText("APK");
        holder.label1.setVisibility(app.apkBackup ? View.VISIBLE : View.INVISIBLE);
        holder.label2.setText("DATA");
        holder.label2.setVisibility(app.dataBackup ? View.VISIBLE : View.INVISIBLE);

        holder.title.setText(app.appName);
        String versionName = app.versionName == null ? "v1.0" : "v" + app.versionName;
        holder.text1.setText(Utils.getHumanReadableByteCount(app.size) + " | " + versionName);

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
        return mCacheManager.getIcon(app.packageName);
    }

    static class ViewHolder {

        ImageView icon;
        TextView title;
        TextView subtitle;
        TextView text1;
        TextView label1;
        TextView label2;
        TextView info1;
        CheckBox checkBox;

        ViewHolder(View convertView) {
            icon = (ImageView) convertView.findViewById(R.id.icon);
            title = (TextView) convertView.findViewById(R.id.title);
            subtitle = (TextView) convertView.findViewById(R.id.subtitle);
            text1 = (TextView) convertView.findViewById(R.id.text1);
            label1 = (TextView) convertView.findViewById(R.id.label1);
            label2 = (TextView) convertView.findViewById(R.id.label2);
            info1 = (TextView) convertView.findViewById(R.id.info1);
            checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
        }
    }


}
