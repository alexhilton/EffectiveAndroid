package com.hilton.effectiveandroid;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HelloWorldActivity extends Activity {
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);
        layout.setLayoutParams(params);
        TextView text = new TextView(this);
        text.setText("Hello, welcome to Effective Android");
        layout.addView(text);
        setContentView(layout);
    }
}
