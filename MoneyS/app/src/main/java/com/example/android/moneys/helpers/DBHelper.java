package com.example.android.moneys.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by stas on 16.10.2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    final String LOG_TAG = "myLogs";

    public static final String DATABASE_NAME = "money_counter";
    public static final String TABLE_NAME = "finances";

    public static final String COL1 = "id";
    public static final String COL2 = "sum";
    public static final String COL3 = "category";
    public static final String COL4 = "note";
    public static final String COL5 = "date";


    public static final String TABLE_NAME_EXPENSE = "expense";
    public static final String COL_EX = "category";


    public static final String TABLE_NAME_INCOME = "income";
    public static final String COL_IN = "category";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "--- onCreate database ---");
// creating table with rows
        db.execSQL("create table " + TABLE_NAME + " ("
                + COL1 + " integer primary key autoincrement,"
                + COL2 + " text,"
                + COL3 + " text,"
                + COL4 + " text,"
                + COL5 + " text" + ");");

        db.execSQL("create table " + TABLE_NAME_EXPENSE + " ("
                + COL1 + " integer primary key autoincrement,"
                + COL_EX + " text" + ");");

        db.execSQL("create table " + TABLE_NAME_INCOME + " ("
                + COL1 + " integer primary key autoincrement,"
                + COL_IN + " text" + ");");

        defaultValues(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void defaultValues(SQLiteDatabase db){
            db.execSQL("insert into " + TABLE_NAME_EXPENSE + "(" + COL_EX + " ) values ('Food')");
            db.execSQL("insert into " + TABLE_NAME_EXPENSE + "(" + COL_EX + " ) values ('Health')");
            db.execSQL("insert into " + TABLE_NAME_EXPENSE + "(" + COL_EX + " ) values ('Transpor')");
            db.execSQL("insert into " + TABLE_NAME_EXPENSE + "(" + COL_EX + " ) values ('Other')");
            db.execSQL("insert into " + TABLE_NAME_EXPENSE + "(" + COL_EX + " ) values ('Fun')");

        db.execSQL("insert into " + TABLE_NAME_INCOME + "(" + COL_IN + " ) values ('Salary')");
        db.execSQL("insert into " + TABLE_NAME_INCOME + "(" + COL_IN + " ) values ('Other')");
    }

    public boolean addData(String sum, String category, String note, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL2, sum);
        cv.put(COL3, category);
        cv.put(COL4, note);
        cv.put(COL5, date);

        long result = db.insert(TABLE_NAME, null, cv);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addCategoryExpense(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_EX, name);
        long result = db.insert(TABLE_NAME_EXPENSE, null,cv);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addCategoryIncome(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_IN, name);
        long result = db.insert(TABLE_NAME_INCOME, null,cv);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //query for 1 week repeats
    public Cursor getListContents() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return data;
    }



}