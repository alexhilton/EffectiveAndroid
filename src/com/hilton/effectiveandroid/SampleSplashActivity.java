package com.hilton.effectiveandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

public class SampleSplashActivity extends Activity {
    private static final int LAUNCH_APP = 1001;

    private boolean mActive = true;
    protected int mSplashDuration = 5000; // in ms
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case LAUNCH_APP:
                launchApp();
                break;
            default:
                break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    //requestWindowFeature(Window.FEATURE_NO_TITLE);
	    //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.splash);

        /*Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (mActive && (waited < mSplashDuration)) {
                        sleep(100);
                        if (mActive) {
                            waited += 100;
                        }
                    }
                } catch (InterruptedException e) {
                    
                } finally {
                    launchApp();
                }
            }
        };
        splashThread.start();*/
        mHandler.sendEmptyMessageDelayed(LAUNCH_APP, mSplashDuration);
    }

    private void launchApp() {
        finish();
        startActivity(new Intent("com.hilton.splash.APPLICATION"));
    }
}
