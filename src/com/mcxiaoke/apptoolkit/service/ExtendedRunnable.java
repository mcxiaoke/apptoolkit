package com.mcxiaoke.apptoolkit.service;

/**
 * Project: apptoolkit
 * Package: com.mcxiaoke.apptoolkit.task
 * User: mcxiaoke
 * Date: 13-6-15
 * Time: 下午6:59
 */
public abstract class ExtendedRunnable implements Runnable {
    private String mName;
    private boolean mCancelled;

    public ExtendedRunnable(String name) {
        mName = name;
        mCancelled = false;
    }

    public void cancel() {
        mCancelled = true;
    }

    public boolean isCancelled() {
        return mCancelled;
    }

    public String getName() {
        return mName;
    }

}
