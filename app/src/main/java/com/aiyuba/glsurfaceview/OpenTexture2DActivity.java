package com.aiyuba.glsurfaceview;

import android.opengl.GLES20;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * OpenGL ES渲染图片纹理
 * 1、编写着色器（顶点着色器和片元着色器）

 2、设置顶点、纹理坐标

 3、加载着色器

 4、创建纹理

 5、渲染图片

 */
public class OpenTexture2DActivity extends AppCompatActivity {
    private String VertexGLSL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_texture2_d);
        WlEglGLSurfaceView eglGLSurfaceView = findViewById(R.id.egl_surfaceView);
//        eglGLSurfaceView.setRender(new TextureRender(this));
        eglGLSurfaceView.setRender(new VBORender(this));
    }
}
