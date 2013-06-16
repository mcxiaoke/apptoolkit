package com.mcxiaoke.apptoolkit.cache;

import android.graphics.drawable.Drawable;

import java.util.HashMap;

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
    private HashMap<Long, Object> mObjectCache;

    public static CacheManager getInstance() {
        return sInstance;
    }

    private CacheManager() {
        mIconCache = new HashMap<String, Drawable>();
        mObjectCache = new HashMap<Long, Object>();
    }

    public boolean put(long key, Object object) {
        if (object == null) {
            return false;
        }
        synchronized (mLock) {
            mObjectCache.put(key, object);
        }
        return true;
    }

    public Object get(long key) {
        Object object = mObjectCache.get(key);
        if (object == null) {
            synchronized (mLock) {
                mObjectCache.remove(key);
            }
        }
        return object;
    }

    public Object remove(long key) {
        synchronized (mLock) {
            return mObjectCache.remove(key);
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
            mObjectCache.clear();
        }
    }

}
