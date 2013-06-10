package com.mcxiaoke.appmanager.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcxiaoke.appmanager.R;
import com.mcxiaoke.appmanager.model.AppInfo;
import com.mcxiaoke.appmanager.util.Utils;

import java.util.HashMap;
import java.util.List;

/**
 * Project: filemanager
 * Package: com.mcxiaoke.appmanager.adapter
 * User: mcxiaoke
 * Date: 13-6-10
 * Time: 下午5:48
 */
public class AppInfosAdapter extends ArrayAdapter<AppInfo> {
    private Context mContext;
    private LayoutInflater mInflater;
    private PackageManager mPackageManager;
    private HashMap<String, Drawable> mIconCache;

    public AppInfosAdapter(Context context, List<AppInfo> objects) {
        super(context, 0, objects);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mPackageManager = context.getPackageManager();
        mIconCache = new HashMap<String, Drawable>();
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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final AppInfo app = getItem(position);
        holder.appName.setText(app.appName);
        holder.sourceDir.setText(app.sourceDir);
        holder.version.setText(app.versionName);
        holder.size.setText(Utils.getHumanReadableByteCount(app.size));
        holder.time.setText(Utils.formatDate(app.createdAt));
        Drawable icon = getIcon(app);
        if (icon != null) {
            holder.icon.setImageDrawable(icon);
        }
        return convertView;
    }

    private String getInfo(int position) {
        AppInfo info = getItem(position);
        return info.toString();
    }

    private Drawable getIcon(AppInfo app) {
        Drawable icon = mIconCache.get(app.packageName);
        if (icon == null) {
            try {
                PackageInfo info = mPackageManager.getPackageInfo(app.packageName, 0);
                icon = Utils.getAppIcon(mPackageManager, info);
                if (icon != null) {
                    mIconCache.put(app.packageName, icon);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return icon;
    }

    static class ViewHolder {
        ImageView icon;
        TextView appName;
        TextView sourceDir;
        TextView version;
        TextView size;
        TextView time;
    }


}
