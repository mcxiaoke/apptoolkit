package com.mcxiaoke.apptoolkit.task;

/**
 * Project: apptoolkit
 * Package: com.mcxiaoke.apptoolkit.task
 * User: mcxiaoke
 * Date: 13-6-16
 * Time: 下午6:23
 */
public abstract class SimpleAsyncTask extends AsyncTaskBase<TaskMessage, Void, Boolean> {

    public SimpleAsyncTask() {
        super(null);
    }
}
