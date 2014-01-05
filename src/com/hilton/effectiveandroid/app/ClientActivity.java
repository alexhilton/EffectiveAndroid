package com.hilton.effectiveandroid.app;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ClientActivity extends Activity {
    private static final String TAG = "ClientActivity";
    private MyInterfaceDemo mService;
    
    @Override
    public void onCreate(Bundle icicle) {
	super.onCreate(icicle);
	final Button testButton = new Button(getApplication());
	testButton.setText("click to test");
	testButton.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		if (mService == null) {
		    Toast.makeText(getApplication(), "Oops service is null", Toast.LENGTH_LONG).show();
		    return;
		}
		try {
		    mService.sayHello();
		    mService.say("Freedom is nothing but a chance to be better");
		    Toast.makeText(getApplication(), mService.getResponse(), Toast.LENGTH_LONG).show();
		} catch (RemoteException e) {
		    e.printStackTrace();
		}
	    }
	});
	setContentView(testButton);
	bindToService(mCallback);
    }
    
    @Override
    public void onDestroy() {
	super.onDestroy();
	unbindService(mCallback);
    }
    
    /*
     * You see, not only Activity can bind to a service, all components extends from Context can bind to 
     * a service, as long as you can call startService() and bindService().
     */
    private void bindToService(ServiceConnection osc) {
	final Intent service = new Intent(getApplication(), RemoteService.class);
	bindService(service, osc, Service.BIND_AUTO_CREATE);
    }
    
    private ServiceConnection mCallback = new ServiceConnection() {
	public void onServiceConnected(ComponentName className, IBinder stub) {
	    Log.e(TAG, "mCallback.onServiceConnection, good className " + className + ", stub " +stub);
	    mService = MyInterfaceDemo.Stub.asInterface(stub);
	}

	public void onServiceDisconnected(ComponentName name) {
	    Log.e(TAG, "mCallback.onServiceDisconnection, bye bye name " + name);
	    mService = null;
	}
    };
}