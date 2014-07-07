package com.zhuang.quickcall.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.logging.DevLog;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public abstract class QuickCallDbTable {
	
	private static final String TAG = "[ZHUANG]QuickCallDbTable";
	
	private static final String SQLITE_STMT_LIST_TABLES = "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android%'";
	private static final String SQLITE_TABLE_NAME_COLUMN = "name";
	private static final String SQLITE_STMT_TEMPLATE_LIST_COLUMNS = "SELECT * FROM %s LIMIT 1";
	private static final String SQLITE_STMT_TEMPLATE_DROP_TABLE = "DROP TABLE IF EXISTS %s";
	private static final String SQLITE_STMT_TEMPLATE_RENAME_TABLE = "ALTER TABLE %s RENAME TO %s";
	private static final String SQLITE_STMT_TEMPLATE_COPY_COLUMNS = "INSERT INTO %s (%s) SELECT %s FROM %s";

    abstract String getName();
    
    abstract void onCreate(SQLiteDatabase db);

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, String tempName) {
        if (LogLevel.DEV) {
            try {
                DevLog.d(TAG, getName() + ".onUpgrade(oldVersion = " + oldVersion + ", newVersion = " + newVersion + ", tempName = " + tempName + ")");
            } catch (java.lang.Throwable th){
            }
        }
        
        renameTable(db, getName(), tempName);
        onCreate(db);
        joinColumns(db, tempName, getName());
        dropTable(db, tempName);
        
    }
    
	static Collection<String> listTables(SQLiteDatabase db) {
		if (LogLevel.DEV) {
			DevLog.d(TAG, "listTables()...");
		}

		Cursor cursor = db.rawQuery(SQLITE_STMT_LIST_TABLES, null);
		if (cursor == null || !cursor.moveToFirst()) {
			if (cursor != null) {
				cursor.close();
			}
			
			if (LogLevel.DEV) {
				DevLog.d(TAG, "listTables(): there are no tables in db.");
			}
			return null;
		}

		int table_name_column = cursor.getColumnIndex(SQLITE_TABLE_NAME_COLUMN);
		HashSet<String> tables = new HashSet<String>(cursor.getCount());
		do {
			tables.add(cursor.getString(table_name_column));
		} while (cursor.moveToNext());
		cursor.close();

		if (LogLevel.DEV) {
			DevLog.d(TAG, "listTables(): " + tables);
		}

		return tables;
	}

	static List<String> listColumns(SQLiteDatabase db, String table) {
		if (LogLevel.DEV) {
			DevLog.d(TAG, "listColumns(" + table + ")...");
		}

		Cursor cursor = db.rawQuery(String.format(SQLITE_STMT_TEMPLATE_LIST_COLUMNS, table), null);
		if (cursor == null) {
			if (LogLevel.DEV) {
				DevLog.d(TAG, "listColumns(" + table + "): no columns in table " + table);
			}
			return null;
		}

		List<String> columns = Arrays.asList(cursor.getColumnNames());
		cursor.close();

		return columns;
	}

	public static void dropTable(SQLiteDatabase db, String table) {
		if (LogLevel.DEV) {
			DevLog.d(TAG, "dropTable(" + table + ")...");
		}

		db.execSQL(String.format(SQLITE_STMT_TEMPLATE_DROP_TABLE, table));
	}

	public static void renameTable(SQLiteDatabase db, String oldName, String newName) {
		if (LogLevel.DEV) {
			DevLog.d(TAG, "renameTable(" + oldName + ", " + newName + ")...");
		}

		db.execSQL(String.format(SQLITE_STMT_TEMPLATE_RENAME_TABLE, oldName,
				newName));
	}

	public static void joinColumns(SQLiteDatabase db, String oldTable, String newTable) {
		if (LogLevel.DEV) {
			DevLog.d(TAG, "joinColumns(" + oldTable + ", " + newTable + ")...");
		}

		db.delete(newTable, null, null);

		ArrayList<String> old_columns = new ArrayList<String>(listColumns(db, oldTable));
		List<String> new_columns = listColumns(db, newTable);
		old_columns.retainAll(new_columns);

		String common_columns = TextUtils.join(",", old_columns);
		if (LogLevel.DEV) {
			DevLog.d(TAG, "joinColumns: Common columns: " + common_columns);
		}

		db.execSQL(String.format(SQLITE_STMT_TEMPLATE_COPY_COLUMNS, newTable,
				common_columns, common_columns, oldTable));
	}
}
