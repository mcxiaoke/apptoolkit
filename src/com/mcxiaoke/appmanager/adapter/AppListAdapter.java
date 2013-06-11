package com.mcxiaoke.appmanager.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcxiaoke.appmanager.R;
import com.mcxiaoke.appmanager.cache.AppIconCache;
import com.mcxiaoke.appmanager.model.AppInfo;
import com.mcxiaoke.appmanager.util.Utils;

import java.util.List;

/**
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager.adapter
 * User: com.mcxiaoke
 * Date: 13-6-10
 * Time: 下午5:48
 */
public class AppListAdapter extends ArrayAdapter<AppInfo> {
    private Context mContext;
    private LayoutInflater mInflater;
    private PackageManager mPackageManager;
    private AppIconCache mIconCache;
    private Handler mUiHandler;

    public AppListAdapter(Context context, List<AppInfo> objects) {
        super(context, 0, objects);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mPackageManager = context.getPackageManager();
        mIconCache = AppIconCache.getInstance();
        mUiHandler = new Handler(Looper.getMainLooper());
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

    private Drawable getIcon(final AppInfo app) {
        return mIconCache.get(app.packageName);
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
