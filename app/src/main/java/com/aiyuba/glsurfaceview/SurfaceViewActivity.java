package com.aiyuba.glsurfaceview;

import android.opengl.GLES20;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SurfaceViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view);
        final SurfaceView surfaceView = findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(final SurfaceHolder holder, int format, final int width, final int height) {

                new Thread(){
                    @Override
                    public void run() {
                        EGLHepler eglHepler = new EGLHepler();
                        eglHepler.initEGl(holder.getSurface(),null);
                        while (true){
                            GLES20.glViewport(0,0,width,height);
                            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                            GLES20.glClearColor(1f,0,0,1f);
                            eglHepler.swapBuffer();
                            try {
                                Thread.sleep(16);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }
}
