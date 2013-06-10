package com.mcxiaoke.appmanager.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.mcxiaoke.appmanager.AppContext;
import com.mcxiaoke.appmanager.R;
import com.mcxiaoke.appmanager.adapter.AppInfosAdapter;
import com.mcxiaoke.appmanager.model.AppInfo;
import com.mcxiaoke.appmanager.util.Utils;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;

import java.io.File;
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
            GetInstalledAppsAsync task = new GetInstalledAppsAsync(this, mArrayAdapter);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            AppContext.v("Root Access Not Granted");
            AppContext.showToast(this, R.string.root_access_failed);
            finish();
        }
    }

    private static class GetInstalledAppsAsync extends AsyncTask<Void, Void, List<AppInfo>> {
        private Context context;
        private ArrayAdapter<AppInfo> adapter;

        public GetInstalledAppsAsync(Context context, ArrayAdapter<AppInfo> adapter) {
            this.context = context;
            this.adapter = adapter;
        }

        @Override
        protected List<AppInfo> doInBackground(Void... params) {
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
                }
            }
            return apps;
        }

        @Override
        protected void onPostExecute(List<AppInfo> packageInfos) {
            super.onPostExecute(packageInfos);
            adapter.addAll(packageInfos);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    private void listDataFiles() {
        CommandCapture cmd = new CommandCapture(0, "chmod 777 /data/app");
        try {
            RootTools.getShell(true).add(cmd).waitForFinish();
            File appDir = new File("/data/app");
            File[] apps = appDir.listFiles();
            if (apps != null) {
                for (File file : apps) {
                    AppContext.v("App: " + file.getAbsolutePath());
                }
            }
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
