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
import android.util.Log;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import java.util.Date;

public class OrientationHandler implements SensorEventListener {
    private static final String TAG = "OrientationHandler";
    private static final float START_ANGLE = -90.0f;
    private static final float END_ANGLE = 0.0f;
    private static final float EPSILON = 30.0f;
    private static final float INVALID_ANGLE = -9999999.0f;
    private static final int IDLE_DELAY = 10;

    // Message Queue handler message
    private static final int HANDLER_RESET_ACTION = 1001;

    private Context mContext;
    private SensorManager mSensorManager;
    private Sensor mOrientationSensor;
//    private Sensor mAccelerometerSensor;
//    private Sensor mMagneticSensor;
    private boolean mActionStarted;
    private float mFromAngle;
    private float[] mAccelerometerValues;
    private float[] mMagneticValues;
    private boolean mActionActivated;
    
    private OrientationListener mOrientationListener;

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

    public void setOrientationListener(OrientationListener listener) {
        mOrientationListener = listener;
    }

    // Prevent undesired instantiation
    private OrientationHandler() {
    }

    public OrientationHandler(Context context) {
        mContext = context;
        mActionStarted = false;
        mActionActivated = false;
        mFromAngle = INVALID_ANGLE;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void onResume() {
        registerListeners();
    }

    public void onPause() {
        unregisterListeners();
    }
    
    public void activate() {
        mActionActivated = true;
    }
    
    public void deactivate() {
        mActionActivated = false;
    }
    
    private void registerListeners() {
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
//        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        if (mOrientationSensor != null) {
            mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_UI);
//        } else {
//            mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
//            mSensorManager.registerListener(this, mMagneticSensor, SensorManager.SENSOR_DELAY_FASTEST);
//        }
    }

    private void unregisterListeners() {
        if (mOrientationSensor != null) {
            mSensorManager.unregisterListener(this, mOrientationSensor);
        }
//        if (mAccelerometerSensor != null) {
//            mSensorManager.unregisterListener(this, mAccelerometerSensor);
//        }
//        if (mMagneticSensor != null) {
//            mSensorManager.unregisterListener(this, mMagneticSensor);
//        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        float azimuth = 0;
        float pitch = 0;
        float roll = 0;

//        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
//            mAccelerometerValues = event.values;
//        }
//        if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
//            mMagneticValues = event.values;
//        }
        if (sensorType == Sensor.TYPE_ORIENTATION) {
            azimuth = event.values[0];
            pitch = event.values[1];
            roll = event.values[2];
        }// else if (mAccelerometerValues != null && mMagneticValues != null) {
//            // Calculate orientation from accelerometer and magnetic
//            float[] values = new float[3];
//            float[] R = new float[9];
//            if (SensorManager.getRotationMatrix(R, null, mAccelerometerValues, mMagneticValues)) {
//                SensorManager.getOrientation(R, values);
//                // convert from radians to degress, because SensorManager#getOrientation() returns radians
//                azimuth = (float) Math.toDegrees(values[0]);
//                pitch = (float) Math.toDegrees(values[1]);
//                roll = (float) Math.toDegrees(values[2]);
//            }
//        }

        if (mOrientationListener != null) {
            mOrientationListener.onOrientationChanged(azimuth, pitch, roll);
        }
        if (mActionActivated) {
            if (mActionStarted) {
                if (mFromAngle == END_ANGLE && pitch >= (START_ANGLE-EPSILON) && pitch <= (START_ANGLE+EPSILON)) {
                    // Fire the action up event and close this action
                    if (mOrientationListener != null) {
                        mOrientationListener.onActionUp();
                    }
                    mFromAngle = INVALID_ANGLE;
                    mHandler.sendEmptyMessageDelayed(HANDLER_RESET_ACTION, IDLE_DELAY);
                }
                if (mFromAngle == START_ANGLE && pitch >= (END_ANGLE-EPSILON) && pitch <= (END_ANGLE+EPSILON)) {
                    // Fire the action down event and close this action
                    if (mOrientationListener != null) {
                        mOrientationListener.onActionDown();
                    }
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