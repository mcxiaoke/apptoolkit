package com.mcxiaoke.apptoolkit.menu;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.mcxiaoke.apptoolkit.AppContext;
import com.mcxiaoke.apptoolkit.R;
import com.mcxiaoke.apptoolkit.app.UIHome;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mcxiaoke
 * @version 2.1 2012.04.24
 */
public class MenuFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final boolean DEBUG = AppContext.isDebug();
    private static final String TAG = MenuFragment.class.getSimpleName();

    static void debug(String message) {
        Log.v(TAG, message);
    }

    private static final int MENU_ID = 1000;
    public static final int MENU_ID_HOME = MENU_ID + 1;
    public static final int MENU_ID_TOOLKIT = MENU_ID + 2;
    public static final int MENU_ID_PROCESS = MENU_ID + 3;
    public static final int MENU_ID_BACKUP = MENU_ID + 4;
    public static final int MENU_ID_SYSTEM = MENU_ID + 5;
    public static final int MENU_ID_FILE = MENU_ID + 6;
    public static final int MENU_ID_SERVICE = MENU_ID + 7;
    public static final int MENU_ID_COMPONENT = MENU_ID + 8;
    public static final int MENU_ID_CACHE = MENU_ID + 10;
    public static final int MENU_ID_OPTION = MENU_ID + 49;
    public static final int MENU_ID_ABOUT = MENU_ID + 50;
    public static final int MENU_ID_DEBUG = MENU_ID + 99;

    private ListView mListView;
    private TextView mFooterTextView1;
    private TextView mFooterTextView2;
    private MenuItemListAdapter mMenuAdapter;
    private List<MenuItemResource> mMenuItems;
    private MenuCallback mCallback;
    private SparseBooleanArray mCheckedState;
    private UIHome mUiHome;

    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMenuItems = new ArrayList<MenuItemResource>();
        mCheckedState = new SparseBooleanArray();
        fillColumns();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (MenuCallback) activity;
        mUiHome = (UIHome) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fm_menu, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) getView().findViewById(android.R.id.list);
        mFooterTextView1 = (TextView) getView().findViewById(android.R.id.text1);
        mFooterTextView2 = (TextView) getView().findViewById(android.R.id.text2);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setSelector(getResources().getDrawable(
                R.drawable.selector_drawer_menu));

//        mListView.setDivider(getResources().getDrawable(
//                R.drawable.sliding_menu_list_divider));
        mMenuAdapter = new MenuItemListAdapter(getActivity(), mMenuItems);
        mListView.setOnItemClickListener(this);
        mListView.setDrawSelectorOnTop(true);
        mListView.setAdapter(mMenuAdapter);
        mListView.setItemChecked(0, true);
        mFooterTextView1.setText(AppContext.getVersionName() + " Build " + AppContext.getVersionCode() + (DEBUG ? " Debug" : ""));

    }

    @Override
    public void onResume() {
        super.onResume();
        Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out_long);
        mFooterTextView2.startAnimation(fadeOut);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFooterTextView2.clearAnimation();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final MenuItemResource menuItem = (MenuItemResource) parent
                .getItemAtPosition(position);
        debug("on item click ,position=" + position + " item=" + menuItem);
        ListView listView = (ListView) parent;
        listView.setItemChecked(position, true);
        if (menuItem != null) {
            mCheckedState.clear();
            mCheckedState.put(position, true);
            if (menuItem.highlight) {
                mMenuAdapter.setCurrentPosition(position);
            }
            mMenuAdapter.notifyDataSetChanged();
            if (mCallback != null) {
                mCallback.onMenuItemSelected(position, menuItem);
            }
        }
    }

    private void fillColumns() {
        MenuItemResource item = MenuItemResource.newBuilder().id(R.id.menu_app)
                .text(getString(R.string.menu_appmanager)).highlight(true)
                .build();
        mMenuItems.add(item);

        item = MenuItemResource.newBuilder().id(R.id.menu_backup)
                .text(getString(R.string.menu_backupmanager)).highlight(true)
                .build();
        mMenuItems.add(item);

        item = MenuItemResource.newBuilder().id(R.id.menu_process)
                .text(getString(R.string.menu_processmanager)).highlight(true)
                .build();
        mMenuItems.add(item);

/*        item = MenuItemResource.newBuilder().id(R.id.menu_component)
                .text(getString(R.string.menu_componentmanager)).highlight(true)
                .build();
        mMenuItems.add(item);*/

/*        item = MenuItemResource.newBuilder().id(R.id.menu_receiver)
                .text(getString(R.string.menu_receivermanager)).highlight(true)
                .build();
        mMenuItems.add(item);*/

/*        item = MenuItemResource.newBuilder().id(R.id.menu_service)
                .text(getString(R.string.menu_servicemanager)).highlight(true)
                .build();
        mMenuItems.add(item);*/

/*        item = MenuItemResource.newBuilder().id(R.id.menu_file)
                .text(getString(R.string.menu_filemanager)).highlight(true)
                .build();
        mMenuItems.add(item);*/
/*
        item = MenuItemResource.newBuilder().id(R.id.menu_cache)
                .text(getString(R.string.menu_cachemanager)).highlight(true)
                .build();
        mMenuItems.add(item);*/

/*        item = MenuItemResource.newBuilder().id(R.id.menu_network)
                .text(getString(R.string.menu_networkmanager)).highlight(true)
                .build();
        mMenuItems.add(item);*/

        item = MenuItemResource.newBuilder().id(R.id.menu_toolkit)
                .text(getString(R.string.menu_toolkit)).highlight(true)
                .build();
        mMenuItems.add(item);

        item = MenuItemResource.newBuilder().id(R.id.menu_about)
                .text(getString(R.string.menu_about)).highlight(true)
                .build();
        mMenuItems.add(item);

    }

    private static class MenuItemListAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;
        private List<MenuItemResource> mItems;
        private int currentPosition;

        public void setCurrentPosition(int position) {
            this.currentPosition = position;
        }

        public MenuItemListAdapter(Context context, List<MenuItemResource> data) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            this.mItems = new ArrayList<MenuItemResource>();
            if (data != null && data.size() > 0) {
                this.mItems.addAll(data);
            }
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public MenuItemResource getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_menu, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final MenuItemResource item = mItems.get(position);
            holder.icon.setImageResource(item.iconId);
            holder.icon.setVisibility(View.GONE);
            holder.text.setText(item.text);

            if (position == currentPosition && item.highlight) {
                convertView.setBackgroundResource(R.drawable.selector_drawer_menu_light_checked);
//                holder.text.setTextColor(context.getResources()
//                        .getColorStateList(R.color.light_blue_text_color));
            } else {
                convertView.setBackgroundColor(0);
//                holder.text.setTextColor(context.getResources()
//                        .getColorStateList(R.color.text_white));
            }
            return convertView;
        }

        private static class ViewHolder {
            ImageView icon;
            TextView text;

            public ViewHolder(View base) {
                icon = (ImageView) base.findViewById(R.id.icon);
                text = (TextView) base.findViewById(R.id.text);
            }
        }

    }

}
