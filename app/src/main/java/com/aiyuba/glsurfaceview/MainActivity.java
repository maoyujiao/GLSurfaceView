package com.aiyuba.glsurfaceview;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {
    private int textureId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GLSurfaceView surfaceView = findViewById(R.id.glSurfaceView);
        surfaceView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                int[] textures = new int[1];
                GLES20.glGenTextures(0,textures,0);
                textureId = textures[0];
                GLES20.glActiveTexture(textureId);

            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                GLES20.glViewport(0,0,width,height);

            }

            @Override
            public void onDrawFrame(GL10 gl) {
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                GLES20.glClearColor(1f,0f, 0f, 1f);


            }
        });
    }
}
