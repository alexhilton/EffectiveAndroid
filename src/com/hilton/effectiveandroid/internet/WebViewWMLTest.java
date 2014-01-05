package com.hilton.effectiveandroid.internet;

import com.hilton.effectiveandroid.R;
import com.hilton.effectiveandroid.R.id;
import com.hilton.effectiveandroid.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class WebViewWMLTest extends Activity {
    protected static final String TAG = null;
    private ProgressBar mProgressBar;
    private WebView mWebview;
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.webview_wml_test);
        mWebview = (WebView) findViewById(R.id.view_article_webview);
        mProgressBar = (ProgressBar) findViewById(R.id.view_article_progressbar);
        // config the webview
        mWebview.setWebChromeClient(new WebChromeClient() {
           public void onProgressChanged(WebView view, int progress) {
               mProgressBar.setProgress(progress);
               if (progress == 100) {
                   mProgressBar.setVisibility(View.GONE);
               }
           }
        });
        mWebview.setWebViewClient(new WebViewClient() { 
           public void onReceivedError(WebView view, int errorCode, String description, String failureUrl) {
//               Toast.makeText(ViewArticleActivity.this, description + " on url " + failureUrl, Toast.LENGTH_LONG).show();
               Log.e(TAG, description + " on url " + failureUrl);
           }
        });
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.loadUrl("file:///sdcard/index.do");
    }
}
