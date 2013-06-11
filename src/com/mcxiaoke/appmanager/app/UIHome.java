package com.mcxiaoke.appmanager.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.mcxiaoke.appmanager.AppContext;
import com.mcxiaoke.appmanager.R;
import com.mcxiaoke.appmanager.cache.AppIconCache;
import com.mcxiaoke.appmanager.fragment.AppListFragment;
import com.mcxiaoke.appmanager.fragment.BaseFragment;
import com.mcxiaoke.appmanager.task.RootAccessAsyncTask;
import com.mcxiaoke.appmanager.task.SimpleAsyncTaskCallback;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager.app
 * User: com.mcxiaoke
 * Date: 13-6-10
 * Time: 下午9:18
 */
public class UIHome extends UIBaseSupport {
    private BaseFragment mFragment;
    private RootAccessAsyncTask mRootAccessAsyncTask;
    private UIHome mContext;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RootTools.debugMode = true;
        mContext = this;
        setContentView(R.layout.main);
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
        stopAsyncTask();
        AppIconCache.getInstance().clear();
    }

    private static void executeSuCommand() {
        File file = new File("/data/data/com.baidu.input");
        file.setExecutable(true);
        file.setWritable(true);
        file.setReadable(true);
        AppContext.v("file r=" + file.canRead() + " e=" + file.canExecute() + " w=" + file.canWrite());
        if (file.canRead()) {
            File[] list = file.listFiles();
            if (list != null) {
                for (File f : list) {
                    AppContext.v("file: " + f.getAbsolutePath());
                }
            }
        }
        CommandCapture chmodAppDir = new CommandCapture(0, "chmod 777 /data/app");
        try {
            Shell shell = RootTools.getShell(true);
            shell.add(chmodAppDir).waitForFinish();
//            shell.add(chmodDataDir).waitForFinish();
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
