package com.fmning.wpi_csa.cache;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.fmning.wpi_csa.helpers.Utils;

/**
 * Created by fangmingning
 * On 11/6/17.
 */

public class Database {
    private final Context context;
    private SQLiteDatabase database;
    private DatabaseOpenHelper dbHelper;

    public Database(Context context){
        this.context = context;
        dbHelper = new DatabaseOpenHelper(context);
    }

    public Database open() throws SQLException
    {
        try
        {
            dbHelper.openDataBase();
            dbHelper.close();
            database = dbHelper.getReadableDatabase();
        }
        catch (SQLException e)
        {
            Utils.logMsg(e.toString());
        }
        return this;
    }

    public void close()
    {
        dbHelper.close();
    }

    public void test(){
        try{
            String query ="SELECT title FROM articles where id < 10";

            Cursor cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()){
                do{
                    String a = cursor.getString(0);
                    Utils.logMsg("Read " + a);
                }while (cursor.moveToNext());
            }else{
                Utils.logMsg("no records");
            }

            cursor.close();
        } catch (SQLException e) {
            Utils.logMsg(e.toString());
        }
    }

    public static void test1(Context context){
        Database db = new Database(context);
        db.open();

        try{
            String query ="SELECT title FROM articles where id < 10";

            Cursor cursor = db.database.rawQuery(query, null);
            if (cursor.moveToFirst()){
                do{
                    String a = cursor.getString(0);
                    Utils.logMsg("Read " + a + "in static");
                }while (cursor.moveToNext());
            }else{
                Utils.logMsg("no records");
            }

            cursor.close();
        } catch (SQLException e) {
            Utils.logMsg(e.toString());
        }

        db.close();
    }
}
