package com.hilton.effectiveandroid.concurrent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.hilton.effectiveandroid.R;

/*
 * AsyncTask cannot be reused, i.e. if you have executed one AsyncTask, you must discard it, you cannot execute it again.
 * If you try to execute an executed AsyncTask, you will get "java.lang.IllegalStateException: Cannot execute task: the task is already running"
 * In this demo, if you click "get the image" button twice at any time, you will receive "IllegalStateException".
 * About cancellation:
 * You can call AsyncTask#cancel() at any time during AsyncTask executing, but the result is onPostExecute() is not called after
 * doInBackground() finishes, which means doInBackground() is not stopped. AsyncTask#isCancelled() returns true after cancel() getting
 * called, so if you want to really cancel the task, i.e. stop doInBackground(), you must check the return value of isCancelled() in
 * doInBackground, when there are loops in doInBackground in particular.
 * This is the same to Java threading, in which is no effective way to stop a running thread, only way to do is set a flag to thread, and check
 * the flag every time in Thread#run(), if flag is set, run() aborts.
 */
public class AsyncTaskDemoActivity extends Activity {
    private static final String ImageUrl = "http://i1.cqnews.net/sports/attachement/jpg/site82/2011-10-01/2960950278670008721.jpg";
    private ProgressBar mProgressBar;
    private ImageView mImageView;
    private Button mGetImage;
    private Button mAbort;
    
    @Override
    public void onCreate(Bundle icicle) {
	super.onCreate(icicle);
	setContentView(R.layout.async_task_demo_activity);
	mProgressBar = (ProgressBar) findViewById(R.id.async_task_progress);
	mImageView = (ImageView) findViewById(R.id.async_task_displayer);
	final ImageLoader loader = new ImageLoader();
	mGetImage = (Button) findViewById(R.id.async_task_get_image);
	mGetImage.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		loader.execute(ImageUrl);
	    }
	});
	mAbort = (Button) findViewById(R.id.asyc_task_abort);
	mAbort.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		loader.cancel(true);
	    }
	});
	mAbort.setEnabled(false);
    }
    
    private class ImageLoader extends AsyncTask<String, Integer, Bitmap> {
	private static final String TAG = "ImageLoader";

	@Override
	protected void onPreExecute() {
	    // Initialize progress and image
	    mGetImage.setEnabled(false);
	    mAbort.setEnabled(true);
	    mProgressBar.setVisibility(View.VISIBLE);
	    mProgressBar.setProgress(0);
	    mImageView.setImageResource(R.drawable.icon);
	}
	
	@Override
	protected Bitmap doInBackground(String... url) {
	    /*
	     * Fucking ridiculous thing happened here, to use any Internet connections, either via HttpURLConnection
	     * or HttpClient, you must declare INTERNET permission in AndroidManifest.xml. Otherwise you will get
	     * "UnknownHostException" when connecting or other tcp/ip/http exceptions rather than "SecurityException"
	     * which tells you need to declare INTERNET permission.
	     */
	    try {
		URL u;
		HttpURLConnection conn = null;
		InputStream in = null;
		OutputStream out = null;
		final String filename = "local_temp_image";
		try {
		    u = new URL(url[0]);
		    conn = (HttpURLConnection) u.openConnection();
		    conn.setDoInput(true);
		    conn.setDoOutput(false);
		    conn.setConnectTimeout(20 * 1000);
		    in = conn.getInputStream();
		    out = openFileOutput(filename, Context.MODE_PRIVATE);
		    byte[] buf = new byte[8196];
		    int seg = 0;
		    final long total = conn.getContentLength();
		    long current = 0;
		    /*
		     * Without checking isCancelled(), the loop continues until reading whole image done, i.e. the progress
		     * continues go up to 100. But onPostExecute() will not be called.
		     * By checking isCancelled(), we can stop immediately, i.e. progress stops immediately when cancel() is called.
		     */
		    while (!isCancelled() && (seg = in.read(buf)) != -1) {
			out.write(buf, 0, seg);
			current += seg;
			int progress = (int) ((float) current / (float) total * 100f);
			publishProgress(progress);
			SystemClock.sleep(1000);
		    }
		} finally {
		    if (conn != null) {
			conn.disconnect();
		    }
		    if (in != null) {
			in.close();
		    }
		    if (out != null) {
			out.close();
		    }
		}
		return BitmapFactory.decodeFile(getFileStreamPath(filename).getAbsolutePath());
	    } catch (MalformedURLException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    return null;
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
	    mProgressBar.setProgress(progress[0]);
	}
	
	@Override
	protected void onPostExecute(Bitmap image) {
	    if (image != null) {
		mImageView.setImageBitmap(image);
	    }
	    mProgressBar.setProgress(100);
	    mProgressBar.setVisibility(View.GONE);
	    mAbort.setEnabled(false);
	}
    }
}