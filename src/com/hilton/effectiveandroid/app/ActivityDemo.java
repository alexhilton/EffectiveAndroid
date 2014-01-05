package com.hilton.effectiveandroid.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ActivityDemo extends Activity {

    private static final String TAG = "ActivityDemo";

    private Handler mMainHandler = new Handler(new Handler.Callback() {
	public boolean handleMessage(Message msg) {
	    dumpThreadInfo();
	    return false;
	}
    });
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	dumpThreadInfo();
	super.onCreate(savedInstanceState);
	
	// add four buttons
	LinearLayout layout = new LinearLayout(getApplication());
	layout.setOrientation(LinearLayout.VERTICAL);
	Button startService = new Button(getApplication());
	startService.setText("Start a Service");
	startService.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		Intent i = new Intent(getApplication(), ServiceDemo.class);
		startService(i);
	    }
	});
	layout.addView(startService);
	Button startAnother = new Button(getApplication());
	startAnother.setText("Start another Activity");
	startAnother.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		Intent i = new Intent(getApplication(), AnotherActivity.class);
		startService(i);
	    }
	});
	layout.addView(startAnother);
	Button startContentProvider = new Button(getApplication());
	startContentProvider.setText("Start a ContentProvider");
	startContentProvider.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		getContentResolver().query(ContentProviderDemo.CONTENT_URI, null, null, null, null);
	    }
	});
	layout.addView(startContentProvider);
	Button startReceiver = new Button(getApplication());
	startReceiver.setText("Start a BroadcastReceiver");
	startReceiver.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		Intent i = new Intent("android.action.start_broadcastreceiver_demo");
		sendBroadcast(i);
	    }
	});
	layout.addView(startReceiver);
	setContentView(layout);
	
	mMainHandler.sendEmptyMessageDelayed(0, 500);
    }
  
    public void dumpThreadInfo() {
	Thread.dumpStack();
	Log.e(TAG, Thread.currentThread().toString());
	Log.e(TAG, " " + getMainLooper());
    }
}