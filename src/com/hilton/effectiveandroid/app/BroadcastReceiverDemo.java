package com.hilton.effectiveandroid.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastReceiverDemo extends BroadcastReceiver {

    private static final String TAG = "BroadcastReceiverDemo";
    @Override
    public void onReceive(Context context, Intent intent) {
	Log.e(TAG, "intent is " + intent);
	dumpThreadInfo();
    }
    
    public void dumpThreadInfo() {
	Thread.dumpStack();
	Log.e(TAG, Thread.currentThread().toString());
    }
}