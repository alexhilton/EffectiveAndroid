package com.hilton.effectiveandroid.app;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class ServiceDemo extends Service {
    private Handler mMainHandler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            // this will cause ANR, too
            Log.e(TAG, "this is very slow you know, slow slow");
            SystemClock.sleep(20 * 1000);
            dumpThreadInfo();
            return false;
        }
    });
    private static final String TAG = "ServiceDemo";
    @Override
    public IBinder onBind(Intent arg0) {
	dumpThreadInfo();
	return null;
    }

    @Override
    public void onCreate() {
	dumpThreadInfo();
	super.onCreate();
	mMainHandler.sendEmptyMessageDelayed(0, 500);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	dumpThreadInfo();
	return super.onStartCommand(intent, flags, startId);
    }
    
    public void dumpThreadInfo() {
	Thread.dumpStack();
	Log.e(TAG, Thread.currentThread().toString());
	Log.e(TAG, " " + getMainLooper());
    }
}