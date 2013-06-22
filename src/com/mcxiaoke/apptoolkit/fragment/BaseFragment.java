package com.mcxiaoke.apptoolkit.fragment;

import android.app.Activity;
import com.actionbarsherlock.app.SherlockFragment;
import com.mcxiaoke.apptoolkit.app.UIBaseSupport;

/**
 * Project: filemanager
 * Package: com.com.mcxiaoke.appmanager.fragment
 * User: com.mcxiaoke
 * Date: 13-6-11
 * Time: 上午10:51
 */
public abstract class BaseFragment extends SherlockFragment implements Refreshable {
    private UIBaseSupport mBaseSupport;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mBaseSupport = (UIBaseSupport) activity;
    }

    protected UIBaseSupport getBaseSupport() {
        return mBaseSupport;
    }

    @Override
    public void hideProgress() {
        mBaseSupport.hideProgress();
    }

    @Override
    public void showProgress() {
        mBaseSupport.showProgress();
    }

}
