package com.hilton.effectiveandroid.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class AnotherActivity extends Activity {
    private static final String TAG = "AnotherActivity";
    private Handler mMainHandler = new Handler(getMainLooper(), new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            // this will cause ANR
            Log.e(TAG, "you know what this is very slow slow slow slow");
            SystemClock.sleep(20 * 1000);
            dumpThreadInfo();
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	dumpThreadInfo();
	super.onCreate(savedInstanceState);
	
	setTitle("this is another activity");
	mMainHandler.sendEmptyMessageDelayed(0, 500);
    }

    @Override
    protected void onDestroy() {
	dumpThreadInfo();
	super.onDestroy();
    }

    public void dumpThreadInfo() {
	Thread.dumpStack();
	Log.e(TAG, Thread.currentThread().toString());
	Log.e(TAG, " " + getMainLooper());
    }
}