package com.hilton.effectiveandroid;

import com.hilton.effectiveandroid.R;
import com.hilton.effectiveandroid.R.id;
import com.hilton.effectiveandroid.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class HelloWorld extends Activity {
    private static final String TAG = "HelloWorld";

    private static final int REQUEST_FOCUS = 100;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // show a dialog with special flag#system_dialog
//        final AlertDialog dialog = new AlertDialog.Builder(HelloWorld.this)
//        .setTitle("Android")
//        .setIcon(android.R.drawable.ic_dialog_alert)
//        .setNegativeButton("Cancel", null)
//        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface arg0, int arg1) {
//				return;
//			}
//		})
//        .create();
//        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
//        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        dialog.show();
//        View view = this.getWindow().getDecorView();
//
//        ImageView image1 = (ImageView) findViewById(R.id.image1);
//        ImageView image2 = (ImageView) findViewById(R.id.image2);
//        
//        Resources r = getResources();
//        final String pkgName = getPackageName();
//        final int icon1_id = r.getIdentifier("icon", "drawable", pkgName);
////        image1.setImageResource(icon1_id);
//        
//        final int icon2_id = r.getIdentifier("icon2", "drawable", pkgName);
//        image2.setImageResource(icon2_id);
//        
//        Bitmap b = BitmapFactory.decodeResource(r, icon1_id);
//        image1.setImageBitmap(b);
        
//        View view = (View) findViewById(R.id.layout);
//        if (!view.isDrawingCacheEnabled()) {
//        	view.setDrawingCacheEnabled(true);
//        }
//        Bitmap screenshot = view.getDrawingCache();
//        ImageView i = (ImageView) findViewById(R.id.image3);
//        i.setImageBitmap(screenshot);
//        
        Button getScreenshot = (Button) findViewById(R.id.get_screenshot);
        getScreenshot.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
		        View view = (View) findViewById(R.id.layout);
		        view.clearFocus();
		        if (!view.isDrawingCacheEnabled()) {
		        	view.setDrawingCacheEnabled(true);
		        }
		        Bitmap screenshot = view.getDrawingCache(true);
		        ImageView i = (ImageView) findViewById(R.id.image3);
		        i.setImageBitmap(screenshot);
			}
        });
        
        Button getScreenshot2 = (Button) findViewById(R.id.get_screenshot2);
        getScreenshot2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
		        View view = HelloWorld.this.getWindow().getDecorView();
		        view.clearFocus();
		        if (!view.isDrawingCacheEnabled()) {
		        	view.setDrawingCacheEnabled(true);
		        }
		        Bitmap screenshot = view.getDrawingCache();
		        ImageView i = (ImageView) findViewById(R.id.image3);
		        i.setImageBitmap(screenshot);
		        startActivity(new Intent("android.intent.action.GRID_VIEW_ACTIVITY"));
			}
        });
        
        Button getVideo = (Button) findViewById(R.id.get_videos);
        getVideo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent(Intent.ACTION_GET_CONTENT); 
				i.setType("video/*"); 
				startActivityForResult(i,1); 
			}
		});
        
        Button getImage = (Button) findViewById(R.id.get_images);
        getImage.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent(Intent.ACTION_GET_CONTENT); 
				i.setType("image/*"); 
				startActivityForResult(i, 2); 
			}
		});
    }
    
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
    	switch (reqCode) {
    	case 2:
    		if (resCode == RESULT_OK) {
    			Log.e(TAG, "we are here");
    			Log.e(TAG, "data " + data);
    			Bitmap image = (Bitmap) data.getParcelableExtra("data");
 		        ImageView i = (ImageView) findViewById(R.id.image3);
 		        i.setImageBitmap(image);
    		} else {
    			Log.e(TAG, "result is not okay");
    		}
    	}
    }
    
    private Handler mHandler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
    		case REQUEST_FOCUS:
    			Button getVideo = (Button) findViewById(R.id.get_videos);
    			getVideo.requestFocus();
    		}
    	}
    };
    
    @Override
    public void onConfigurationChanged(Configuration config) {
    	super.onConfigurationChanged(config);
    }
}