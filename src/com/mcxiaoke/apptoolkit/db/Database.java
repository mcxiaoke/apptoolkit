package com.mcxiaoke.apptoolkit.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.Pair;
import com.mcxiaoke.apptoolkit.AppContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: apptoolkit
 * Package: com.mcxiaoke.apptoolkit.db
 * User: mcxiaoke
 * Date: 13-7-3
 * Time: 下午10:42
 */
public class Database {
    public static final boolean DEBUG = AppContext.isDebug();
    public static final String TAG = Database.class.getSimpleName();
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "data.db";

    static final String APP_TABLE = "app";
    static final String PROCESS_TABLE = "process";
    static final String BACKUP_TABLE = "backup";

    private Context mContext;
    private DBHelper mHelper;

    public Database(Context context) {
        mContext = context.getApplicationContext();
        mHelper = new DBHelper(mContext);
    }

    public int addBackups(List<String> list) {
        if (list == null || list.isEmpty()) {
            return -1;
        }
        int count = 0;
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        try {
            for (String packageName : list) {
                values.clear();
                values.put(Columns.PACKAGE, packageName);
                values.put(Columns.CREATED, System.currentTimeMillis());
                db.replaceOrThrow(BACKUP_TABLE, null, values);
                count++;
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        if (DEBUG) {
            Log.v(TAG, "addBackups() size is " + list.size() + " count is " + count);
        }
        return count;
    }

    public List<Pair<String, Long>> getBackupApps() {
        List<Pair<String, Long>> list = new ArrayList<Pair<String, Long>>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(BACKUP_TABLE, null, null, null, null, null, null);
            if (cursor != null) {
                int packageIndex = cursor.getColumnIndexOrThrow(Columns.PACKAGE);
                int createdAtIndex = cursor.getColumnIndexOrThrow(Columns.CREATED);
                while (cursor.moveToNext()) {
                    String packageName = cursor.getString(packageIndex);
                    long createdAt = cursor.getLong(createdAtIndex);
                    list.add(new Pair<String, Long>(packageName, createdAt));
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (DEBUG) {
            Log.v(TAG, "getBackupApps() size is " + list.size());
        }
        return list;
    }

    public boolean clearBackups() {
        boolean result = false;
        SQLiteDatabase db = mHelper.getWritableDatabase();
        try {
            db.delete(BACKUP_TABLE, null, null);
            result = true;
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
        }
        return result;
    }

    public boolean addBackup(String packageName) {
        if (packageName == null) {
            return false;
        }
        if (DEBUG) {
            Log.v(TAG, "addBackup() packageName is " + packageName);
        }
        boolean result = false;
        SQLiteDatabase db = mHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(Columns.PACKAGE, packageName);
            values.put(Columns.CREATED, System.currentTimeMillis());
            db.replaceOrThrow(BACKUP_TABLE, null, values);
            result = true;
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {

        }
        return result;
    }

    public boolean removeBackup(String packageName) {
        if (packageName == null || packageName.length() == 0) {
            return false;
        }
        if (DEBUG) {
            Log.v(TAG, "removeBackup() packageName is " + packageName);
        }
        boolean result = false;
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String where = Columns.PACKAGE + " =? ";
        String[] whereArgs = new String[]{packageName};
        try {
            db.delete(BACKUP_TABLE, where, whereArgs);
            result = true;
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
        }
        return result;
    }

    private static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createBackupTable(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

    }

    static void createBackupTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + BACKUP_TABLE + " ( "
                + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Columns.PACKAGE + " TEXT NOT NULL, "
                + Columns.CREATED + " INTEGER NOT NULL, "
                + " unique ( "
                + Columns.PACKAGE
                + " ) on conflict replace );";
        db.execSQL(sql);
    }

    public static class Columns implements BaseColumns {
        public static final String PACKAGE = "package";
        public static final String CREATED = "created_at";
    }

}
