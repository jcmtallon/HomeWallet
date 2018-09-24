package com.hoo.tally.homewallet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.List;

public class MyHistoryDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "history.db";
    public static  final String TABLE_EXPENSES = "history";

    public static  final String COLUMN_ID = "_id";
    public static  final String COLUMN_CATEGORY = "category";
    public static  final String COLUMN_QUANTITY = "quantity";
    public static  final String COLUMN_DATE = "date";

    public MyHistoryDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_EXPENSES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_QUANTITY + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_DATE + " TEXT " +
                ");";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        onCreate(sqLiteDatabase);
    }

    //Add a new row to the database
    public void addRow(String rowQuantity, String rowCategory, String rowDate){
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY, rowQuantity);
        values.put(COLUMN_CATEGORY, rowCategory);
        values.put(COLUMN_DATE, rowDate);
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_EXPENSES, null, values);
        sqLiteDatabase.close();
    }

    //Delete a product from the database
    public void deleteExpense(String expenseId){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + TABLE_EXPENSES + " WHERE " + COLUMN_ID + "=\"" + expenseId + "\";" );
    }

    //Print out the database as a string
    public void databaseToArray(List<HashMap<String,String>> listItems) {

        listItems.clear();

        //This query will initiate a loop through the database table.
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_EXPENSES + " WHERE 1";

        //Cursor point to a location in your results
        Cursor c = sqLiteDatabase.rawQuery(query, null);

        //Move to the first row in your results
        c.moveToFirst();

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("quantity"))!=null){
                HashMap<String, String> resultsMap = new HashMap<>();
                resultsMap.put("First Line", c.getString(c.getColumnIndex("quantity")));
                resultsMap.put("Second Line", c.getString(c.getColumnIndex("category")));
                resultsMap.put("Third Line", c.getString(c.getColumnIndex("date")));
                resultsMap.put("Forth Line", c.getString(c.getColumnIndex("_id")));

                listItems.add(resultsMap);
            }
            c.moveToNext();
        }
        sqLiteDatabase.close();

    }

    //Clears database
    public void clearTable(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_EXPENSES,null,null);
    }

    public Cursor raw() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_EXPENSES , new String[]{});

        return res;
    }

}
