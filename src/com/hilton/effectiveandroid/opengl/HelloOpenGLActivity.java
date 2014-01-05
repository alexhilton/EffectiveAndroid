package com.hilton.effectiveandroid.opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class HelloOpenGLActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView view = new GLSurfaceView(this);
        view.setRenderer(new HelloOpenGLRender());
        setContentView(view);
    }
    
    private class HelloOpenGLRender extends SimpleRenderer {
        float x = 0.8f;
        float[] rectVertice = new float[] {
                x, x, 0.0f,
                x, -x, 0.0f,
                -x, x, 0.0f,
                -x, -x, 0.0f
        };
        int y = 65535;
        int[] rectColor = new int[] {
                0, y, 0, 0,
                0, 0, y, 0,
                y, 0, 0, 0,
                y, y, 0, 0
        };
        FloatBuffer rectVerticesBuffer;
        IntBuffer rectColorBuffer;
        private float translateX;
        private float translateY;
        private float step = 0.02f;
        private float maxx = 1.0f;
        private float maxy = 1.0f;
        private float rotateStep = 10.f;
        private float rotate;
        public HelloOpenGLRender() {
            rectVerticesBuffer = FloatBuffer.wrap(rectVertice);
            rectColorBuffer = IntBuffer.wrap(rectColor);
            translateX = 0.0f;
            translateY = 0.0f;
            rotate = 0.0f;
        }
        
        @Override
        public void onDrawFrame(GL10 gl) {
            super.onDrawFrame(gl);
            gl.glTranslatef(translateX, translateY, -1.0f);
            gl.glRotatef(rotate, 0.0f, 0.0f, 1.0f);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, rectVerticesBuffer);
            gl.glColorPointer(4, GL10.GL_FIXED, 0, rectColorBuffer);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
            
            translateX += step;
            translateY += step;
            if (translateX > maxx) {
                translateX = -maxx;
                step *= -1;
            }
            if (translateX < -maxx) {
                translateX = maxx;
                step *= -1;
            }
            if (translateY > maxy) {
                translateY = -maxy;
                step *= -1;
            }
            if (translateY < -maxy) {
                translateY = maxy;
                step *= -1;
            }
            
            rotate += rotateStep;
        }
    }
}