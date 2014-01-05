package com.hilton.effectiveandroid.animation;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hilton.effectiveandroid.R;

public class LauncherActivity extends Activity {
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.animation_common_layout);
        final LinearLayout container = (LinearLayout) findViewById(R.id.animation_container);
        final ImageView target = (ImageView) findViewById(R.id.target);
        
        // With animator
//        ValueAnimator anim = ObjectAnimator.ofInt(target, "backgroundColor", Color.RED, Color.BLUE);
//        anim.setDuration(2000);
//        anim.setEvaluator(new ArgbEvaluator());
//        anim.setRepeatCount(ValueAnimator.INFINITE);
//        anim.setRepeatMode(ValueAnimator.REVERSE);
//        anim.start();
        
        // with animation
        target.setBackgroundColor(Color.RED);
        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        alpha.setFillBefore(true);
        alpha.setDuration(4000);
        alpha.setRepeatCount(Animation.INFINITE);
        alpha.setRepeatMode(Animation.REVERSE);
        target.setAnimation(alpha);
        alpha.start();
    }
}
