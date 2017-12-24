package com.fmning.wpi_csa.cache;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.fmning.wpi_csa.helpers.Utils;
import com.fmning.wpi_csa.objects.Article;
import com.fmning.wpi_csa.objects.Cache;
import com.fmning.wpi_csa.objects.Menu;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

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

    /*public static void test(Context context){
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
    }*/

    public List<Menu> getSubMenus(int menuId, String prefix) {
        String query;
        List<Menu> menuList= new ArrayList<>();

        try {
            if (menuId == 0) {
                query = "SELECT ID, TITLE FROM ARTICLES WHERE PARENT_ID IS NULL ORDER BY POSITION ASC";
            } else {
                query = "SELECT ID, TITLE FROM ARTICLES WHERE PARENT_ID = " + Integer.toString(menuId)
                        + " ORDER BY POSITION ASC";
            }

            Cursor cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()){
                do{
                    int id = cursor.getInt(0);
                    String name = cursor.getString(1);
                    name = prefix + name;
                    Menu menu = new Menu(id, name);

                    menu.subMenus = getSubMenus(id, prefix + "    ");
                    menu.isParentMenu = menu.subMenus.size() > 0;
                    if (!menu.isParentMenu) {
                        Utils.menuOrderList.add(id);
                    }
                    menuList.add(menu);
                }while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            Utils.logMsg(e.toString());
        }

        return menuList;
    }

    public List<Menu> searchArticles(String keyword) {
        String query = "SELECT ID, TITLE FROM ARTICLES WHERE CONTENT LIKE '%" + keyword + "%'";
        List<Menu> menuList= new ArrayList<>();

        try {
            Cursor cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()){
                do{
                    int id = cursor.getInt(0);
                    String name = cursor.getString(1);

                    menuList.add(new Menu(id, name));
                }while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            Utils.logMsg(e.toString());
        }

        return menuList;
    }

    public String getMenuTitle(int menuId) {
        String query = "SELECT TITLE FROM ARTICLES WHERE ID = " + Integer.toString(menuId);
        String name = "";

        try {
            Cursor cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()){
                name = cursor.getString(0);
            } else {
                Utils.logMsg("Article not found");
            }
            cursor.close();
        } catch (SQLException e) {
            Utils.logMsg(e.toString());
        }

        return name;
    }

    public Article getArticle(int menuId) {
        String query = "SELECT CONTENT FROM ARTICLES WHERE ID = " + Integer.toString(menuId);
        Article article;

        try {
            Cursor cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()){
                String content = cursor.getString(0);
                article = new Article(content);
            } else {
                Utils.logMsg("Article not found");
                article = new Article("");
            }
            cursor.close();
        } catch (SQLException e) {
            Utils.logMsg(e.toString());
            article = new Article("");
        }

        article.menuId = menuId;
        int index = Utils.menuOrderList.indexOf(menuId);

        if (index != Utils.menuOrderList.size() - 1) {
            article.nextMenuId = Utils.menuOrderList.get(index + 1);//TODO: Check for index?
            article.nextMenuText = getMenuTitle(article.nextMenuId);
        }
        if (index != 0) {
            article.prevMenuId = Utils.menuOrderList.get(index - 1);//TODO: Check for index?
            article.prevMenuText = getMenuTitle(article.prevMenuId);
        }

        return article;
    }

    public static Cache getCache(Context context, CacheType type) {
        return getCache(context, type, 0);
    }

    static Cache getCache(Context context, CacheType type, int mappingId) {
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

    static void createOrUpdateImageCache(Context context, int imageId) {
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

    static void deleteCache(Context context, int id){
        run(context, "DELETE FROM CACHE WHERE ID = " + Integer.toString(id));
    }

    static void imageHit(Context context, int id){
        String query = "UPDATE CACHE SET VALUE = VALUE + 1 WHERE TYPE = 'Image' ";
        query += "AND MAPPING_ID = " + Integer.toString(id);
        run(context, query);
    }

    public static void run(Context context, String queries) {
        Database db = new Database(context);
        db.open();
        try {
            String[] queryArr = queries.split(";");
            for(String query : queryArr){
                db.database.execSQL(query);
            }
        } catch (SQLException e){
            Utils.logMsg(e.toString());
        }
        db.close();
    }

}
