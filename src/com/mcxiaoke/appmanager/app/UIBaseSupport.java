package com.mcxiaoke.appmanager.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import com.mcxiaoke.appmanager.AppContext;

/**
 * Project: DoubanShuo
 * User: mcxiaoke
 * Date: 13-5-30
 * Time: 上午9:58
 */
public class UIBaseSupport extends Activity {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
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

    public void showActionBarRefresh() {
        ensureMainThread();
        mRefreshing = true;
        invalidateOptionsMenu();
    }

    public void hideActionBarRefresh() {
        ensureMainThread();
        mRefreshing = false;
        invalidateOptionsMenu();
    }

    protected void ensureMainThread() {
        Looper looper = Looper.myLooper();
        if (looper != null && looper != getMainLooper()) {
            throw new IllegalStateException("Only call this from your main thread.");
        }
    }
}
