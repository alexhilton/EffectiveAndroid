package com.hilton.effectiveandroid.os;

import java.net.URI;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/*
 * on 4.0 or up This activity works well. That's because AsyncTask's InternalHandler is initialized
 * when ActivityThread bind application in which is Main Thread. Here is the stacktrace:
10-30 06:47:01.799: W/System.err(1181): java.lang.Throwable: stack dump
10-30 06:47:01.799: W/System.err(1181):     at java.lang.Thread.dumpStack(Thread.java:496)
10-30 06:47:01.799: W/System.err(1181):     at android.os.AsyncTask$InternalHandler.<init>(AsyncTask.java:609)
10-30 06:47:01.800: W/System.err(1181):     at android.os.AsyncTask.<clinit>(AsyncTask.java:190)
10-30 06:47:01.800: W/System.err(1181):     at android.app.ActivityThread.handleBindApplication(ActivityThread.java:3825)
10-30 06:47:01.800: W/System.err(1181):     at android.app.ActivityThread.access$1300(ActivityThread.java:129)
10-30 06:47:01.800: W/System.err(1181):     at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1223)
10-30 06:47:01.800: W/System.err(1181):     at android.os.Handler.dispatchMessage(Handler.java:99)
10-30 06:47:01.800: W/System.err(1181):     at android.os.Looper.loop(Looper.java:137)
10-30 06:47:01.800: W/System.err(1181):     at android.app.ActivityThread.main(ActivityThread.java:4518)
10-30 06:47:01.800: W/System.err(1181):     at java.lang.reflect.Method.invokeNative(Native Method)
10-30 06:47:01.800: W/System.err(1181):     at java.lang.reflect.Method.invoke(Method.java:511)
10-30 06:47:01.800: W/System.err(1181):     at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:809)
10-30 06:47:01.800: W/System.err(1181):     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:576)
10-30 06:47:01.800: W/System.err(1181):     at dalvik.system.NativeStart.main(Native Method)
 * But for 2.3 or lower, it crashes because of AsyncTask.onProgressUpdate cannot touch TextView.
 * This is because AsyncTask's InternalHandler is initialized when we new SimpleAsyncTask in spawn thread, so
 * AsyncTask's InternalHandler is run within the spawn thread, as a result it cannot touch UI. Here is the stacktrace:
01-01 04:49:50.605: W/System.err(1079): java.lang.Throwable: stack dump
01-01 04:49:50.606: W/System.err(1079):     at java.lang.Thread.dumpStack(Thread.java:608)
01-01 04:49:50.606: W/System.err(1079):     at android.os.AsyncTask$InternalHandler.<init>(AsyncTask.java:454)
01-01 04:49:50.606: W/System.err(1079):     at android.os.AsyncTask.<clinit>(AsyncTask.java:183)
01-01 04:49:50.606: W/System.err(1079):     at com.hilton.effectiveandroid.os.AsyncTaskTrapActivity$1.run(AsyncTaskTrapActivity.java:31)
01-01 04:49:50.606: W/System.err(1079):     at java.lang.Thread.run(Thread.java:1050)
 */
public class AsyncTaskTrapActivity extends Activity {
    private SimpleAsyncTask asynctask;
    private Looper myLooper;
    private TextView status;
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
//        URI.create("http://d1.sina.com.cn/d1images/pb/pbv4.html?http://sina.allyes.com/main/adfclick?db=sina&bid=343571,402779,408093&cid=0,0,0&sid=405374&advid=3406&camid=65333&show=ignore&url=http://sxpp.sina.com.cn${}jpg${}http://d3.sina.com.cn/201112/22/384545_750450-YANGFAN.jpg");
        asynctask = null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                myLooper = Looper.myLooper();
                status = new TextView(getApplication());
                asynctask = new SimpleAsyncTask(status);
                Looper.loop();
            }
        }).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        setContentView((TextView) status, params);
        asynctask.execute();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        myLooper.quit();
    }
    
    private class SimpleAsyncTask extends AsyncTask<Void, Integer, Void> {
        private TextView mStatusPanel;
        
        public SimpleAsyncTask(TextView text) {
            mStatusPanel = text;
        }
        
        @Override
        protected Void doInBackground(Void... params) {
            int prog = 1;
            while (prog < 101) {
                SystemClock.sleep(1000);
                publishProgress(prog);
                prog++;
            }
            return null;
        }
        
        // Not Okay, will crash, said it cannot touch TextView
        @Override
        protected void onPostExecute(Void result) {
            mStatusPanel.setText("Welcome back.");
        }
        
        // Okay, because it is called in #execute() which is called in Main thread, so it runs in Main Thread.
        @Override
        protected void onPreExecute() {
            mStatusPanel.setText("Before we go, let me tell you something buried in my heart for years...");
        }
        
        // Not okay, will crash, said it cannot touch TextView
        @Override
        protected void onProgressUpdate(Integer... values) {
            mStatusPanel.setText("On our way..." + values[0].toString());
        }
    }
}