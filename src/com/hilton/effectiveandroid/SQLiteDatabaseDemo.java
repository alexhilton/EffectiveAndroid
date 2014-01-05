package com.hilton.effectiveandroid;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Bundle;
import android.util.Log;

public class SQLiteDatabaseDemo extends Activity {
    private static final String TAG = "SQLiteDatabaseDemo";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyDatabase db = new MyDatabase(this);
        
        int id = db.setName("Michael Jordan");
        Log.e(TAG, "id of " + id + " is " + db.getName(id));
    }
    
    private class MyDatabase {
        private static final String name = "demo.db";
        private static final String table = "demo";
        private final String[] projection = new String[] {"_id", "name" };
        private MyDatabaseHelper helper;
        
        public MyDatabase(Context context) {
            helper = new MyDatabaseHelper(context, name, null, 1);
        }
        
        public String getName(int id) {
            final Cursor c = helper.getReadableDatabase().query("demo", projection, "_id=" + id, 
                    null, null, null, null);
            if (c == null || !c.moveToFirst()) {
                return null;
            }
            return c.getString(1);
        }
        
        public int setName(String name) {
            ContentValues cv = new ContentValues();
            cv.put("name", name);
            return (int) helper.getWritableDatabase().insert(table, "name", cv);
        }
    }
    
    private class MyDatabaseHelper extends SQLiteOpenHelper {
        public MyDatabaseHelper(Context context, String name,
                CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE demo (_id INTEGER PRIMARY KEY, name TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int old, int newver) {
            
        }
    }
}
