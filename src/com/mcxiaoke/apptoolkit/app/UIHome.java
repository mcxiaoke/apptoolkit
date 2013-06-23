package com.mcxiaoke.apptoolkit.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.google.analytics.tracking.android.EasyTracker;
import com.mcxiaoke.apptoolkit.AppConfig;
import com.mcxiaoke.apptoolkit.R;
import com.mcxiaoke.apptoolkit.adapter.BaseArrayAdapter;
import com.mcxiaoke.apptoolkit.cache.CacheManager;
import com.mcxiaoke.apptoolkit.callback.IPackageMonitor;
import com.mcxiaoke.apptoolkit.fragment.BaseFragment;
import com.mcxiaoke.apptoolkit.fragment.PackageListFragment;
import com.mcxiaoke.apptoolkit.receiver.PackageMonitor;

import java.util.List;

/**
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager.app
 * User: com.mcxiaoke
 * Date: 13-6-10
 * Time: 下午9:18
 */
public class UIHome extends UIBaseSupport implements IPackageMonitor, ActionBar.OnNavigationListener {
    private static final int MSG_PACKAGE_REMOVED = 0;
    private BaseFragment mFragment;
    private UIHome mContext;
    private PackageMonitor mPackageMonitor;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.main);
        hideProgressIndicator();
        debug("onCreate()");
//        setActionBar();
        mPackageMonitor = new PackageMonitor();
        mPackageMonitor.register(this, this, false);
        addAppListFragment();
//        new LoadRunningProcessTask(this, null).start(new TaskMessage());

    }

    private void setActionBar() {
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        Context context = ab.getThemedContext();
        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.navigation, android.R.layout.simple_spinner_item);
        list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ab.setListNavigationCallbacks(list, this);
    }

    private void addAppListFragment() {
        debug("addAppListFragment()");
        mFragment = PackageListFragment.newInstance(AppConfig.TYPE_USER_APP_MANAGER, false);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, mFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        debug("onCreateOptionsMenu()");
        menu.clear();
        getSupportMenuInflater().inflate(R.menu.menu_home, menu);
        if (isRefreshing()) {
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.action_bar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refresh();
                return true;
            case R.id.menu_settings:
                showSettings();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    protected void onHomeClick() {
    }

    private void refresh() {
        if (mFragment != null && mFragment.isVisible()) {
            debug("refresh()");
            mFragment.refresh();
        }
    }

    private void showSettings() {
        Intent intent = new Intent(this, UISettings.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        debug("onDestroy()");
        if (mPackageMonitor != null) {
            mPackageMonitor.unregister();
            mPackageMonitor = null;
        }
        CacheManager.getInstance().clear();
    }

    @Override
    protected boolean hasRefreshAction() {
        return true;
    }

    @Override
    protected void debug(String message) {
        super.debug(message);
    }

    @Override
    public void onPackageAdded(String packageName, int uid) {
        debug("onPackageAdded() packageName=" + packageName + " uid=" + uid);
    }

    @Override
    public void onPackageChanged(String packageName, int uid, String[] components) {
        debug("onPackageChanged() packageName=" + packageName + " uid=" + uid);
    }

    @Override
    public void onPackageModified(String packageName) {
        debug("onPackageModified() packageName=" + packageName);
    }

    @Override
    public void onPackageRemoved(String packageName, int uid) {
        debug("onPackageRemoved() packageName=" + packageName + " uid=" + uid);
        if (mFragment != null && mFragment.isVisible() && mFragment instanceof IPackageMonitor) {
            ((PackageListFragment) mFragment).onPackageRemoved(packageName, uid);
        }
    }

    @Override
    public void onUidRemoved(int uid) {
        debug("onUidRemoved() uid=" + uid);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return true;
    }

    static class ListNavigationAdapter extends BaseArrayAdapter<String> {

        ListNavigationAdapter(Context context, List<String> objects) {
            super(context, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}
