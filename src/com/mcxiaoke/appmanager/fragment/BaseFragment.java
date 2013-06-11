package com.mcxiaoke.appmanager.fragment;

import android.app.Activity;
import android.app.Fragment;
import com.mcxiaoke.appmanager.app.UIBaseSupport;

/**
 * Project: filemanager
 * Package: com.mcxiaoke.appmanager.fragment
 * User: mcxiaoke
 * Date: 13-6-11
 * Time: 上午10:51
 */
public abstract class BaseFragment extends Fragment implements Refreshable {
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
    public void hideProgressIndicator() {
        mBaseSupport.hideActionBarProgress();
    }

    @Override
    public void showProgressIndicator() {
        mBaseSupport.showActionBarProgress();
    }

}
