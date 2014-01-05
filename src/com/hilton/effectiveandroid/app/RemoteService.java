package com.hilton.effectiveandroid.app;

import com.hilton.effectiveandroid.BuildConfig;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class RemoteService extends Service {
    private static final String TAG = "ServiceDemo";
    @Override
    public IBinder onBind(Intent intent) {
	Log.e(TAG, "onBind, intent " + intent);
	return mBinder;
    }

    @Override
    public void onCreate() {
	Log.e(TAG, "onCreate, create the service");
	super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	if (BuildConfig.DEBUG) {
	    Log.e(TAG, "onStartCommand, intent " + intent + " flags " + flags + " start id " + startId);
	}
	return super.onStartCommand(intent, flags, startId);
    }

    public void say(String msg) {
	Log.e(TAG, "Service.say: '" + msg + "';");
	/*
	 * TODO: weird enough, if bind to this service from another application and call say(), it will
	 * raise an exception saying Toast is not called from main thread. Need to find out why.
	 */
//	Toast.makeText(getApplication(), msg, Toast.LENGTH_LONG).show();
    }

    public String response() {
	return "With great power comes great responsibility";
    }

    private IBinder mBinder =  new ServiceStub(this);
    
    /*
     * Question: 
     * 1. What is IBinder?
     * 2. Why have to extends InterfaceDemo.Stub? Can we implements InterfaceDemo? In that way, 
     * we can use ServiceDemo implements the InterfaceDemo directly.
     */
    private class ServiceStub extends MyInterfaceDemo.Stub {
	private RemoteService mService;
	public ServiceStub(RemoteService service) {
	    mService = service;
	}

	public void sayHello() throws RemoteException {
	    mService.say("Hello, world");
	}
	public void say(String msg) throws RemoteException {
	    mService.say(msg);
	}
	public String getResponse() throws RemoteException {
	    return mService.response();
	}
    }
}