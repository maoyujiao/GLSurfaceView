package com.aiyuba.glsurfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by maoyujiao on 2019/12/19.
 *
 * 9、检查链接源程序是否成功
 GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
 10、得到着色器中的属性：
 int vPosition  = GLES20.glGetAttribLocation(program, "v_Position");
 11、使用源程序：
 GLES20.glUseProgram(program);
 12、使顶点属性数组有效：
 GLES20.glEnableVertexAttribArray(vPosition);
 13、为顶点属性赋值：
 GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8,
 vertexBuffer);
 14、绘制图形：
 GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);


 OpenGL ES绘制纹理过程

 1、加载shader和生成program过程不变

 2、创建和绑定纹理：
 GLES20.glGenTextures(1, textureId, 0);
 GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid);
 3、设置环绕和过滤方式
 环绕（超出纹理坐标范围）：（s==x t==y GL_REPEAT 重复）
 GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
 GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
 过滤（纹理像素映射到坐标点）：（缩小、放大：GL_LINEAR线性）
 GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
 GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);



 */

public class TextureRender implements WlEglGLSurfaceView.Renderer {
    private Context mContext;
    private int vPosition;
    private int fPosition;
    //顶点坐标系
    private float[] vertexData = new float[]{
            -1,-1,
            1,-1,
            -1,1,
            1,1
    };

    //纹理坐标系
    private float[] fragmentData = new float[]{
            0,1,
            1,1,
            0,0,
            1,0
    };
    private int sampler;
    private int textureId;
    private int program;
    private FloatBuffer vertexBuffer;
    private FloatBuffer fragmentBuffer;


    public TextureRender(Context context) {
        mContext = context;
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        fragmentBuffer = ByteBuffer.allocateDirect(fragmentData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(fragmentData);
        fragmentBuffer.position(0);

    }

    @Override
    public void onSurfaceCreated() {
        program = WlShaderUtil.createProgram(WlShaderUtil.getRawResource(mContext, R.raw.vertex_shader),
                WlShaderUtil.getRawResource(mContext,R.raw.fragment_shader));
        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");
        sampler = GLES20.glGetUniformLocation(program, "sTexture");


        int[] texture = new int[1];
        GLES20.glGenTextures(1,texture,0);
        textureId = texture[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glUniform1i(sampler,0);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_girl);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);
        bitmap.recycle();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);

    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0,0,width,height);

    }

    @Override
    public void onDrawFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1,1,0,0);
        GLES20.glUseProgram(program);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition,2,GLES20.GL_FLOAT,false,8,vertexBuffer);

        GLES20.glEnableVertexAttribArray(fPosition);
        GLES20.glVertexAttribPointer(fPosition,2,GLES20.GL_FLOAT,false,8,fragmentBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);


    }
}
