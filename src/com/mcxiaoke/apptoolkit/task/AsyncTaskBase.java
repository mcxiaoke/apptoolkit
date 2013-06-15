/*
 * Copyright (c) 2005 - 2013, all rights reserved
 */

package com.mcxiaoke.apptoolkit.task;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Pair;

public abstract class AsyncTaskBase<Params, Progress, Result>
        extends AsyncTask<Params, Progress, Pair<Result, Throwable>> {

    private AsyncTaskCallback<Progress, Result> mCallback;
    protected volatile boolean mCancelled;

    public AsyncTaskBase(AsyncTaskCallback<Progress, Result> callback) {
        this.mCallback = callback;
        this.mCancelled = false;
    }

    /**
     * 设置结果回调监听器
     *
     * @param callback
     */
    public void setCallback(AsyncTaskCallback<Progress, Result> callback) {
        this.mCallback = callback;
    }

    @Override
    protected void onProgressUpdate(Progress... values) {
        super.onProgressUpdate(values);
        if (mCallback != null) {
            mCallback.onTaskProgress(0, values[0]);
        }
    }

    @Override
    protected final Pair<Result, Throwable> doInBackground(Params... params) {
        Result res = null;
        Throwable ex = null;
        try {
            res = onExecute(params);
        } catch (Throwable e) {
            ex = e;
        }
        return new Pair<Result, Throwable>(res, ex);
    }

    @Override
    protected final void onPostExecute(Pair<Result, Throwable> result) {
        if (mCancelled) {
            return;
        }
        try {
            if (result.first != null) {
                onPostExecuteSuccess(result.first);
            } else {
                onPostExecuteFailure(result.second);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * 继承这个方法来实现线程调用
     *
     * @param params
     * @return
     * @throws Exception
     */
    protected abstract Result onExecute(Params... params) throws Exception;

    /**
     * 调用成功
     *
     * @param result
     */
    protected void onPostExecuteSuccess(Result result) {
        if (mCallback != null) {
            mCallback.onTaskSuccess(0, result);
        }
    }

    /**
     * 失败 并返回异常
     *
     * @param exception
     */
    protected void onPostExecuteFailure(Throwable exception) {
        if (mCallback != null) {
            mCallback.onTaskFailure(0, exception);
        }
    }

    /**
     * 取消任务
     */
    public void stop() {
        mCancelled = true;
        super.cancel(true);
    }

    protected boolean isUserCancelled() {
        return mCancelled;
    }


    /**
     * @param params
     */
    public void start(Params... params) {
        if (Build.VERSION.SDK_INT > 10) {
            super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            super.execute(params);
        }
    }

}
