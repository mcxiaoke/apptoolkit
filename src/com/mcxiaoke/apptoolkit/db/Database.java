package com.mcxiaoke.apptoolkit.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Project: apptoolkit
 * Package: com.mcxiaoke.apptoolkit.db
 * User: mcxiaoke
 * Date: 13-7-3
 * Time: 下午10:42
 */
public class Database {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "app.db";

    static final String APP_TABLE = "app";
    static final String PROCESS_TABLE = "process";
    static final String BACKUP_TABLE = "backup";


    private static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

}
