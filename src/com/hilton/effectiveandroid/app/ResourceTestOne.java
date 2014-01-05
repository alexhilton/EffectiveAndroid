package com.hilton.effectiveandroid.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hilton.effectiveandroid.R;

public class ResourceTestOne extends Activity {
    @Override
    public void onCreate(Bundle icicle) {
	super.onCreate(icicle);
	setContentView(R.layout.resource_test_one);
	final TextView text = (TextView) findViewById(R.id.resource_text_view);
	text.setText("I pledge allegiance to the flag of united states of america and to the republic " +
			"for which it stands one nation under god indivisible with liberty and justice for all");
	text.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		startActivity(new Intent(getApplication(), ResourceTestTwo.class));
	    }
	});
    }
}