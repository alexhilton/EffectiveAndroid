package com.hilton.effectiveandroid.networks;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hilton.effectiveandroid.R;

public class WifiManagerActivity extends Activity {
    private static final String TAG = "WifiManagerActivity";

    // Dialog ids
    private static final int SCANNING_PROGRESS = 1001;

    // Handler message
    private static final int GET_SCAN_RESULT = 10001;

    private WifiManager mWifiManager;
    private TextView mInfoPanel;
    private Button mConnect;
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case GET_SCAN_RESULT:
                updateHotspotList();
                break;
            default:
                break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_manager_activity);
        mInfoPanel = (TextView) findViewById(R.id.info_panel);
        ListView list = (ListView) findViewById(R.id.hotspot_list);
        TextView hotspotHeader = new TextView(this);
        hotspotHeader.setText("List of hotspots after scanning");
        list.addHeaderView(hotspotHeader);
        ListView configList = (ListView) findViewById(R.id.config_list);
        TextView configHeader = new TextView(this);
        configHeader.setText("List of configed wifi");
        configList.addHeaderView(configHeader);
   
        final Button enable = (Button) findViewById(R.id.button_enable);
        enable.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View view) {
                if (!mWifiManager.isWifiEnabled() && mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
                    mWifiManager.setWifiEnabled(true);
                }
            }
        });
        final Button disable = (Button) findViewById(R.id.button_disable);
        disable.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View view) {
                mWifiManager.setWifiEnabled(false);
            }
        });
        
        mConnect = (Button) findViewById(R.id.button_connect);
        mConnect.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View view) {
                List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
                if (configs.size() > 0) {
                    int id = configs.get(0).networkId;
                    mWifiManager.enableNetwork(id, true);
                }
            }
        });

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // First of first, we should check the connectivity of the Wifi networks
        // If there is a network, we use it direclty
        // If not, we check the reason, not enabled? or no wifi networks available
        WifiInfo info = mWifiManager.getConnectionInfo();
        // Attention: #getConnectionInfo will never return null, even if there is no active connection
        // it returns a empty WifiInfo object when there is no active networks.
        // So better way is to check whether Wifi is enabled
        if (info != null && info.getBSSID() != null) {
            mInfoPanel.setText(info.toString());
        } else {
            // No wifi connection, there might be lots of reasons, check them out one by one
            mInfoPanel.setText("Error: no active Wifi connection");
            // First, check whether the Wifi is enabled on this device
            final int state = mWifiManager.getWifiState();
            if (!mWifiManager.isWifiEnabled()) {
                if (state != WifiManager.WIFI_STATE_ENABLING) {
                    // prompt to enable the wifi
                    new AlertDialog.Builder(WifiManagerActivity.this)
                        .setTitle("wifi_connection")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("enable wifi")
                        .setPositiveButton("enable", new DialogInterface.OnClickListener() {
                            
                            public void onClick(DialogInterface dialog, int which) {
                                if (mWifiManager.setWifiEnabled(true)) {
                                    if (mWifiManager.startScan()) {
                                        // show the progress indicating the scanning progress until we get scanning results
                                        showDialog(SCANNING_PROGRESS);
                                    }
                                }
                            }
                        })
                        .show();
                }
            } else {
                // also start scanning?
                if (mWifiManager.startScan()) {
                    // show the progress indicating the scanning progress until we get scanning results
                    showDialog(SCANNING_PROGRESS);
                }
            }
        }
    }

    private BroadcastReceiver mStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                Log.e(TAG, "WifiManager.onreceive, got empty intent");
                return;
            }
            final String action = intent.getAction();
            if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                final int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                final int prevState = intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                // update wifi state
                if (state == WifiManager.WIFI_STATE_ENABLED && prevState == WifiManager.WIFI_STATE_ENABLING) {
                    if (mWifiManager.startScan()) {
                        showDialog(SCANNING_PROGRESS);
                    }
                }
            } else if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
            } else if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            } else if (action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
                // update wifi signal stregth
            } else if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                // retrieve the scan results and try to connect 
                // Send a message to process it in message queue
                dismissDialog(SCANNING_PROGRESS);
                mHandler.sendEmptyMessage(GET_SCAN_RESULT);
            }
        }
    };

    private void registerBroadcastReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mStateReceiver, filter);
    }

    private void unregisterBroadcastReceiver() {
        unregisterReceiver(mStateReceiver);
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

    @Override
    public Dialog onCreateDialog(int which) {
        ProgressDialog dialog = null;
        switch (which) {
        case SCANNING_PROGRESS:
            dialog = new ProgressDialog(WifiManagerActivity.this);
            dialog.setTitle("wifi_connection");
            dialog.setIndeterminate(true);
            dialog.setMessage("wifi_scanning");
            dialog.setCancelable(true);
            break;
        default:
            break;
        }
        return dialog;
    }
    
    private void updateHotspotList() {
        final List<ScanResult> results = mWifiManager.getScanResults();
        mInfoPanel.setText("scan_result" + results.size());
        ListView list = (ListView) findViewById(R.id.hotspot_list);
        list.setAdapter(new SimpleAdapter(getApplication(), results));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScanResult item = results.get(position-1);
                WifiConfiguration config = new WifiConfiguration();
                config.BSSID = item.BSSID;
                config.SSID = item.SSID;
                mWifiManager.addNetwork(config);
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ScanResult hotspot = results.get(position-1); // there is a header view
                StringBuilder sb = new StringBuilder();
                sb.append("BSSID: " + hotspot.BSSID);
                sb.append("\nSSID: " + hotspot.SSID);
                sb.append("\nCapability: " + hotspot.capabilities);
                sb.append("\nFrequency: " + hotspot.frequency);
                sb.append("\nLevel: " + hotspot.level);
                new AlertDialog.Builder(WifiManagerActivity.this)
                    .setTitle("Hotspot details")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(sb.toString())
                    .setPositiveButton("Okay", null)
                    .show();
                return false;
            }
        });

        final List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        ListView configList = (ListView) findViewById(R.id.config_list);
        configList.setAdapter(new SimpleConfigAdapter(getApplication(), configs));
        configList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mWifiManager.addNetwork(configs.get(position-1));
            }
        });
        configList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                WifiConfiguration config = configs.get(position-1); // Because there is a header view
                StringBuilder sb = new StringBuilder();
                sb.append("BSSID: " + config.BSSID);
                sb.append("\nSSID: " + config.SSID);
                sb.append("\nNetworkId: " + config.networkId);
                sb.append("\nStatus: " + config.status);
                sb.append("\nPriority: " + config.priority);
                new AlertDialog.Builder(WifiManagerActivity.this)
                    .setTitle("Configuration details")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(sb.toString())
                    .setPositiveButton("Okay", null)
                    .show();
                return false;
            }
        });
    }

    private class SimpleAdapter extends BaseAdapter {
        private Context mContext;
        private List<ScanResult> mResults;
        private int mItemCount;

        // prevent undesired instantiating
        private SimpleAdapter() {
        }

        public SimpleAdapter(Context context, List<ScanResult> result) {
            mContext = context;
            mResults = result;
            mItemCount = mResults.size();
        }

        
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position < 0 || position >= mItemCount) {
                Log.e(TAG, "getView, invalid index");
                return null;
            }
            if (convertView == null) {
                LayoutInflater factory = LayoutInflater.from(mContext);
                convertView = factory.inflate(R.layout.wifi_list_item, null);
            }
            TextView info = (TextView) convertView.findViewById(R.id.hotspot_info);
            info.setText(mResults.get(position).toString());
            return convertView;
        }

        
        public int getCount() {
            return mItemCount;
        }

        
        public Object getItem(int position) {
            return null;
        }

        
        public long getItemId(int position) {
            return (long) position;
        }
    }

    private class SimpleConfigAdapter extends BaseAdapter {
        private Context mContext;
        private List<WifiConfiguration> mResults;
        private int mItemCount;

        // prevent undesired instantiating
        private SimpleConfigAdapter() {
        }

        public SimpleConfigAdapter(Context context, List<WifiConfiguration> result) {
            mContext = context;
            mResults = result;
            mItemCount = mResults.size();
        }

        
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position < 0 || position >= mItemCount) {
                Log.e(TAG, "getView, invalid index");
                return null;
            }
            if (convertView == null) {
                LayoutInflater factory = LayoutInflater.from(mContext);
                convertView = factory.inflate(R.layout.wifi_list_item, null);
            }
            TextView info = (TextView) convertView.findViewById(R.id.hotspot_info);
            info.setText(mResults.get(position).toString());
            return convertView;
        }

        
        public int getCount() {
            return mItemCount;
        }

        
        public Object getItem(int position) {
            return null;
        }

        
        public long getItemId(int position) {
            return (long) position;
        }
    }
}