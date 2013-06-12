package com.mcxiaoke.appmanager.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.mcxiaoke.appmanager.AppContext;
import com.mcxiaoke.appmanager.R;
import com.mcxiaoke.appmanager.adapter.AppListAdapter;
import com.mcxiaoke.appmanager.model.AppInfo;
import com.mcxiaoke.appmanager.task.AppListAsyncTask;
import com.mcxiaoke.appmanager.task.AsyncTaskCallback;
import com.mcxiaoke.appmanager.task.BackupAsyncTask;
import com.mcxiaoke.appmanager.task.SimpleAsyncTaskCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager.fragment
 * User: com.mcxiaoke
 * Date: 13-6-11
 * Time: 上午10:55
 */
public class AppListFragment extends BaseFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private ListView mListView;
    private List<AppInfo> mAppInfos;
    private ArrayAdapter<AppInfo> mArrayAdapter;
    private AppListAsyncTask mAsyncTask;
    private BackupAsyncTask mBackupTask;

    private ProgressDialog mProgressDialog;

    private boolean isBackuping;

    private Handler mUiHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.v("AppListFragment onCreate()");
        mAppInfos = new ArrayList<AppInfo>();
        mUiHandler = new Handler(Looper.getMainLooper());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fm_applist, null);
        mListView = (ListView) root.findViewById(android.R.id.list);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(this);
        mArrayAdapter = new AppListAdapter(getActivity(), mAppInfos);
        mListView.setAdapter(mArrayAdapter);
        refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
        stopAsyncTask();
        stopBackup();
    }

    @Override
    public void refresh() {
        startAsyncTask();
    }

    private void stopAsyncTask() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(false);
        }
    }

    private void startAsyncTask() {
        stopAsyncTask();
        mArrayAdapter.clear();
        mAsyncTask = new AppListAsyncTask(getActivity(), new SimpleAsyncTaskCallback<List<AppInfo>>() {
            @Override
            public void onTaskSuccess(int code, List<AppInfo> appInfos) {
                hideProgressIndicator();
                mArrayAdapter.addAll(appInfos);
            }

            @Override
            public void onTaskFailure(int code, Throwable e) {
                hideProgressIndicator();
            }
        });
        mAsyncTask.start();
        showProgressIndicator();
    }

    private void showBackupConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_backup_all_title);
        builder.setMessage(R.string.dialog_backup_all_message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startBackup();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showBackupCompleteDialog(int count) {
        String message = String.format(getString(R.string.dialog_backup_complete_message), mAppInfos.size(), count);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_backup_complete_title);
        builder.setMessage(message);
        builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void startBackup() {
        if (isBackuping) {
            return;
        }
        AppContext.v("startBackup");
        stopBackup();
        isBackuping = true;
        mBackupTask = new BackupAsyncTask(getActivity(), new AsyncTaskCallback<String, Integer>() {

            @Override
            public void onTaskProgress(int code, String text) {
                AppContext.v("BackupAsyncTask.onTaskProgress " + text);
                updateProgressDialog(text);
            }

            @Override
            public void onTaskSuccess(int code, Integer integer) {
                AppContext.v("BackupAsyncTask.onTaskSuccess backup count is " + integer);
                int added = integer == null ? 0 : integer;
                dismissProgressDialog();
                showBackupCompleteDialog(added);
                isBackuping = false;
            }

            @Override
            public void onTaskFailure(int code, Throwable e) {
                AppContext.v("BackupAsyncTask.onTaskFailure ex is " + e);
                isBackuping = false;
                dismissProgressDialog();
            }
        });
        mBackupTask.start(mAppInfos);
        showProgressDialog();

    }

    private void stopBackup() {
        if (mBackupTask != null) {
            AppContext.v("stopBackup");
            mBackupTask.stop();
            mBackupTask = null;
            isBackuping = false;
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setTitle(R.string.dialog_backup_title);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    AppContext.v("OnCancelListener");
                    stopBackup();
                }
            });
        }
        mProgressDialog.show();
    }

    private void updateProgressDialog(final String text) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.setMessage(text);
                }
            }
        });
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_backup, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.menu_backup == item.getItemId()) {
            showBackupConfirmDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final AppInfo app = mArrayAdapter.getItem(position);
        showDialog(app);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    private static final String DIALOG_TAG = "DIALOG_TAG";

    private void showDialog(AppInfo app) {
        if (app != null) {
            //        mStackLevel++;
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag(DIALOG_TAG);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            AppActionDialogFragment newFragment = AppActionDialogFragment.newInstance(app);
            newFragment.show(ft, DIALOG_TAG);
        }
    }
}
