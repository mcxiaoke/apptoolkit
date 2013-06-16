package com.mcxiaoke.apptoolkit.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mcxiaoke.apptoolkit.AppConfig;
import com.mcxiaoke.apptoolkit.AppContext;
import com.mcxiaoke.apptoolkit.R;
import com.mcxiaoke.apptoolkit.adapter.AppListAdapter;
import com.mcxiaoke.apptoolkit.adapter.MultiChoiceArrayAdapter;
import com.mcxiaoke.apptoolkit.callback.IPackageMonitor;
import com.mcxiaoke.apptoolkit.model.AppInfo;
import com.mcxiaoke.apptoolkit.task.AsyncTaskCallback;
import com.mcxiaoke.apptoolkit.task.BackupAppsApkTask;
import com.mcxiaoke.apptoolkit.task.LoadAppsTask;
import com.mcxiaoke.apptoolkit.task.TaskMessage;
import com.mcxiaoke.apptoolkit.util.Utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager.fragment
 * User: com.mcxiaoke
 * Date: 13-6-11
 * Time: 上午10:55
 */
public class PackageListFragment extends BaseFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, MultiChoiceArrayAdapter.OnCheckedListener, IPackageMonitor {
    private static final String TAG = PackageListFragment.class.getSimpleName();

    private static void debug(String message) {
        AppContext.v(message);
    }

    private static final int MSG_PACKAGE_ADDED = 1001;
    private static final int MSG_PACKAGE_REMOVED = 1002;

    private ListView mListView;
    private CopyOnWriteArrayList<AppInfo> mAppData;
    private MultiChoiceArrayAdapter<AppInfo> mArrayAdapter;
    private ActionModeCallback mActionModeCallback;
    private LoadAppsTask mLoadAppsTask;
    private TaskMessage mLoadAppsTaskParam;
    private BackupAppsApkTask mBackupTask;

    private ProgressDialog mProgressDialog;
    private ActionMode mActionMode;

    private boolean isBackuping;
    private boolean isAppLoading;
    private Handler mUiHandler;

    private int mType;
    private boolean mSystem;

    public static PackageListFragment newInstance(int type, boolean system) {
        PackageListFragment fragment = new PackageListFragment();
        Bundle args = new Bundle();
        args.putInt(AppConfig.EXTRA_TYPE, type);
        args.putBoolean(AppConfig.EXTRA_SYSTEM, system);
        fragment.setArguments(args);
        return fragment;
    }

    public PackageListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.v("AppListFragment onCreate()");
        Bundle args = getArguments();
        if (args != null) {
            mType = args.getInt(AppConfig.EXTRA_TYPE, AppConfig.TYPE_USER_APP_MANAGER);
            mSystem = args.getBoolean(AppConfig.EXTRA_SYSTEM, false);
        }
        mLoadAppsTaskParam = new TaskMessage(mType, mSystem);
        mAppData = new CopyOnWriteArrayList<AppInfo>();
        setHasOptionsMenu(true);

        mUiHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppContext.v("AppListFragment onCreateView()");
        View root = inflater.inflate(R.layout.fm_applist, null);
        mListView = (ListView) root.findViewById(android.R.id.list);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppContext.v("AppListFragment onActivityCreated()");
        mActionModeCallback = new ActionModeCallback(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mArrayAdapter = new AppListAdapter(getActivity(), mAppData);
        mArrayAdapter.setOnCheckedListener(this);
        mListView.setAdapter(mArrayAdapter);
        refresh();
    }

    @Override
    public void refresh() {
        AppContext.v("AppListFragment refresh()");
        startLoadAppsTask();
    }

    private void stopLoadAppsTask() {
        if (mLoadAppsTask != null) {
            mLoadAppsTask.cancel(false);
        }
    }

    private void startLoadAppsTask() {
        stopLoadAppsTask();
        isAppLoading = true;
        mArrayAdapter.clear();
        AppContext.v("AppListFragment startLoadAppsTask()");
        mLoadAppsTask = new LoadAppsTask(getActivity(), new AsyncTaskCallback<Pair<Integer, Integer>, List<AppInfo>>() {

            @Override
            public void onTaskProgress(int code, Pair<Integer, Integer> integerIntegerPair) {
            }

            @Override
            public void onTaskSuccess(int code, List<AppInfo> appInfos) {
                AppContext.v("AppListFragment onTaskSuccess() size is " + (appInfos == null ? "null" : appInfos.size()));
                hideProgressIndicator();
                isAppLoading = false;
                mArrayAdapter.addAll(appInfos);
            }

            @Override
            public void onTaskFailure(int code, Throwable e) {
                AppContext.v("AppListFragment onTaskFailure()");
                hideProgressIndicator();
                isAppLoading = false;
            }
        });

        mLoadAppsTask.start(mLoadAppsTaskParam);
        showProgressIndicator();
    }

