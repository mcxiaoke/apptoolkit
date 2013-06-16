/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mcxiaoke.apptoolkit.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import com.mcxiaoke.apptoolkit.callback.IPackageMonitor;

import java.lang.ref.WeakReference;
import java.util.HashSet;

/**
 * Helper class for monitoring the state of packages: adding, removing,
 * updating, and disappearing and reappearing on the SD card.
 */
public class PackageMonitor extends android.content.BroadcastReceiver {
    static final IntentFilter sPackageFilter = new IntentFilter();
    static final IntentFilter sNonDataFilter = new IntentFilter();
    static final IntentFilter sExternalFilter = new IntentFilter();

    static {
        sPackageFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        sPackageFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        sPackageFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        sPackageFilter.addAction(Intent.ACTION_PACKAGE_RESTARTED);
        sPackageFilter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
        sPackageFilter.addAction(Intent.ACTION_UID_REMOVED);
        sPackageFilter.addDataScheme("package");
        sNonDataFilter.addAction(Intent.ACTION_UID_REMOVED);
        sExternalFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        sExternalFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
    }

    final HashSet<String> mUpdatingPackages = new HashSet<String>();

    private Context mRegisteredContext;
    private String[] mDisappearingPackages;
    private String[] mAppearingPackages;
    private String[] mModifiedPackages;
    private int mChangeType;
    private boolean mSomePackagesChanged;

    private String[] mTempArray = new String[1];

    private WeakReference<IPackageMonitor> mPackageCallbackRef;

    public void register(Context context, IPackageMonitor callback, boolean externalStorage) {
        if (mRegisteredContext != null) {
            throw new IllegalStateException("Already registered");
        }
        mRegisteredContext = context;
        mPackageCallbackRef = new WeakReference<IPackageMonitor>(callback);
        context.registerReceiver(this, sPackageFilter);
        context.registerReceiver(this, sNonDataFilter);
        if (externalStorage) {
            context.registerReceiver(this, sExternalFilter);
        }
    }

    public void unregister() {
        if (mRegisteredContext == null) {
            throw new IllegalStateException("Not registered");
        }
        mRegisteredContext.unregisterReceiver(this);
        mRegisteredContext = null;
        mPackageCallbackRef.clear();
        mPackageCallbackRef = null;
    }

    //not yet implemented
    private boolean isPackageUpdating(String packageName) {
        synchronized (mUpdatingPackages) {
            return mUpdatingPackages.contains(packageName);
        }
    }

    protected void onBeginPackageChanges() {
    }

    protected void onPackageAdded(String packageName, int uid) {
        if (mPackageCallbackRef != null) {
            IPackageMonitor callback = mPackageCallbackRef.get();
            if (callback != null) {
                callback.onPackageAdded(packageName, uid);
            }
        }
    }

    protected void onPackageRemoved(String packageName, int uid) {
        if (mPackageCallbackRef != null) {
            IPackageMonitor callback = mPackageCallbackRef.get();
            if (callback != null) {
                callback.onPackageRemoved(packageName, uid);
            }
        }
    }

    protected void onPackageUpdateStarted(String packageName, int uid) {
        if (mPackageCallbackRef != null) {
            IPackageMonitor callback = mPackageCallbackRef.get();
            if (callback != null) {
            }
        }
    }

    protected void onPackageUpdateFinished(String packageName, int uid) {
        if (mPackageCallbackRef != null) {
            IPackageMonitor callback = mPackageCallbackRef.get();
            if (callback != null) {
            }
        }
    }

    protected void onPackageChanged(String packageName, int uid, String[] components) {
        if (mPackageCallbackRef != null) {
            IPackageMonitor callback = mPackageCallbackRef.get();
            if (callback != null) {
                callback.onPackageChanged(packageName, uid, components);
            }
        }
    }

    protected boolean onHandleForceStop(Intent intent, String[] packages, int uid, boolean doit) {
        return false;
    }

    protected void onUidRemoved(int uid) {
        if (mPackageCallbackRef != null) {
            IPackageMonitor callback = mPackageCallbackRef.get();
            if (callback != null) {
                callback.onUidRemoved(uid);
            }
        }
    }

    protected void onPackagesAvailable(String[] packages) {
        if (mPackageCallbackRef != null) {
            IPackageMonitor callback = mPackageCallbackRef.get();
            if (callback != null) {
            }
        }
    }

    protected void onPackagesUnavailable(String[] packages) {
    }

    public static final int PACKAGE_UNCHANGED = 0;
    public static final int PACKAGE_UPDATING = 1;
    public static final int PACKAGE_TEMPORARY_CHANGE = 2;
    public static final int PACKAGE_PERMANENT_CHANGE = 3;

    protected void onPackageDisappeared(String packageName, int reason) {
    }

    protected void onPackageAppeared(String packageName, int reason) {
    }

    protected void onPackageModified(String packageName) {
        if (mPackageCallbackRef != null) {
            IPackageMonitor callback = mPackageCallbackRef.get();
            if (callback != null) {
                callback.onPackageModified(packageName);
            }
        }
    }

    protected boolean didSomePackagesChange() {
        return mSomePackagesChanged;
    }

