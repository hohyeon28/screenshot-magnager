package com.google.sample.cloudvision;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = DBHelper.class.getSimpleName();
    public static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "tb_ocr";
    public static final String ID = "_id";
    public static final String CONTENT = "content";
    public static final String PATH = "path";

    private SQLiteDatabase mReadableDB;


    public DBHelper(Context context) {
        super(context, "tb_ocr", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String ocrSQL = "create table tb_ocr" +
                "(_id integer primary key autoincrement, " +
                "content," +"path) "; //ocr테이블 생성

        db.execSQL(ocrSQL);
    }

    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) { //중복 테이블 제거
        if (newVersion == DATABASE_VERSION) {
            db.execSQL("drop table tb_ocr");
            onCreate(db);
        }
    }

    public Cursor search(String searchString) {
        String query = String.format("SELECT * FROM %s WHERE %s LIKE '%%%s%%';", TABLE_NAME, CONTENT, searchString);

        Log.e("QUERY", query);
        Cursor cursor = null;
        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.rawQuery(query, null);
        } catch (Exception e) {
            Log.d(TAG, "SEARCH EXCEPTION! " + e); // Just log the exception
        }
        return cursor;
    }

}