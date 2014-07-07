/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.provider;

import java.util.LinkedHashMap;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * EasiioDataStore
 * @author gavin.zhuang
 */
public class QuickCallDataStore {
	
	private QuickCallDataStore() {};

    public static final int DB_VERSION = 3;
    static final String DB_FILE = "quickcall.db";
    
    public interface QuickCallColumns {
        public static final String DEFAULT_SORT_ORDER = "_ID ASC";
    }
    
    public static final class CurrentUserTable extends QuickCallDbTable implements BaseColumns, QuickCallColumns {

        private CurrentUserTable() {};
        private static final CurrentUserTable sInstance = new CurrentUserTable();
        public static CurrentUserTable getInstance() {
            return sInstance;
        }

        public static final String TABLE_NAME = "CurrentUserTab";
        
        private static final String CREATE_TABLE_STMT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
          + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT "
          + ");";


        @Override
        String getName() {
            return TABLE_NAME;
        }
        
        @Override
        void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_STMT);
        }
    }
    
    public static final class QuickCallTable extends QuickCallDbTable implements BaseColumns, QuickCallColumns {

        private QuickCallTable() {};
        private static final QuickCallTable sInstance = new QuickCallTable();
        public static QuickCallTable getInstance() {
            return sInstance;
        }

        public static final String TABLE_NAME = "QuickCallTab";
        
        public static final String QUICK_CALL_NUMBER = "QuickCallNumber";
        public static final String QUICK_CALL_NAME = "QuickCallName";
        public static final String QUIKC_CALL_PHOTO_ID = "QuickCallPhotoID";
        public static final String QUICK_CALL_TOUCH_TRACK = "QuickCallTouchTrack";
        public static final String CREATE_TIME = "CreateTime";
        
        private static final String CREATE_TABLE_STMT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
          + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
          + QUICK_CALL_NUMBER + " TEXT, "
          + QUICK_CALL_NAME + " TEXT, "
          + QUIKC_CALL_PHOTO_ID + " INTEGER, "
          + QUICK_CALL_TOUCH_TRACK + " TEXT UNIQUE ON CONFLICT REPLACE, "
          + CREATE_TIME + " INTEGER "
          + ");";


        @Override
        String getName() {
            return TABLE_NAME;
        }
        
        @Override
        void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_STMT);
        }
    }
    
    static LinkedHashMap<String, QuickCallDbTable> sQuickCallDbTables = new LinkedHashMap<String, QuickCallDbTable>();
    
    static {
    	sQuickCallDbTables.put(CurrentUserTable.getInstance().getName(), CurrentUserTable.getInstance());
    	sQuickCallDbTables.put(QuickCallTable.getInstance().getName(), QuickCallTable.getInstance());
    }
}
