package com.hilton.effectiveandroid.networks;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hilton.effectiveandroid.R;

public class NetworksTesterActivity extends Activity {
    private static final String TAG = "NetworksManagerActivity";
	private Button mButtonEnable;
	private Button mButtonWifi;
	private TextView mInfoPanel;
	private ConnectivityManager mConnectivityManager;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.networks_manager_activity);
        
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        
        mInfoPanel = (TextView) findViewById(R.id.info_panel);
        mButtonEnable = (Button) findViewById(R.id.button_enable);
        mButtonEnable.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // TODO, find a way to enable 3G/GPRS/GSM...
            }
        });
        mButtonWifi = (Button) findViewById(R.id.button_wifi);
        mButtonWifi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(NetworksTesterActivity.this, WifiManagerActivity.class));
            }
        });
        updateStatus();
    }

	private void updateStatus() {
		StringBuilder info = new StringBuilder();
        boolean backgroundDataEnabled = mConnectivityManager.getBackgroundDataSetting();
        info.append("Background data is " + (backgroundDataEnabled ? "enabled\n" : "disabled\n"));
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            info.append("Oops, no active networks available");
            return;
        }
        int networkType = networkInfo.getType();
        switch (networkType) {
        case ConnectivityManager.TYPE_MOBILE:
        	info.append("Mobile networks is available\n");
        	break;
        case ConnectivityManager.TYPE_WIFI:
        	info.append("Wifi network is available\n");
        	break;
        default:
        	break;
        }
        
        int desiredType = ConnectivityManager.TYPE_MOBILE;
        networkInfo = mConnectivityManager.getNetworkInfo(desiredType);
        NetworkInfo.State state = networkInfo.getState();
        switch (state) {
        case DISCONNECTED:
            info.append("mobile network is disconnected\n");
            break;
        case DISCONNECTING:
            info.append("mobile network is disconnecting\n");
            break;
        case CONNECTED:
            info.append("mobile network is connected\n");
            break;
        case CONNECTING:
            info.append("mobile network is connecting\n");
            break;
        default:
            info.append("mobile network state is unknown\n");
            break;
        }

        // After reviewing the source codes, we know that the returned type of ConnectivityManager#getNetworkPreference()
        // and parameter of ConnectivityManager#setNetworkPreference(int) are network types: ConnectivityManager#TYPE_WIFI
        // and ConnectivityManager#TYPE_3G
        int pref = mConnectivityManager.getNetworkPreference();
        info.append("Netowrk preference is " + pref);
        mConnectivityManager.setNetworkPreference(ConnectivityManager.TYPE_WIFI);
        mInfoPanel.setText(info.toString());
	}

    private BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                Log.e(TAG, "onReceive, oops receive empty intent");
                return;
            }
            final String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                final String extraInfo = intent.getStringExtra(ConnectivityManager.EXTRA_EXTRA_INFO);
                final boolean failover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
                final NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                final boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                final NetworkInfo otherInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
                final String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
                Log.e(TAG, "intent is " + intent);
                Log.e(TAG, "extraInfo " + extraInfo);
                Log.e(TAG, "extra no connectivity " + noConnectivity);
            } else if (action.equals(ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED)) {
                final boolean backgroundDataEnabled = mConnectivityManager.getBackgroundDataSetting();
                Toast.makeText(NetworksTesterActivity.this, "Background data setting has changed, now it is " + 
                    (backgroundDataEnabled ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
            }
        }
    };

	private void registerBroadcastReceiver() {
		final IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mConnectivityReceiver, filter);
	}

    private void unregisterBroadcastReceiver() {
        unregisterReceiver(mConnectivityReceiver);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterBroadcastReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerBroadcastReceiver();
    }
}
