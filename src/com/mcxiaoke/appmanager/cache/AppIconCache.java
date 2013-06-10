package com.mcxiaoke.appmanager.cache;

import android.graphics.drawable.Drawable;

import java.util.HashMap;

/**
 * Project: filemanager
 * Package: com.mcxiaoke.appmanager.cache
 * User: mcxiaoke
 * Date: 13-6-11
 * Time: 上午12:09
 */
public class AppIconCache {
    private static AppIconCache sInstance = new AppIconCache();

    private HashMap<String, Drawable> mIconCache;

    public static AppIconCache getInstance() {
        return sInstance;
    }

    private AppIconCache() {
        mIconCache = new HashMap<String, Drawable>();
    }

    public boolean put(String key, Drawable drawable) {
        if (key == null || drawable == null) {
            return false;
        }
        synchronized (this) {
            mIconCache.put(key, drawable);
        }
        return true;
    }

    public Drawable get(String key) {
        if (key == null) {
            return null;
        }
        Drawable drawable = mIconCache.get(key);
        if (drawable == null) {
            synchronized (this) {
                mIconCache.remove(key);
            }
        }
        return drawable;
    }

    public synchronized void clear() {
        mIconCache.clear();
    }

}
