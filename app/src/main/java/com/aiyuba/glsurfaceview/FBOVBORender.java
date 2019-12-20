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

 */

public class FBOVBORender implements WlEglGLSurfaceView.Renderer {
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
    private int fboId;
    private FBORender fboRender;
    private int imgTextureId;


    public FBOVBORender(Context context) {
        mContext = context;
        fboRender = new FBORender(context);
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
        fboRender.onCreate();
        program = WlShaderUtil.createProgram(WlShaderUtil.getRawResource(mContext, R.raw.vertex_shader),
                WlShaderUtil.getRawResource(mContext,R.raw.fragment_shader));
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

        //创建fbo
        int[] fbo = new int[1];
        GLES20.glGenBuffers(1,fbo,0);
        fboId = fbo[0];
        //绑定fbo
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,fboId);

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

//        3、设置FBO分配内存大小
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,
                WlShaderUtil.getDisplayMetric(mContext).widthPixels,WlShaderUtil.getDisplayMetric(mContext).heightPixels,0,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,null);
        //把纹理绑定到fbo
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,GLES20.GL_COLOR_ATTACHMENT0,GLES20.GL_TEXTURE_2D,textureId,0);
        //检查fbo绑定成功否
        if(GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE){
            throw new RuntimeException("fbo绑定失败");
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        //解绑fbo
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
        imgTextureId = WlShaderUtil.loadTexture(mContext,R.drawable.ic_girl);

    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0,0,width,height);
        fboRender.onChanged(width,height);

    }

    @Override
    public void onDrawFrame() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,fboId);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1,1,0,0);
        GLES20.glUseProgram(program);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,imgTextureId);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition,2,GLES20.GL_FLOAT,false,8,0);

        GLES20.glEnableVertexAttribArray(fPosition);
        GLES20.glVertexAttribPointer(fPosition,2,GLES20.GL_FLOAT,false,8,vertexData.length * 4);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
        fboRender.onDraw(textureId);

    }
}
