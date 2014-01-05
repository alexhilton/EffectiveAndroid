package com.hilton.effectiveandroid;

import com.hilton.effectiveandroid.R;
import com.hilton.effectiveandroid.app.MediaButtonReceiver;
import com.hiltoneffectiveandroid.view.ViewStubDemoActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class EffectiveAndroidActivity extends Activity {
    protected static final String TAG = "EffectiveAndroidActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button startInternalActivity = (Button) findViewById(R.id.start_internal_activity);
        startInternalActivity.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent i = new Intent();
                i.setComponent(new ComponentName(getApplication(), ViewStubDemoActivity.class));
                i.setComponent(new ComponentName(getApplication(), ViewStubDemoActivity.class.getName()));
                i.setComponent(new ComponentName(getApplication().getPackageName(), ViewStubDemoActivity.class.getName()));
                i.setClass(getApplication(), ViewStubDemoActivity.class);
                i.setClassName(getApplication(), ViewStubDemoActivity.class.getName());
                i.setClassName(getApplication().getPackageName(), ViewStubDemoActivity.class.getName());
                Log.e(TAG, "pkg name: " + getApplication().getPackageName() + ", clz name: " + ViewStubDemoActivity.class.getName());
                startActivity(i);
            }
        });
        
        Button startExternalActivity = (Button) findViewById(R.id.start_external_activity);
        startExternalActivity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent();
//                i.setComponent(new ComponentName(getApplication(), "com.hilton.networks.WifiManagerActivity")); no way, context is not right
                i.setComponent(new ComponentName("com.hilton.networks", "com.hilton.networks.WifiManagerActivity"));
//                i.setClassName(getApplication(), "com.hilton.networks.WifiManagerActivity");
                i.setClassName("com.hilton.networks", "com.hilton.networks.WifiManagerActivity");
                startActivity(i);
            }
        });
        
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.registerMediaButtonEventReceiver(new ComponentName(this, MediaButtonReceiver.class.getName()));
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}