package com.aiyuba.glsurfaceview;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by maoyujiao on 2019/12/19.
 *

 */

public class MulitySurfaceRender implements WlEglGLSurfaceView.Renderer {
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
    private int vboId;
    private int index;


    public MulitySurfaceRender(Context context) {
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

    public void setTextureId(int textureId,int index){
        this.textureId = textureId;
        this.index = index;
    }

    @Override
    public void onSurfaceCreated() {
        String fragmentShader;
        String vertextShader =  WlShaderUtil.getRawResource(mContext, R.raw.vertex_shader);
        fragmentShader = WlShaderUtil.getRawResource(mContext,R.raw.fragment_shader);
        if(index == 0){
            fragmentShader = WlShaderUtil.getRawResource(mContext,R.raw.fragment_shader1);
        } else if(index == 1){
            fragmentShader = WlShaderUtil.getRawResource(mContext,R.raw.fragment_shader2);
        } else if(index == 2){
            fragmentShader = WlShaderUtil.getRawResource(mContext,R.raw.fragment_shader3);
        }
        program = WlShaderUtil.createProgram(vertextShader, fragmentShader);
        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");
        sampler = GLES20.glGetUniformLocation(program, "sTexture");

        int[] vbo = new int[1];
//        1、创建VBO
        GLES20.glGenBuffers(1,vbo,0);
        vboId = vbo[0];
        //绑定vbo
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        // 为vbo分配大小
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,vertexData.length * 4 + fragmentData.length * 4,null,GLES20.GL_STATIC_DRAW);
        // 为vbo 设置顶点数据
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER,0,vertexData.length * 4,vertexBuffer);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER,vertexData.length * 4,fragmentData.length * 4,fragmentBuffer);
        //解绑vbo
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);

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

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition,2,GLES20.GL_FLOAT,false,8,0);

        GLES20.glEnableVertexAttribArray(fPosition);
        GLES20.glVertexAttribPointer(fPosition,2,GLES20.GL_FLOAT,false,8,vertexData.length * 4);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);

    }
}
