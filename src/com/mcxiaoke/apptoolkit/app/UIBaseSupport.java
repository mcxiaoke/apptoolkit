package com.mcxiaoke.apptoolkit.app;

import android.os.Bundle;
import android.os.Looper;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.mcxiaoke.apptoolkit.AppContext;

/**
 * Project: DoubanShuo
 * User: com.mcxiaoke
 * Date: 13-5-30
 * Time: 上午9:58
 */
public class UIBaseSupport extends SherlockFragmentActivity {

    protected static final boolean DEBUG = AppContext.isDebug();

    protected void debug(String message) {
        AppContext.v(message);
    }

    protected void error(String message) {
        AppContext.e(message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
    }


    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }


    private boolean mRefreshing;

    public boolean isRefreshing() {
        return mRefreshing;
    }

    private void showActionBarProgress() {
        ensureMainThread();
        mRefreshing = true;
        invalidateOptionsMenu();
    }

    private void hideActionBarProgress() {
        ensureMainThread();
        mRefreshing = false;
        invalidateOptionsMenu();
    }

    protected void showProgressIndicator() {
        setSupportProgressBarIndeterminateVisibility(true);
    }

    protected void hideProgressIndicator() {
        setSupportProgressBarIndeterminateVisibility(false);
    }


    public void showProgress() {
        if (hasRefreshAction()) {
            showActionBarProgress();
        } else {
            showProgressIndicator();
        }
    }

    public void hideProgress() {
        if (hasRefreshAction()) {
            hideActionBarProgress();
        } else {
            hideProgressIndicator();
        }
    }

    protected void ensureMainThread() {
        Looper looper = Looper.myLooper();
        if (looper != null && looper != getMainLooper()) {
            throw new IllegalStateException("Only call this from your main thread.");
        }
    }

    protected boolean hasRefreshAction() {
        return false;
    }
}
