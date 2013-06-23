package com.mcxiaoke.apptoolkit.task;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import com.mcxiaoke.apptoolkit.AppContext;

import java.util.List;

/**
 * Project: apptoolkit
 * Package: com.mcxiaoke.apptoolkit.task
 * User: mcxiaoke
 * Date: 13-6-22
 * Time: 下午10:17
 */
public class LoadRunningProcessTask extends AsyncTaskBase<TaskMessage, Integer, List<RunningAppProcessInfo>> {
    private static final String TAG = LoadRunningProcessTask.class.getSimpleName();
    private Context mContext;

    public LoadRunningProcessTask(Context context, AsyncTaskCallback<Integer, List<RunningAppProcessInfo>> callback) {
        super(callback);
        mContext = context;
    }

    @Override
    protected List<RunningAppProcessInfo> onExecute(TaskMessage... params) throws Exception {
        TaskMessage tm = params[0];
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        for (int i = 0; i < processes.size(); i++) {
            RunningAppProcessInfo info = processes.get(i);
            AppContext.v(TAG, "Process: " + info.processName + " pkg: " + info.pkgList[0] + " pid: " + info.pid);
        }
        return null;
    }

    @Override
    protected void onPostExecuteSuccess(List<RunningAppProcessInfo> runningAppProcessInfos) {
        super.onPostExecuteSuccess(runningAppProcessInfos);
    }

    @Override
    protected void onPostExecuteFailure(Throwable exception) {
        super.onPostExecuteFailure(exception);
    }
}
