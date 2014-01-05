package com.hilton.effectiveandroid.concurrent;

import java.util.ArrayList;
import java.util.Random;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * How to attach an Handler to a Thread:
 * If you specify Looper object to Handler, i.e. new Handler(Looper), then the handler is attached to the thread owning
 * the Looper object, in which handleMessage() is executed.
 * If you do not specify the Looper object, then the handler is attached to the thread calling new Handler(), in which
 * handleMessage() is executed.
 * In this example, for class CookServer or DeliverServer, if you write this way:
 *     private class CookServer extends Thread {
		private Handler mHandler;
		private Looper mLooper;
	
		public CookServer() {
			mHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					....
				}
	    		start();
		}
 * then mHandler is attached to thread calling new CookServer(), which is the main thread, so mHandler.handleMessage() will
 * be executed in main thread.
 * To attach mHandler to its own thread, you must put it in run(), or after mLooper is created. For our example, providing
 * mLooper or not won't matter, because new Handler() is called in run(), which is in a new thread.
 */
public class HandlerITCDemo extends ListActivity {
    private static final int COOKING_STARTED = 1;
    private static final int COOKING_DONE = 2;
    private static final int DELIVERING_STARTED = 3;
    private static final int ORDER_DONE = 4;
    
    private ListView mListView;
    private static final String[] mFoods = new String[] {
	"Cubake",
	"Donut",
	"Eclaire",
	"Gingerbread",
	"Honeycomb",
	"Ice Cream Sanwitch",
	"Jelly Bean",
    };
    private ArrayList<String> mOrderList;
    private TextView mGeneralStatus;
    private Button mSubmitOrder;
    private static Random mRandomer = new Random(47);
    private int mOrderCount;
    private int mCookingCount;
    private int mDeliveringCount;
    private int mDoneCount;
    
    private Handler mMainHandler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
	    switch (msg.what) {
	    case COOKING_STARTED:
		mCookingCount++;
		break;
	    case COOKING_DONE:
		mCookingCount--;
		break;
	    case DELIVERING_STARTED:
		mDeliveringCount++;
		break;
	    case ORDER_DONE:
		mDeliveringCount--;
		mDoneCount++;
	    default:
		break;
	    }
	    mGeneralStatus.setText(makeStatusLabel());
	}
    };
    
    private CookServer mCookServer;
    private DeliverServer mDeliverServer;
    
    @Override
    protected void onDestroy() {
	super.onDestroy();
	if (mCookServer != null) {
	    mCookServer.exit();
	    mCookServer = null;
	}
	if (mDeliverServer != null) {
	    mDeliverServer.exit();
	    mDeliverServer = null;
	}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	mListView = getListView();
	mOrderList = new ArrayList<String>();
	mGeneralStatus = new TextView(getApplication());
	mGeneralStatus.setText(makeStatusLabel());
	mSubmitOrder = new Button(getApplication());
	mSubmitOrder.setText("Submit order");
	mSubmitOrder.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		String order = mFoods[mRandomer.nextInt(mFoods.length)];
		mOrderList.add(order);
		mOrderCount = mOrderList.size();
		mGeneralStatus.setText(makeStatusLabel());
		setAdapter();
		mCookServer.cook(order);
	    }
	});
	mListView.addHeaderView(mGeneralStatus);
	mListView.addFooterView(mSubmitOrder);
	setAdapter();
	mCookServer = new CookServer();
	mDeliverServer = new DeliverServer("deliver server");
    }
    
    private String makeStatusLabel() {
	StringBuilder sb = new StringBuilder();
	sb.append("Total: ");
	sb.append(mOrderCount);
	sb.append("    Cooking: ");
	sb.append(mCookingCount);
	sb.append("    Delivering: ");
	sb.append(mDeliveringCount);
	sb.append("    Done: ");
	sb.append(mDoneCount);
	return sb.toString();
    }
    
    private void setAdapter() {
	final ListAdapter adapter = new ArrayAdapter<String>(getApplication(), android.R.layout.simple_list_item_1, mOrderList);
	setListAdapter(adapter);
    }
    
    private class CookServer extends Thread {
	private Handler mHandler;
	private Looper mLooper;
	
	public CookServer() {
	    start();
	}
	
	@Override
	public void run() {
	    Looper.prepare();
	    mLooper = Looper.myLooper();
	    mHandler = new Handler(mLooper, new Handler.Callback() {
		public boolean handleMessage(Message msg) {
		    new Cooker((String) msg.obj);
		    return true;
		}
	    });
	    Looper.loop();
	}
	
	public void cook(String order) {
	    if (mLooper == null || mHandler == null) {
		return;
	    }
	    Message msg = Message.obtain();
	    msg.obj = order;
	    mHandler.sendMessage(msg);
	}
	
	public void exit() {
	    if (mLooper != null) {
		mLooper.quit();
		mHandler = null;
		mLooper = null;
	    }
	}
    }
    
    private class Cooker extends Thread {
	private String order;
	public Cooker(String order) {
	    this.order = order;
	    start();
	}
	
	@Override
	public void run() {
            mMainHandler.sendEmptyMessage(COOKING_STARTED);
            SystemClock.sleep(mRandomer.nextInt(50000));
            mDeliverServer.deliver(order);
            mMainHandler.sendEmptyMessage(COOKING_DONE);
	}
    }
    
    private class DeliverServer extends HandlerThread {
	private Handler mHandler;
	
	public DeliverServer(String name) {
	    super(name);
	    start();
	}
	
	@Override
	protected void onLooperPrepared() {
	    super.onLooperPrepared();
	    mHandler = new Handler(getLooper(), new Handler.Callback() {
		public boolean handleMessage(Message msg) {
		    new Deliver((String) msg.obj);
		    return true;
		}
	    });
	}

	public void deliver(String order) {
	    if (mHandler == null || getLooper() == null) {
		return;
	    }
	    Message msg = Message.obtain();
	    msg.obj = order;
	    mHandler.sendMessage(msg);
	}
	
	public void exit() {
	    quit();
	    mHandler = null;
	}
    }
    
    private class Deliver extends Thread {
	private String order;
	public Deliver(String order) {
	    this.order = order;
	    start();
	}
	
	@Override
	public void run() {
	    mMainHandler.sendEmptyMessage(DELIVERING_STARTED);
	    SystemClock.sleep(mRandomer.nextInt(50000));
	    mMainHandler.sendEmptyMessage(ORDER_DONE);
	}
    }
}