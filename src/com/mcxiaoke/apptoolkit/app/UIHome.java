package com.mcxiaoke.apptoolkit.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.mcxiaoke.apptoolkit.AppContext;
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
        mPackageMonitor = new PackageMonitor();
        mPackageMonitor.register(this, this, false);
        addAppListFragment();
        if (Shell.SU.available()) {
            AppContext.v("Root Access Granted");
        } else {
            AppContext.v("Root Access Not Granted");
            AppContext.showToast(mContext, R.string.root_access_failed);
        }
    }

    private void addAppListFragment() {
        mFragment = new AppListFragment();
        getFragmentManager().beginTransaction().replace(R.id.container, mFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_home, menu);
        if (isRefreshing()) {
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.action_bar_indeterminate_progress);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void refresh() {
        if (mFragment != null && mFragment.isVisible()) {
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
