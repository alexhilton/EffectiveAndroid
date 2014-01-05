package com.hilton.effectiveandroid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class PermissionCheckActivity extends Activity {
    private static final String TAG = "PermissionCheckActivity";
    private TextView mTextView;
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.permission);
        mTextView = (TextView) findViewById(R.id.permission_text);
        try {
            readMmsDatabase();
        } catch (Exception e) {
            Log.e(TAG, "e");
            e.printStackTrace();
        }
        try {
            readMmsInternalFiles();
        } catch (IOException e) {
            Log.e(TAG, "exception caught", e);
        }
    }
    
    private void readMmsDatabase() throws IOException {
        final String mmsDBPath = "/data/data/com.android.providers.telephony/databases/mmssms.db";
        File file = new File(mmsDBPath);
        printFileInfo(file);
        final String dbname = "mms_database.db";
        try {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(mmsDBPath);
                out = openFileOutput(dbname, Context.MODE_PRIVATE);
                byte[] buf = new byte[8096];
                int seg;
                while ((seg = in.read(buf)) != -1) {
                    out.write(buf, 0, seg);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "exception caught ", e);
        }
        printFileInfo(getFileStreamPath(dbname));
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbname, null);
        if (db == null) {
            mTextView.setText("Oops, failed to open database");
            return;
        }
        Cursor cursor = db.query("threads", null, null, null, null, null, null);
        DatabaseUtils.dumpCursor(cursor);
    }
    
    private static class MyCursorFactory implements CursorFactory {
        public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
                String editTable, SQLiteQuery query) {
            return new SQLiteCursor(db, driver, editTable, query);
        }
    }
    
    private void readMmsInternalFiles() throws IOException {
        final String path = "/data/data/com.android.mms/shared_prefs/com.android.mms_preferences.xml";
        File file = new File(path);
        printFileInfo(file);
        final String privateFile = "/data/data/com.android.mms/files/mms_private.txt";
        try {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(privateFile)));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    mTextView.setText(line);
                    Log.e(TAG, "content in " + privateFile + " is:" + line);
                }
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "what is this", e);
        }
//        printFileInfo(new File(privateFile));
    }
    
    private String printFileInfo(final File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(file);
        sb.append("\nabsolute path: " + file.getAbsolutePath());
        sb.append("\ncanonical path: " + file.getCanonicalPath());
        sb.append("\nexists: " + file.exists());
        sb.append("\nlength: " + file.length());
//        mTextView.setText(sb.toString());
        Log.e(TAG, "fileinfo:" + sb.toString());
        return sb.toString();
    }
}