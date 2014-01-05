package com.hilton.effectiveandroid.app;

import com.hilton.effectiveandroid.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
public class ResourceTestTwo extends Activity {
    @Override
    public void onCreate(Bundle icicle) {
	super.onCreate(icicle);
	setContentView(R.layout.resource_test_two);
	final TextView text = (TextView) findViewById(R.id.resource_text_view);
	text.setText("With great power comes great responsibility");
    }
}