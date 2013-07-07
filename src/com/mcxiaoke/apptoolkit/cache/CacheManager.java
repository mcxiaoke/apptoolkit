package com.mcxiaoke.apptoolkit.cache;

import android.graphics.drawable.Drawable;
import com.mcxiaoke.apptoolkit.model.AppInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Project: apptoolkit
 * Package: com.mcxiaoke.apptoolkit.cache
 * User: com.mcxiaoke
 * Date: 13-6-11
 * Time: 上午12:09
 */
public class CacheManager {
    private static CacheManager sInstance = new CacheManager();

    private final Object mLock = new Object();

    private HashMap<String, Drawable> mIconCache;
    private List<AppInfo> mAppInfoCache;

    public static CacheManager getInstance() {
        return sInstance;
    }

    private CacheManager() {
        mIconCache = new HashMap<String, Drawable>();
        mAppInfoCache = new CopyOnWriteArrayList<AppInfo>();
    }

    public boolean addAll(Collection<AppInfo> apps) {
        if (apps == null || apps.isEmpty()) {
            return false;
        }
        synchronized (mLock) {
            mAppInfoCache.addAll(apps);
        }
        return true;
    }

    public List<AppInfo> getAll() {
        return mAppInfoCache;
    }

    public boolean remove(AppInfo app) {
        synchronized (mLock) {
            return mAppInfoCache.remove(app);
        }
    }

    public boolean putIcon(String key, Drawable drawable) {
        if (key == null || drawable == null) {
            return false;
        }
        synchronized (mLock) {
            mIconCache.put(key, drawable);
        }
        return true;
    }

    public Drawable getIcon(String key) {
        if (key == null) {
            return null;
        }
        Drawable drawable = mIconCache.get(key);
        if (drawable == null) {
            synchronized (mLock) {
                mIconCache.remove(key);
            }
        }
        return drawable;
    }

    public synchronized void clear() {
        synchronized (mLock) {
            mIconCache.clear();
            mAppInfoCache.clear();
        }
    }

}
