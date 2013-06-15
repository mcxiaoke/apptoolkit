package com.mcxiaoke.apptoolkit.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcxiaoke.apptoolkit.R;
import com.mcxiaoke.apptoolkit.model.AppAction;

import java.util.List;

/**
 * Project: filemanager
 * Package: com.mcxiaoke.appmanager.adapter
 * User: mcxiaoke
 * Date: 13-6-12
 * Time: 下午11:00
 */
public class AppActionsAdapter extends BaseArrayAdapter<AppAction> {

    public AppActionsAdapter(Context context, List<AppAction> objects) {
        super(context, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_action, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.action);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(getItem(position).name);
        return convertView;
    }

    private static class ViewHolder {
        TextView textView;
    }


}
