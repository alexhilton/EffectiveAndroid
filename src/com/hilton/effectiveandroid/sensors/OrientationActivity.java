package com.hilton.effectiveandroid.sensors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hilton.effectiveandroid.R;

public class OrientationActivity extends Activity {
    private static final String TAG = "OrientationActivity";
    private TextView mInfoPanel;
    private TextView mOrientationPanel;
    private Button mControlButton;
    private OrientationHandler mOrientationHandler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mInfoPanel = (TextView) findViewById(R.id.info_panel);
        mInfoPanel.setText("This is another way to action down and up with more OO");
        mOrientationPanel = (TextView) findViewById(R.id.orientation_panel);
        mOrientationHandler = new OrientationHandler(getApplication());
        mOrientationHandler.setOrientationListener(mOrientationListener);
        mControlButton = (Button) findViewById(R.id.control_button);
        mControlButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    mOrientationHandler.deactivate();
                    break;
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    mOrientationHandler.activate();
                    break;
                default:
                    break;
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mOrientationHandler.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mOrientationHandler.onPause();
    }

    private OrientationListener mOrientationListener = new OrientationListener() {
        public void onOrientationChanged(float azimuth, float pitch, float roll) {
            StringBuilder sb = new StringBuilder();
            sb.append("\nAzimuth: " + azimuth);
            sb.append("\nPitch: " + pitch);
            sb.append("\nRoll: " + roll);
            mOrientationPanel.setText(sb.toString());
        }

        public void onActionUp() {
            // to control the media player
            final Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            intent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
            sendBroadcast(intent);
            Toast.makeText(getApplication(), "Action up", Toast.LENGTH_SHORT).show();
        }

        public void onActionDown() {
            // to control the media player
            final Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            intent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent.KEYCODE_MEDIA_NEXT);
            sendBroadcast(intent);
            Toast.makeText(getApplication(), "Action down", Toast.LENGTH_SHORT).show();
        }
    };
}