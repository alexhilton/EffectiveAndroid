package com.hilton.effectiveandroid.app;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;

import com.hilton.effectiveandroid.R;

public class DialogDemo extends Activity {
    private static final int DISMISS_DIALOG = 1;
    
    private ProgressDialog mBetterDialog;

    private Handler mMainHandler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
	    switch (msg.what) {
	    case DISMISS_DIALOG:
		Dialog dialog = (Dialog) msg.obj;
		dialog.dismiss();
		break;
	    default:
		break;
	    }
	}
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.dialog_demo);
	
	final Button sucking = (Button) findViewById(R.id.sucking);
	sucking.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		final Activity activity = DialogDemo.this;
		final ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setTitle("Worst dialogging");
		dialog.setMessage("This is the worst dialogging scheme, NEVER use it. This dialog is easy to " +
			"run out of its attached activity, yielding WindowManager#BadTokenException if the activity is gone when dismissing");
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		// You MUST do the show in main thread anyway
		dialog.show();
		new Thread(new Runnable() {
		    public void run() {
			SystemClock.sleep(10000);
			/*
			 * IllegalArgumentException: View not attached to window manager
			 * If the activity showing the dialog was killed before dismiss() out of rotation or locale changed,
			 * the dialog will gone with activity, but when dismiss() yields "IllegalArgumentException: View not attached to 
			 * window manager".
			 * Checking isShowing() won't help.
			 * Checking activity.isFinishing() won't help, either.
			 * Dismiss it in main thread also won't give any help.
			 */
			// THIS WON't WORK
//			if (dialog.isShowing()) {
//			    dialog.dismiss();
//			}
//			if (!activity.isFinishing()) {
//			    dialog.dismiss();
//			}
			Message msg = Message.obtain();
			msg.what = DISMISS_DIALOG;
			msg.obj = dialog;
			mMainHandler.sendMessage(msg);
		    }
		}).start();
	    }
	});
	
	final Button better = (Button) findViewById(R.id.better);
	better.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		mBetterDialog = new ProgressDialog(DialogDemo.this);
		mBetterDialog.setTitle("Better dialogging");
		mBetterDialog.setMessage("This dialogging can be used. The dialog object is a field of its activity, so activity can" +
				" control it to make sure dialog only lives within activity lifecircle");
		mBetterDialog.setIndeterminate(true);
		mBetterDialog.setCancelable(true);
		// You MUST do the show in main thread anyway
		mBetterDialog.show();
		new Thread(new Runnable() {
		    public void run() {
			SystemClock.sleep(10000);
			/*
			 * This is much better, mBetterDialog is a field of its activity, so activity can take care of it in order
			 * to make sure dialog only live within activity's life circle, to avoid any unexpected exceptions.
			 */
			// THIS really works
    			if (mBetterDialog != null && mBetterDialog.isShowing()) {
    			    mBetterDialog.dismiss();
    			}
		    }
		}).start();
	    }
	});
	
	final Button optional = (Button) findViewById(R.id.optional);
	optional.setOnClickListener(new View.OnClickListener() {
	    @SuppressWarnings("deprecation")
	    public void onClick(View v) {
		showDialog(0);
		new Thread(new Runnable() {
		    public void run() {
			SystemClock.sleep(10000);
			/*
			 * This way works best for most of time, except if activity died before dismissing, exception must be
			 * thrown: "IllegalArgumentException: View not attached to window manager".
			 * Although activity takes care of its belonging dialog, there is no way to operate it manually any more.
			 * First you do not have reference to dialog object and second, any manual operation only interferences
			 * and breaks state maintained by frameworks.
			 */
			dismissDialog(0);
		    }
		}).start();
	    }
	});
    }

    @Override
    protected Dialog onCreateDialog(int id) {
	ProgressDialog d = new ProgressDialog(this);
	d.setTitle("Optional dialogging");
	d.setMessage("This dialogging scheme works best for most times, the dialogs are all taken care of by activitys and frameworks" +
			". Except for activity being killed during dialog showing");
	d.setIndeterminate(true);
	d.setCancelable(true);
	return d;
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	// Activity is dying, all its belonging dialogs should be dismissed, of course.
	if (mBetterDialog != null && mBetterDialog.isShowing()) {
	    mBetterDialog.dismiss();
	    mBetterDialog = null;
	}
	// For dialogs showed via showDialog(int), no way to stop it in onDestroy()
//	dismissDialog(0); // cause "IllegalArgumentException: no dialog with id 0 was ever shown via Activity#showDialog"
			    // This is because Activity has to manage its dialog during onPause() and onResume() to restore
	                  // dialogs' state. So if you manually dismiss it in onDestroy(), it will cause JE.
	
//	removeDialog(0);// cause "IllegalArgumentException: no dialog with id 0 was ever shown via Activity#showDialog", when
			// dismissing in thread. 
	              // This is because Activity has to manage its dialog during onPause() and onResume() to restore
                     // dialogs' state. So if you manually dismiss it in onDestroy(), it will cause JE.
    }
}