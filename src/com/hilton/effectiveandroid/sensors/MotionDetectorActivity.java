package com.hilton.effectiveandroid.sensors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hilton.effectiveandroid.R;

public class MotionDetectorActivity extends Activity {
    private static final String TAG = "SensortestActivity";
    private static final float START_ANGLE = -90.0f;
    private static final float END_ANGLE = 0.0f;
    private static final float EPSILON = 30.0f;
    private static final float INVALID_ANGLE = -9999999.0f;
    private static final int IDLE_DELAY = 10;

    // Message Queue handler message
    private static final int HANDLER_RESET_ACTION = 1001;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SensorEventListener mSensorEventListener;
    private TextView mInfoPanel;
    private TextView mOrientationPanel;
    private Button mControlButton;
    private boolean mActionStarted;
    private float mFromAngle;
    private boolean mActionActivated;
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case HANDLER_RESET_ACTION:
                mActionStarted = false;
                break;
            default:
                break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mInfoPanel = (TextView) findViewById(R.id.info_panel);
        mInfoPanel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                startActivity(new Intent(MotionDetectorActivity.this, OrientationActivity.class));
            }
        });
        
        mOrientationPanel = (TextView) findViewById(R.id.orientation_panel);
        mControlButton = (Button) findViewById(R.id.control_button);
        mActionActivated = false;
        mControlButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    mActionActivated = false;
                    break;
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    mActionActivated = true;
                    break;
                default:
                    break;
                }
                return false;
            }
        });
        
        mActionStarted = false;
        mFromAngle = INVALID_ANGLE;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorEventListener = new MySensorEventListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_UI);
        mInfoPanel.setText("Listening sensor " + mSensor.getName() + " by " + mSensor.getVendor());
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorEventListener);
        mInfoPanel.setText("Stop listening sensor " + mSensor.getName() + " by " + mSensor.getVendor());
    }

    private class MySensorEventListener implements SensorEventListener {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            StringBuilder sb = new StringBuilder();
            sb.append("Sensor: " + event.sensor.getName() + " by " + event.sensor.getVendor());
            sb.append("\nAccuracy: " + event.accuracy);
//            sb.append("\nTimestamp: " + new Date(event.timestamp));
            sb.append("\nAzimuth value[0]: " + event.values[0]);
            sb.append("\nPitch value[1]: " + event.values[1]);
            sb.append("\nRoll value[2]: " + event.values[2]);
            sb.append("\nAction started: " + mActionStarted);
            sb.append("\nFrom angle: " + mFromAngle);
            mOrientationPanel.setText(sb.toString());

            final float pitch = event.values[1];
            if (mActionActivated) {
                if (mActionStarted) {
                    if (mFromAngle == END_ANGLE && pitch >= (START_ANGLE-EPSILON) && pitch <= (START_ANGLE+EPSILON)) {
                        // Fire the action up event and close this action
                        // to control the media player
                        final Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                        intent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                        sendBroadcast(intent);
                        Toast.makeText(MotionDetectorActivity.this, "Action up", Toast.LENGTH_SHORT).show();
                        mFromAngle = INVALID_ANGLE;
                        mHandler.sendEmptyMessageDelayed(HANDLER_RESET_ACTION, IDLE_DELAY);
                    }
                    if (mFromAngle == START_ANGLE && pitch >= (END_ANGLE-EPSILON) && pitch <= (END_ANGLE+EPSILON)) {
                        // Fire the action down event and close this action
                        // to control the media player
                        final Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                        intent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent.KEYCODE_MEDIA_NEXT);
                        sendBroadcast(intent);
                        Toast.makeText(MotionDetectorActivity.this, "Action down", Toast.LENGTH_SHORT).show();
                        mHandler.sendEmptyMessageDelayed(HANDLER_RESET_ACTION, IDLE_DELAY);
                        mFromAngle = INVALID_ANGLE;
                    }
                } else {
                    if (pitch >= (START_ANGLE-EPSILON) && pitch <= (START_ANGLE+EPSILON)) {
                        mActionStarted = true;
                        mFromAngle = START_ANGLE;
                    }
                    if (pitch >= (END_ANGLE-EPSILON) && pitch <= (END_ANGLE+EPSILON)) {
                        mActionStarted = true;
                        mFromAngle = END_ANGLE;
                    }
                }
            } else {
                // initialize all field members
                mActionStarted = false;
                mFromAngle = INVALID_ANGLE;
                mHandler.removeMessages(HANDLER_RESET_ACTION);
            }
        }
    }
}