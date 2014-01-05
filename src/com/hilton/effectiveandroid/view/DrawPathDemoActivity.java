package com.hilton.effectiveandroid.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.Path.Direction;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class DrawPathDemoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawPathView(getApplication()));
    }
    
    private class DrawPathView extends View {
        Point mCenter ;
        
        public DrawPathView(Context context) {
            super(context);
        }
        
        @Override
        public void onDraw(Canvas canvas) {
            if (mCenter == null) {
                mCenter = new Point(getWidth()/2, getHeight()/2);
            }
            canvas.drawColor(Color.CYAN);
            float left = mCenter.x;
            float top = mCenter.y;
            float radius = 200;
            Path p = new Path();
            p.addRect(left, top, left+radius, top+radius, Direction.CW);
            p.addCircle(mCenter.x, mCenter.y, radius, Direction.CW);
            p.lineTo(mCenter.x + 2*radius, mCenter.y+2*radius);
            p.close();
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setAntiAlias(true);
//            paint.setStyle(Style.FILL_AND_STROKE);
            canvas.drawPath(p, paint);
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent mv) {
            switch (mv.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCenter.x = (int) mv.getX();
                mCenter.y = (int) mv.getY();
                invalidate();
                return true;
            }
            return super.onTouchEvent(mv);
        }
    }
}