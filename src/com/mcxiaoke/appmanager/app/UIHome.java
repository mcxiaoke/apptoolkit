package com.mcxiaoke.appmanager.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.mcxiaoke.appmanager.AppContext;
import com.mcxiaoke.appmanager.R;
import com.mcxiaoke.appmanager.cache.AppIconCache;
import com.mcxiaoke.appmanager.fragment.AppListFragment;
import com.mcxiaoke.appmanager.fragment.BaseFragment;
import com.mcxiaoke.appmanager.receiver.PackageCallback;
import com.mcxiaoke.appmanager.receiver.PackageMonitor;
import com.mcxiaoke.appmanager.task.RootAccessAsyncTask;
import com.mcxiaoke.appmanager.task.SimpleAsyncTaskCallback;
import com.stericson.RootTools.RootTools;

/**
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager.app
 * User: com.mcxiaoke
 * Date: 13-6-10
 * Time: 下午9:18
 */
public class UIHome extends UIBaseSupport implements PackageCallback {
    private BaseFragment mFragment;
    private RootAccessAsyncTask mRootAccessAsyncTask;
    private UIHome mContext;
    private PackageMonitor mPackageMonitor;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RootTools.debugMode = true;
        mContext = this;
        setContentView(R.layout.main);
        mPackageMonitor = new PackageMonitor();
        mPackageMonitor.register(this, this, false);
        addAppListFragment();
//        startAsyncTask();
        if (RootTools.isAccessGiven()) {
            AppContext.v("Root Access Granted");
        } else {
            AppContext.v("Root Access Not Granted");
            AppContext.showToast(mContext, R.string.root_access_failed);
//            finish();
        }
    }

    private void stopAsyncTask() {
        if (mRootAccessAsyncTask != null) {
            mRootAccessAsyncTask.stop();
            mRootAccessAsyncTask = null;
        }
    }

    private void startAsyncTask() {
        stopAsyncTask();
        mRootAccessAsyncTask = new RootAccessAsyncTask(new SimpleAsyncTaskCallback<Boolean>() {
            @Override
            public void onTaskFailure(int code, Throwable e) {
                AppContext.v("Root Access Granted");
            }

            @Override
            public void onTaskSuccess(int code, Boolean aBoolean) {
                AppContext.v("Root Access Not Granted");
                AppContext.showToast(mContext, R.string.root_access_failed);
                finish();
            }
        });
        mRootAccessAsyncTask.start();
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
        stopAsyncTask();
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
