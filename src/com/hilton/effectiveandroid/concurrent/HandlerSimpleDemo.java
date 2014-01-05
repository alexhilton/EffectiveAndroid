package com.hilton.effectiveandroid.concurrent;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.hilton.effectiveandroid.R;

public class HandlerSimpleDemo extends Activity {
    protected static final String TAG = "HandlerSimpleDemo";
    private static final int MEDIA_PLAYER_READY = 0;
    private static final int REFRESH_PROGRESS = 1;
    
    private Button mStart;
    private Button mStop;
    private SurfaceHolder mSurfaceHolder;
    private ProgressBar mProgressBar;
    private SurfaceView mDisplay;
    private MediaPlayer mMediaPlayer;
    
    private Handler mMainHandler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
	    switch (msg.what) {
	    case MEDIA_PLAYER_READY:
		mProgressBar.setMax(mMediaPlayer.getDuration());
		mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
		    public void onCompletion(MediaPlayer mp) {
			mProgressBar.setProgress(mMediaPlayer.getDuration());
			mMainHandler.removeMessages(REFRESH_PROGRESS);
		    }
		});
		mStart.setEnabled(true);
		mStop.setEnabled(true);
		break;
	    case REFRESH_PROGRESS:
		int cp = mMediaPlayer.getCurrentPosition();
		mProgressBar.setProgress(cp);
		int delay = 1000 - (cp % 1000);
		mMainHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS, delay);
		break;
	    default:
		break;
	    }
	}
    };
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.handler_simple_demo);
	mStart = (Button) findViewById(R.id.handler_simple_start);
	mStart.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		mMediaPlayer.start();
		mMainHandler.sendEmptyMessage(REFRESH_PROGRESS);
	    }
	});
	mStart.setEnabled(false);
	mStop = (Button) findViewById(R.id.handler_simple_stop);
	mStop.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		mMainHandler.removeMessages(REFRESH_PROGRESS);
		mMediaPlayer.pause();
	    }
	});
	mStop.setEnabled(false);
	mProgressBar = (ProgressBar) findViewById(R.id.handler_simple_progress);
	mDisplay = (SurfaceView) findViewById(R.id.handler_simple_display);
	mSurfaceHolder = mDisplay.getHolder();
	mSurfaceHolder.setFixedSize(mDisplay.getWidth(), mDisplay.getHeight());
	// Do not believe the document, setType is necessary, otherwise, video won't play correctly
	mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	
	new Thread(new Runnable() {
	    public void run() {
		try {
		    mMediaPlayer = MediaPlayer.create(getApplication(), R.raw.flug);
		    mMediaPlayer.setDisplay(mSurfaceHolder);
		    mMainHandler.sendEmptyMessage(MEDIA_PLAYER_READY);
		} catch (IllegalArgumentException e) {
		    Log.e(TAG, "caught exception e", e);
		} catch (SecurityException e) {
		    Log.e(TAG, "caught exception e", e);
		} catch (IllegalStateException e) {
		    Log.e(TAG, "caught exception e", e);
		}
	    }
	}).start();
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	mMainHandler.removeMessages(REFRESH_PROGRESS);
	if (mMediaPlayer != null) {
	    mMediaPlayer.release();
	}
    }
}