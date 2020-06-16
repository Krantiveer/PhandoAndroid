package com.perseverance.phando.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SadhnaDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sadhna_db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_SEARCH_HISTORY = "sadhna_search_history";

    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    private static final String KEY_ENTRY_ID = "entry_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_TAGS = "tags";
    private static final String KEY_THUMBNAIL = "thumbnail";
    private static final String KEY_PATH = "path";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_CDN_URL = "cdn_url";
    private static final String KEY_CATEGORY = "category_id";

    public SadhnaDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {


        String CREATE_SEARCH_HISTORY_TABLE = "CREATE TABLE " + TABLE_SEARCH_HISTORY + "("
                + KEY_ID + " datetime default current_timestamp," + KEY_NAME + " TEXT PRIMARY KEY" + ")";

        db.execSQL(CREATE_SEARCH_HISTORY_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_HISTORY);
        // Create tables again
        onCreate(db);
    }


    public void deleteSearchHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_SEARCH_HISTORY, null, null);
            MyLog.e("Table deleted");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }


    public void addSearchHistory(String term) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            values.put(KEY_NAME, term);
            if (db.insert(TABLE_SEARCH_HISTORY, null, values) == -1) {
                try {
                    db.delete(TABLE_SEARCH_HISTORY, KEY_NAME + "=?", new String[]{term});
                    MyLog.e("Term: " + term + " deleted from table");
                    db.insert(TABLE_SEARCH_HISTORY, null, values);
                    MyLog.e("Term: " + term + " inserted/updated in DB");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                MyLog.e("Term: " + term + " inserted/updated in DB");
            }

        } catch (Exception e) {
            MyLog.e(e.getMessage());
        }
        db.close();
    }

    public ArrayList<String> getSearchHistory(String term) {
        ArrayList<String> history = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from " + TABLE_SEARCH_HISTORY + " where " + KEY_NAME + " like '%"
                    + term + "%' order by " + KEY_ID + " desc", null);
            cursor.moveToFirst();
            do {
                String item = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                history.add(item);
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return history;
    }
}
