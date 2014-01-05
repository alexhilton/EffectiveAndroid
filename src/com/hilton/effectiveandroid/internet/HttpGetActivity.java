package com.hilton.effectiveandroid.internet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.hilton.effectiveandroid.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HttpGetActivity extends Activity {
    protected static final String TAG = "HttpGetActivity";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.httpget_activity);
        final TextView label = (TextView) findViewById(R.id.label);
        Button post = (Button) findViewById(R.id.post);
        post.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String url = "http://www.dubblogs.cc:8751/Android/Test/API/Post/index.php";
                HttpPost httpRequest = new HttpPost(url);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("str", "I am post string"));
                try {
                    httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    HttpResponse response = new DefaultHttpClient().execute(httpRequest);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        String result = EntityUtils.toString(response.getEntity());
                        label.setText(result);
                    } else {
                        label.setText("Error response: " + 
                                String.valueOf(response.getStatusLine().getStatusCode()));
                    }
                } catch (ClientProtocolException e) {
                    label.setText(e.getMessage());
                } catch (Exception e) {
                    label.setText(e.getMessage());
                }
            }
        });
        
        Button get = (Button) findViewById(R.id.get);
        get.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String url = 
                    "http://www.dubblogs.cc:8751/Android/Test/API/Get/index.php?str=I+am+get+string";
                HttpGet getRequest = new HttpGet(url);
                try {
                    HttpResponse response = new DefaultHttpClient().execute(getRequest);
                    final int rspCode = response.getStatusLine().getStatusCode();
                    if (rspCode == 200) {
                        String result = EntityUtils.toString(response.getEntity());
//                        result = regReplace(result, "(\r\n|\r|\n|\n\r)", "");
                        label.setText(result);
                    } else {
                        label.setText("Error response: " + String.valueOf(rspCode));
                    }
                } catch (ClientProtocolException e) {
                    label.setText(e.getMessage());
                } catch (Exception e) {
                    label.setText(e.getMessage());
                }
            }
        });
        
        final EditText inputUrl = (EditText) findViewById(R.id.input_url);
        final TextView responseLabel = (TextView) findViewById(R.id.response_label);
        Button connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String url = inputUrl.getText().toString();
                final HttpGet request = new HttpGet(url);
                try {
                    HttpResponse response = new DefaultHttpClient().execute(request);
                    final int rspCode = response.getStatusLine().getStatusCode();
                    if (rspCode == 200) {
                        String result = EntityUtils.toString(response.getEntity());
                        responseLabel.setText(result);
                    } else {
                        responseLabel.setText("error response: " + String.valueOf(rspCode));
                    }
                } catch (Exception ex) {
//                    responseLabel.setText(e.getMessage());
                    Log.e(TAG, "excepiton caught ", ex);
                }
            }
        });
    }
    
    private String regReplace(String string, String from, String to) {
        Pattern pattern = Pattern.compile("(?i)" + from);
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            return string.replace(from, to);
        } else {
            return string;
        }
    }
}