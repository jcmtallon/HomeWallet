package com.hoo.tally.homewallet;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.List;

public class MyCatDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "categories.db";
    public static final String TABLE_CATS = "categories";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CATEGORYNAME = "categoryName";


    public MyCatDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_CATS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CATEGORYNAME + " TEXT " +
                ");";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CATS);
        onCreate(sqLiteDatabase);
    }

    //Add a new row to the database
    public void addCategory(Categories cati){
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORYNAME, cati.get_categoryName());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_CATS, null, values);
        sqLiteDatabase.close();
    }

    //Delete a product from the database
    public void deleteCategory(String catId){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + TABLE_CATS + " WHERE " + COLUMN_ID + "=\"" + catId + "\";" );
    }

    //Print out the database (Array with only one String)
    public void databaseToArray(List<String> listItems){

        // We clean the listItem just to make sure that it is empty.
        listItems.clear();

        //This query will initiate a loop through the database table.
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CATS + " WHERE 1";

        //Cursor point to a location in your results
        Cursor c = sqLiteDatabase.rawQuery(query, null);

        //Move to the first row in your results
        c.moveToFirst();

        while(!c.isAfterLast()){
            //if(c.getString(c.getColumnIndex("categories"))!=null){
                listItems.add(c.getString(c.getColumnIndex("categoryName")));
            //}
            c.moveToNext();
        }
        sqLiteDatabase.close();
    }

    //Print out the database (gets two strings) Used in Category Screen.
    public void databaseToArrayIncludingID( List<HashMap<String,String>> listItems){

        // We clean the listItem just to make sure that it is empty.
        listItems.clear();

        //This query will initiate a loop through the database table.
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CATS + " WHERE 1";

        //Cursor point to a location in your results
        Cursor c = sqLiteDatabase.rawQuery(query, null);

        //Move to the first row in your results
        c.moveToFirst();

        while(!c.isAfterLast()){
            //if(c.getString(c.getColumnIndex("categories"))!=null){
            HashMap<String, String> resultsMap = new HashMap<>();
            resultsMap.put("ID Line", c.getString(c.getColumnIndex("_id")));
            resultsMap.put("Category Line", c.getString(c.getColumnIndex("categoryName")));
            listItems.add(resultsMap);
            //}
            c.moveToNext();
        }
        sqLiteDatabase.close();
    }

    //Clears database
    public void clearTable(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_CATS,null,null);
    }

}
