package com.hilton.effectiveandroid.app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

public class ContentProviderDemo extends ContentProvider {
    public static final Uri CONTENT_URI = Uri.parse("content://com.hilton.effectiveandroid.app/content");
    private static final String TAG = "ContentProviderDemo";
    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {
	dumpThreadInfo();
	return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
	dumpThreadInfo();
	return null;
    }

    @Override
    public boolean onCreate() {
	dumpThreadInfo();
	return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
	dumpThreadInfo();
	// it will cause ANR of course
	Log.e(TAG, "this is very slow, you know that");
	SystemClock.sleep(20 * 1000);
	return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
	dumpThreadInfo();
	return 0;
    }
    
    public void dumpThreadInfo() {
	Thread.dumpStack();
	Log.e(TAG, Thread.currentThread().toString());
    }

    @Override
    public String getType(Uri arg0) {
	return null;
    }
}