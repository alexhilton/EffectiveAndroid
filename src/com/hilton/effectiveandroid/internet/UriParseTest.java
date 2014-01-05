package com.hilton.effectiveandroid.internet;

import com.hilton.effectiveandroid.R;
import com.hilton.effectiveandroid.R.id;
import com.hilton.effectiveandroid.R.layout;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class UriParseTest extends Activity {
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.uri_parse_test);
        TextView text = (TextView) findViewById(R.id.uri_parse_display);
        String hint = "file:///mnt/sdcard/alex%hi%ton/ApkInstaller_v2.2.apk";
        String path;
        final String SCHEME_FILE = "file://";
        if (hint.startsWith(SCHEME_FILE)) {
            path = hint.substring(SCHEME_FILE.length());
        } else {
            path = Uri.parse(hint).getPath();
        }
        text.setText(path);
    }
}