    protected int isPackageAppearing(String packageName) {
        if (mAppearingPackages != null) {
            for (int i = mAppearingPackages.length - 1; i >= 0; i--) {
                if (packageName.equals(mAppearingPackages[i])) {
                    return mChangeType;
                }
            }
        }
        return PACKAGE_UNCHANGED;
    }

    protected boolean anyPackagesAppearing() {
        return mAppearingPackages != null;
    }

    protected int isPackageDisappearing(String packageName) {
        if (mDisappearingPackages != null) {
            for (int i = mDisappearingPackages.length - 1; i >= 0; i--) {
                if (packageName.equals(mDisappearingPackages[i])) {
                    return mChangeType;
                }
            }
        }
        return PACKAGE_UNCHANGED;
    }

    protected boolean anyPackagesDisappearing() {
        return mDisappearingPackages != null;
    }

    protected boolean isPackageModified(String packageName) {
        if (mModifiedPackages != null) {
            for (int i = mModifiedPackages.length - 1; i >= 0; i--) {
                if (packageName.equals(mModifiedPackages[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void onSomePackagesChanged() {
    }

    protected void onFinishPackageChanges() {
    }

    protected String getPackageName(Intent intent) {
        Uri uri = intent.getData();
        String pkg = uri != null ? uri.getSchemeSpecificPart() : null;
        return pkg;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        onBeginPackageChanges();

        mDisappearingPackages = mAppearingPackages = null;
        mSomePackagesChanged = false;

        String action = intent.getAction();
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            String pkg = getPackageName(intent);
            int uid = intent.getIntExtra(Intent.EXTRA_UID, 0);
            // We consider something to have changed regardless of whether
            // this is just an update, because the update is now finished
            // and the contents of the package may have changed.
            mSomePackagesChanged = true;
            if (pkg != null) {
                mAppearingPackages = mTempArray;
                mTempArray[0] = pkg;
                if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                    mModifiedPackages = mTempArray;
                    mChangeType = PACKAGE_UPDATING;
                    onPackageUpdateFinished(pkg, uid);
                    onPackageModified(pkg);
                } else {
                    mChangeType = PACKAGE_PERMANENT_CHANGE;
                    onPackageAdded(pkg, uid);
                }
                onPackageAppeared(pkg, mChangeType);
                if (mChangeType == PACKAGE_UPDATING) {
                    synchronized (mUpdatingPackages) {
                        mUpdatingPackages.remove(pkg);
                    }
                }
            }
        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            String pkg = getPackageName(intent);
            int uid = intent.getIntExtra(Intent.EXTRA_UID, 0);
            if (pkg != null) {
                mDisappearingPackages = mTempArray;
                mTempArray[0] = pkg;
                if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                    mChangeType = PACKAGE_UPDATING;
                    synchronized (mUpdatingPackages) {
                        //not used for now
                        //mUpdatingPackages.add(pkg);
                    }
                    onPackageUpdateStarted(pkg, uid);
                } else {
                    mChangeType = PACKAGE_PERMANENT_CHANGE;
                    // We only consider something to have changed if this is
                    // not a replace; for a replace, we just need to consider
                    // it when it is re-added.
                    mSomePackagesChanged = true;
                    onPackageRemoved(pkg, uid);
                }
                onPackageDisappeared(pkg, mChangeType);
            }
        } else if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
            String pkg = getPackageName(intent);
            int uid = intent.getIntExtra(Intent.EXTRA_UID, 0);
            String[] components = intent.getStringArrayExtra(
                    Intent.EXTRA_CHANGED_COMPONENT_NAME_LIST);
            if (pkg != null) {
                mModifiedPackages = mTempArray;
                mTempArray[0] = pkg;
                onPackageChanged(pkg, uid, components);
                // XXX Don't want this to always cause mSomePackagesChanged,
                // since it can happen a fair amount.
                onPackageModified(pkg);
            }
        } else if (Intent.ACTION_PACKAGE_RESTARTED.equals(action)) {
            mDisappearingPackages = new String[]{getPackageName(intent)};
            mChangeType = PACKAGE_TEMPORARY_CHANGE;
            onHandleForceStop(intent, mDisappearingPackages,
                    intent.getIntExtra(Intent.EXTRA_UID, 0), true);
        } else if (Intent.ACTION_UID_REMOVED.equals(action)) {
            onUidRemoved(intent.getIntExtra(Intent.EXTRA_UID, 0));
        } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action)) {
            String[] pkgList = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
            mAppearingPackages = pkgList;
            mChangeType = PACKAGE_TEMPORARY_CHANGE;
            mSomePackagesChanged = true;
            if (pkgList != null) {
                onPackagesAvailable(pkgList);
                for (int i = 0; i < pkgList.length; i++) {
                    onPackageAppeared(pkgList[i], PACKAGE_TEMPORARY_CHANGE);
                }
            }
        } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action)) {
            String[] pkgList = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
            mDisappearingPackages = pkgList;
            mChangeType = PACKAGE_TEMPORARY_CHANGE;
            mSomePackagesChanged = true;
            if (pkgList != null) {
                onPackagesUnavailable(pkgList);
                for (int i = 0; i < pkgList.length; i++) {
                    onPackageDisappeared(pkgList[i], PACKAGE_TEMPORARY_CHANGE);
                }
            }
        }

        if (mSomePackagesChanged) {
            onSomePackagesChanged();
        }

        onFinishPackageChanges();
    }
}
