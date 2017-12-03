package com.fmning.wpi_csa.cache;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.fmning.wpi_csa.helpers.Utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by fangmingning
 * On 11/6/17.
 */
//TODO: for all functions, handle failure
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

    public static void test(Context context){
        Database db = new Database(context);
        db.open();
        try{
            String query ="INSERT INTO CACHE(TYPE, MAPPING_ID, VALUE) VALUES ('Image', 1, 1)";

            db.database.execSQL(query);


        } catch (SQLException e) {
            Utils.logMsg(e.toString());
        }
        db.close();
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

    public static Cache getCache(Context context, CacheType type) {
        return getCache(context, type, 0);
    }

    public static Cache getCache(Context context, CacheType type, int mappingId) {
        Cache cache = null;

        Database db = new Database(context);
        db.open();

        try{
            String query = "SELECT ID, NAME, MAPPING_ID, VALUE FROM CACHE ";
            query += "WHERE TYPE = '" + StringUtils.capitalize(type.name().toLowerCase());
            query += "' AND MAPPING_ID = " + Integer.toString(mappingId);

            Cursor cursor = db.database.rawQuery(query, null);
            if (cursor.moveToFirst()){
                int cacheId = cursor.getInt(0);
                String name = cursor.getString(1);
                String value = cursor.getString(3);

                cache = new Cache(cacheId, name, type, mappingId, value);
            }else{
                Utils.logMsg("no records");
            }

            cursor.close();
        } catch (SQLException e) {
            Utils.logMsg(e.toString());
        }

        db.close();

        return cache;
    }

    public static void createOrUpdateImageCache(Context context, int imageId) {
        Database db = new Database(context);
        db.open();

        try {
            String query = "SELECT ID FROM CACHE WHERE TYPE = 'Image' AND MAPPING_ID = ";
            query += Integer.toString(imageId);

            Cursor cursor = db.database.rawQuery(query, null);
            if (cursor.moveToFirst()){
                int cacheId = cursor.getInt(0);
                String updateQuery = "UPDATE CACHE SET VALUE = VALUE + 1 WHERE ID = ";
                updateQuery += Integer.toString(imageId);
                run(context, updateQuery);
            } else {
                String insertQuery = "INSERT INTO CACHE(TYPE, MAPPING_ID, VALUE) VALUES ('Image', ";
                insertQuery += Integer.toString(imageId) + ", '1')";
                run(context, insertQuery);
            }

            cursor.close();
        } catch (SQLException e){
            Utils.logMsg(e.toString());
        }

        db.close();
    }

    public static void deleteCache(Context context, int id){
        run(context, "DELETE FROM CACHE WHERE ID = " + Integer.toString(id));
    }

    public static void imageHit(Context context, int id){
        String query = "UPDATE CACHE SET VALUE = VALUE + 1 WHERE TYPE = 'Image' ";
        query += "AND MAPPING_ID = " + Integer.toString(id);
        run(context, query);
    }

    public static void run(Context context, String queries) {
        Database db = new Database(context);
        db.open();
        try {
            db.database.execSQL(queries);
        } catch (SQLException e){
            Utils.logMsg(e.toString());
        }
        db.close();
    }

}
