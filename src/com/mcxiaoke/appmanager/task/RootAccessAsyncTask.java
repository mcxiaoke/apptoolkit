package com.mcxiaoke.appmanager.task;

import com.stericson.RootTools.RootTools;

/**
 * Project: filemanager
 * Package: com.mcxiaoke.appmanager.task
 * User: mcxiaoke
 * Date: 13-6-11
 * Time: 上午11:39
 */
public class RootAccessAsyncTask extends AsyncTaskBase<Void, Void, Boolean> {

    public RootAccessAsyncTask(SimpleAsyncTaskCallback<Boolean> callback) {
        super(callback);

    }

    @Override
    protected Boolean onExecute(Void... params) throws Exception {
        return RootTools.isAccessGiven();
    }
}
