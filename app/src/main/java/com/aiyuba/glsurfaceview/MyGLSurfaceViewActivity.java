package com.aiyuba.glsurfaceview;

import android.opengl.GLES20;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MyGLSurfaceViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_glsurface_view);
        WlEglGLSurfaceView eglGLSurfaceView = findViewById(R.id.egl_surfaceView);
        eglGLSurfaceView.setRender(new WlEglGLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated() {

            }

            @Override
            public void onSurfaceChanged(int width, int height) {
                GLES20.glViewport(0,0,width,height);

            }

            @Override
            public void onDrawFrame() {
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                GLES20.glClearColor(0,1,1,0);
            }
        });
        eglGLSurfaceView.setRendermode(WlEglGLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
