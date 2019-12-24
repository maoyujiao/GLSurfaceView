package com.aiyuba.camera;

import android.content.Context;
import android.opengl.GLES20;

import com.aiyuba.glsurfaceview.R;
import com.aiyuba.glsurfaceview.WlShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by maoyujiao on 2019/12/24.
 */

public class CameraFboRender {
    private Context context;

    private float[] vertexData = new float[]{
            -1,-1,
            1,-1,
            -1,1,
            1,1
    };
    private float[] fragmentData = new float[]{
            1,0,
            1,1,
            0,0,
            1,0
    };
    private FloatBuffer vertexBuffer;
    private FloatBuffer fragmnetBuffer;
    private int vertexPosition;
    private int fragmentPosition;
    private int program;
    private int vboId;
    private int sampler;

    public CameraFboRender(Context context) {
        this.context = context;
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4).order(ByteOrder.nativeOrder()).
                asFloatBuffer().put(vertexData);
        vertexBuffer.position(0);
        fragmnetBuffer = ByteBuffer.allocateDirect(fragmentData.length * 4).order(ByteOrder.nativeOrder()).
                asFloatBuffer().put(fragmentData);
        fragmnetBuffer.position(0);

    }

    public void onCreate(){
        String vettextShader = WlShaderUtil.getRawResource(context, R.raw.vertex_shader_screen);
        String fragmentShader = WlShaderUtil.getRawResource(context, R.raw.fragment_shader_screen);
        program = WlShaderUtil.createProgram(vettextShader,fragmentShader);
        vertexPosition = GLES20.glGetAttribLocation(program,"v_Position");
        fragmentPosition = GLES20.glGetAttribLocation(program,"f_Position");
        sampler = GLES20.glGetUniformLocation(program, "sTexture");

        int[] vboids = new int[1];
        GLES20.glGenBuffers(1,vboids,0);
        vboId = vboids[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,vboId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,vertexData.length * 4 + fragmentData.length * 4,null,GLES20.GL_STATIC_DRAW);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER,0,vertexData.length * 4,vertexBuffer);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER,vertexData.length * 4,fragmentData.length * 4,fragmnetBuffer);


    }

    public void onChange(int with,int height){
        GLES20.glViewport(0,0,with,height);

    }

    public void onDraw(int textureId){
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1f,0f, 0f, 1f);
        GLES20.glUseProgram(program);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glEnableVertexAttribArray(vertexPosition);
        GLES20.glVertexAttribPointer(vertexPosition, 2, GLES20.GL_FLOAT, false, 8,
                0);

        GLES20.glEnableVertexAttribArray(fragmentPosition);
        GLES20.glVertexAttribPointer(fragmentPosition, 2, GLES20.GL_FLOAT, false, 8,
                vertexData.length * 4);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);


    }
}
