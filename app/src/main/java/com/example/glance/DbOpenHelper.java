package com.example.glance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper {

    private static final String GLANCE ="InnerDatabase(SQLite).db";
    private static final int DATABASE_VERSION =1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper{
        public DatabaseHelper(Context context, String name, CursorFactory factory, int version){
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DataBases.CreateDB._CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +DataBases.CreateDB.ALARM);
            onCreate(db);
        }
    }
    public void dropTable() {
        mDB.execSQL("DROP TABLE IF EXISTS " + DataBases.CreateDB.ALARM);
    }

    public DbOpenHelper(Context context){
        this.mCtx = context;
    }

    public DbOpenHelper open() throws SQLException{
        mDBHelper = new DatabaseHelper(mCtx, GLANCE, null ,DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create(){
        mDBHelper.onCreate(mDB);
    }

    public void close(){
        mDB.close();
    }

    public long insertColumn(String category, String name, String title , String text, String datetime, byte[] icon){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.CATEGORY, category);
        values.put(DataBases.CreateDB.NAME, name);
        values.put(DataBases.CreateDB.TITLE, title);
        values.put(DataBases.CreateDB.TEXT, text);
        values.put(DataBases.CreateDB.DATETIME, datetime);
        values.put(DataBases.CreateDB.ICON, icon);
        return mDB.insert(DataBases.CreateDB.ALARM, null, values);
    }

    public boolean updateColumn(long id,String category, String name, String title , String text, String datetime, byte[] icon){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.CATEGORY, category);
        values.put(DataBases.CreateDB.NAME, name);
        values.put(DataBases.CreateDB.TITLE, title);
        values.put(DataBases.CreateDB.TEXT, text);
        values.put(DataBases.CreateDB.DATETIME, datetime);
        values.put(DataBases.CreateDB.ICON, icon);
        return mDB.update(DataBases.CreateDB.ALARM, values, "_id=" + id, null) > 0;
    }

    public void deleteAllColumns(){
        mDB.delete(DataBases.CreateDB.ALARM, null, null);
    }

    public boolean deleteColumns(long id){
        return mDB.delete(DataBases.CreateDB.ALARM, "_id="+id, null) > 0;
    }

    public Cursor seekColumns(String s){
        return mDB.query(DataBases.CreateDB.ALARM, null, "category="+"\""+s+"\"", null, "name", null, "_id");
    }

    public Cursor sortColumn(){
        Cursor c = mDB.rawQuery( "SELECT alarm.category FROM alarm GROUP BY category ORDER BY _id;", null);
        return c;
    }
    public Cursor spreadColumns(String s){
        return mDB.query(DataBases.CreateDB.ALARM, null, "name="+"\""+s+"\"", null, null, null, "_id");
    }


}
