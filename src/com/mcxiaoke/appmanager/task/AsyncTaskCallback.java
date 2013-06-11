package com.mcxiaoke.appmanager.task;

/**
 * Project: DoubanShuo
 * User: mcxiaoke
 * Date: 13-6-4
 * Time: 下午12:08
 */
public interface AsyncTaskCallback<Progress, Result> {
    public void onTaskProgress(int code, Progress progress);

    public void onTaskSuccess(int code, Result result);

    public void onTaskFailure(int code, Throwable e);

}
