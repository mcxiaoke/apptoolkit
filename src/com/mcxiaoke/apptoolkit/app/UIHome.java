package com.mcxiaoke.apptoolkit.app;

import android.os.Bundle;
import com.mcxiaoke.apptoolkit.R;
import com.mcxiaoke.apptoolkit.cache.AppIconCache;
import com.mcxiaoke.apptoolkit.fragment.AppListFragment;
import com.mcxiaoke.apptoolkit.fragment.BaseFragment;
import com.mcxiaoke.apptoolkit.receiver.PackageCallback;
import com.mcxiaoke.apptoolkit.receiver.PackageMonitor;
import com.mcxiaoke.shell.Shell;

/**
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager.app
 * User: com.mcxiaoke
 * Date: 13-6-10
 * Time: 下午9:18
 */
public class UIHome extends UIBaseSupport implements PackageCallback {
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
        debug("onCreate()");
        mPackageMonitor = new PackageMonitor();
        mPackageMonitor.register(this, this, false);
        addAppListFragment();
        if (Shell.SU.available()) {
            debug("Root Access Granted");
        } else {
            debug("Root Access Not Granted");
        }
    }

    private void addAppListFragment() {
        debug("addAppListFragment()");
        mFragment = new AppListFragment();
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    private void refresh() {
        if (mFragment != null && mFragment.isVisible()) {
            debug("refresh()");
            mFragment.refresh();
        }
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
    protected void onDestroy() {
        super.onDestroy();
        debug("onDestroy()");
        if (mPackageMonitor != null) {
            mPackageMonitor.unregister();
            mPackageMonitor = null;
        }
        AppIconCache.getInstance().clear();
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
    }

    @Override
    public void onUidRemoved(int uid) {
        debug("onUidRemoved() uid=" + uid);
    }
}
