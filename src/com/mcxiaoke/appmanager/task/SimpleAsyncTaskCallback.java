package com.mcxiaoke.appmanager.task;

/**
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager.task
 * User: com.mcxiaoke
 * Date: 13-6-11
 * Time: 下午1:51
 */
public class SimpleAsyncTaskCallback<Result> implements AsyncTaskCallback<Void, Result> {

    @Override
    public void onTaskFailure(int code, Throwable e) {
    }

    @Override
    public void onTaskProgress(int code, Void aVoid) {
    }

    @Override
    public void onTaskSuccess(int code, Result result) {
    }

}