    private void showDialog(AppInfo app) {
        if (app != null) {

            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag(DIALOG_TAG);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            ActionsDialogFragment newFragment = ActionsDialogFragment.newInstance(app, mType);
            newFragment.show(ft, DIALOG_TAG);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppContext.v("AppListFragment onItemClick() position=" + position + " mActionMode=" + mActionMode);
        if (mActionMode != null) {
            mArrayAdapter.toggleChecked(position);
            checkActionMode();
        } else {
            final AppInfo app = mArrayAdapter.getItem(position);
            showDialog(app);
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        AppContext.v("AppListFragment onItemLongClick() position=" + position + " mActionMode=" + mActionMode);
        if (mActionMode == null) {
            mArrayAdapter.toggleChecked(position);
            checkActionMode();
        }
        return true;
    }

    @Override
    public void onCheckedChanged(int position, boolean isChecked) {
        checkActionMode();
    }

    private void checkActionMode() {
        if (mActionMode == null) {
            getSherlockActivity().startActionMode(mActionModeCallback);
        }
        setActionModeTitle();
    }

    private void setActionModeTitle() {
        if (mActionMode != null) {
            int checkedCount = mArrayAdapter.getCheckedItemCount();
            if (checkedCount == 0) {
                mActionMode.finish();
            } else {
                mActionMode.setTitle("选择应用");
                mActionMode.setSubtitle("已选择" + checkedCount + "项");
            }
        }
    }

    private void onSelectAll() {
        if (mActionMode != null) {
            mArrayAdapter.checkAll();
            setActionModeTitle();
        }
    }

    private void onUnselectAll() {

    }

    private void onBackupApps() {
        List<AppInfo> checkedApps = mArrayAdapter.getCheckedItems();
        if (checkedApps != null && checkedApps.size() > 0) {
            showBackupConfirmDialog(checkedApps);
        }
    }

    private void onBackupData() {

    }

    private boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_mode_select_all:
                onSelectAll();
                break;
            case R.id.menu_mode_backup_app:
                onBackupApps();
                break;
            case R.id.menu_mode_backup_data:
                onBackupData();
                break;
        }
        return false;
    }

    private void onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_mode_applist, menu);
        mActionMode = mode;
        mArrayAdapter.setActionModeState(true);
        setActionModeTitle();
    }

    private boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    private void onDestroyActionMode(ActionMode mode) {
        mArrayAdapter.setActionModeState(false);
        mActionMode = null;
    }

    private static final String DIALOG_TAG = "DIALOG_TAG";

    static class ActionModeCallback implements ActionMode.Callback {
        private PackageListFragment mFragment;

        public ActionModeCallback(PackageListFragment fragment) {
            this.mFragment = fragment;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return mFragment.onActionItemClicked(mode, item);
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mFragment.onCreateActionMode(mode, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mFragment.onDestroyActionMode(mode);
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return mFragment.onPrepareActionMode(mode, menu);
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


    private void showBackupConfirmDialog(final List<AppInfo> apps) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_backup_all_title);
        builder.setMessage(R.string.dialog_backup_all_message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startBackup(apps);
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

    private void showBackupCompleteDialog(int count, int totalCount) {
        String backupDir = Utils.getBackupAppsDir().getPath();
        String message = String.format(getString(R.string.dialog_backup_complete_message), backupDir, totalCount, count);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_backup_complete_title);
        builder.setMessage(message);
        builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mArrayAdapter.notifyDataSetChanged();
            }
        });
        builder.create().show();
    }

    private void startBackup(List<AppInfo> apps) {
        if (isBackuping) {
            return;
        }
        if (apps == null || apps.isEmpty()) {
            return;
        }

        final int totalCount = apps.size();
        AppContext.v("startBackup");
        stopBackup();
        isBackuping = true;
        mBackupTask = new BackupAppsApkTask(getActivity(), new AsyncTaskCallback<AppInfo, Integer>() {

            @Override
            public void onTaskProgress(int code, AppInfo app) {
                AppContext.v("BackupAsyncTask.onTaskProgress " + app.appName);
                updateProgressDialog(Utils.buildProgressText(app));
            }

            @Override
            public void onTaskSuccess(int code, Integer integer) {
                AppContext.v("BackupAsyncTask.onTaskSuccess backup count is " + integer);
                int backuped = integer == null ? 0 : integer;
                dismissProgressDialog();
                showBackupCompleteDialog(backuped, totalCount);
                isBackuping = false;
            }

            @Override
            public void onTaskFailure(int code, Throwable e) {
                AppContext.v("BackupAsyncTask.onTaskFailure ex is " + e);
                isBackuping = false;
                dismissProgressDialog();
            }
        });

        mBackupTask.start(apps);
        if (mActionMode != null) {
            mActionMode.finish();
        }
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

    @Override
    public void onPackageAdded(String packageName, int uid) {
    }

    @Override
    public void onPackageChanged(String packageName, int uid, String[] components) {
    }

    @Override
    public void onPackageModified(String packageName) {
    }

    @Override
    public void onPackageRemoved(String packageName, int uid) {
        if (packageName != null) {
            synchronized (this) {
                for (AppInfo app : mAppData) {
                    if (app.packageName.equalsIgnoreCase(packageName)) {
                        mAppData.remove(app);
                        mArrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    @Override
    public void onUidRemoved(int uid) {
        if (uid > AppConfig.UID_SYSTEM) {
            synchronized (this) {
                for (AppInfo app : mAppData) {
                    if (app.uid == uid) {
                        mAppData.remove(app);
                        mArrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
        stopLoadAppsTask();
        stopBackup();
    }

}
