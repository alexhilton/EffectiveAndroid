package com.hilton.effectiveandroid.concurrent;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

// TODO
public class TransactionServer {
    protected static final String TAG = null;
    protected static final int DO_UPDATE = 0;
    protected static final int DO_DELETE = 1;
    protected static final int DO_QUERY = 2;
    
    public TransactionServer() {
	
    }
    
    private class WorkerThread extends Thread {
	private Handler mThreadHandler;
	private Looper mLooper;
	
	public void run() {
	    mLooper = Looper.myLooper();
	    Looper.prepare();
	    mThreadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		    switch (msg.what) {
		    case DO_UPDATE:
			Log.e(TAG, "do the updating");
		    case DO_DELETE:
			Log.e(TAG, "do the deleting");
		    case DO_QUERY:
			Log.e(TAG, "do the querying");
		    }
		}
	    };
	    Looper.loop();
	}
	
	public void addAction() {
	    
	}
    }
}
