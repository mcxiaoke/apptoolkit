package com.mcxiaoke.appmanager.app;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.mcxiaoke.appmanager.AppContext;
import com.mcxiaoke.appmanager.R;
import com.mcxiaoke.appmanager.adapter.AppInfosAdapter;
import com.mcxiaoke.appmanager.cache.AppIconCache;
import com.mcxiaoke.appmanager.model.AppInfo;
import com.mcxiaoke.appmanager.util.Utils;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Project: filemanager
 * Package: com.mcxiaoke.appmanager.app
 * User: mcxiaoke
 * Date: 13-6-10
 * Time: 下午9:18
 */
public class UIHome extends UIBaseSupport {


    private ListView mListView;
    private ArrayAdapter<AppInfo> mArrayAdapter;
    private GetInstalledAppsAsync mAppsAsync;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mListView = (ListView) findViewById(android.R.id.list);
        mArrayAdapter = new AppInfosAdapter(this, new ArrayList<AppInfo>());
        mListView.setAdapter(mArrayAdapter);
        if (RootTools.isAccessGiven()) {
            AppContext.v("Root Access Granted");
            startRefresh();
        } else {
            AppContext.v("Root Access Not Granted");
            AppContext.showToast(this, R.string.root_access_failed);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                startRefresh();
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

    private void startRefresh() {
        startAsyncTask();
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
        stopAsyncTask();
    }

    private void stopAsyncTask() {
        if (mAppsAsync != null) {
            mAppsAsync.cancel(false);
        }
    }

    private void startAsyncTask() {
        stopAsyncTask();
        mArrayAdapter.clear();
        mAppsAsync = new GetInstalledAppsAsync(this, mArrayAdapter);
        mAppsAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class GetInstalledAppsAsync extends AsyncTask<Void, Void, List<AppInfo>> {
        private UIBaseSupport context;
        private ArrayAdapter<AppInfo> adapter;

        public GetInstalledAppsAsync(UIBaseSupport context, ArrayAdapter<AppInfo> adapter) {
            this.context = context;
            this.adapter = adapter;
        }

        @Override
        protected List<AppInfo> doInBackground(Void... params) {
            executeSuCommand();
            PackageManager pm = context.getPackageManager();
            int flags = PackageManager.GET_META_DATA;
            flags |= PackageManager.GET_SIGNATURES;
//            flags |= PackageManager.GET_CONFIGURATIONS;
//            flags |= PackageManager.GET_DISABLED_COMPONENTS;
//            flags |= PackageManager.GET_GIDS;
//            flags |= PackageManager.GET_INSTRUMENTATION;
//            flags |= PackageManager.GET_INTENT_FILTERS;
//            flags |= PackageManager.GET_PERMISSIONS;
//            flags |= PackageManager.GET_ACTIVITIES;
//            flags |= PackageManager.GET_SERVICES;
//            flags |= PackageManager.GET_RECEIVERS;
//            flags |= PackageManager.GET_PROVIDERS;
            List<AppInfo> apps = null;
            List<PackageInfo> installedPackages = pm.getInstalledPackages(flags);
            if (installedPackages != null && installedPackages.size() > 0) {
                apps = new ArrayList<AppInfo>(installedPackages.size());
                for (PackageInfo info : installedPackages) {
                    AppInfo app = Utils.convert(pm, info);
                    if (info != null) {
                        apps.add(app);
                    }
                    ApplicationInfo ainfo = info.applicationInfo;
                    Drawable icon = pm.getApplicationIcon(ainfo);
                    AppIconCache.getInstance().put(info.packageName, icon);
                }
            }
            return apps;
        }

        @Override
        protected void onPostExecute(List<AppInfo> packageInfos) {
            super.onPostExecute(packageInfos);
            context.hideActionBarRefresh();
            adapter.addAll(packageInfos);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            context.showActionBarRefresh();

        }
    }

    private static void executeSuCommand() {
        CommandCapture chmodAppDir = new CommandCapture(0, "chmod 777 /data/app");
//        CommandCapture chmodDataDir = new CommandCapture(0, "chmod 777 /data/data");
        try {
            Shell shell = RootTools.getShell(true);
            shell.add(chmodAppDir).waitForFinish();
//            shell.add(chmodDataDir);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (RootDeniedException e) {
            e.printStackTrace();
        }
    }
}
