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
 *FBO纹理坐标系 纹理坐标系 顶点坐标系 这三者都是不一样的
 *
 1、FBO： Frame Buffer object

 2、为什么要用FBO?
 当我们需要对纹理进行"多次渲染采样时"，而这些渲染采样的中间过程是不需要展示给用户看的，所以我们就可以用一个单独的缓冲对象（离屏渲染）来存储我们的这几次渲染采样的结果，等处理完后才显示到窗口上。

 3、优势
 提高渲染效率，避免闪屏，可以很方便的实现纹理共享等。

 4、渲染方式
 渲染到缓冲区（Render）- 深度测试和模板测试
 渲染到纹理（Texture）- 图像渲染


 1、创建FBO
 GLES20.glGenBuffers(1, fbos, 0);

 2、绑定FBO
 GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbos[0]);

 3、设置FBO分配内存大小
 GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 720, 1280, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

 4、把纹理绑定到FBO
 GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureid, 0);

 5、检查FBO绑定是否成功
 GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE)

 6、解绑FBO
 GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

使用FbO
 1、绑定FBO
 GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbos[0]);

 2、获取需要绘制的图片纹理，然后绘制渲染

 3、解绑FBO
 GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

 4、再把绑定到FBO的纹理绘制渲染出来

 */

public class FBORender {
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
//            0,1,
//            1,1,
//            0,0,
//            1,0

            0,0,
            1,0,
            0,1,
            1,1

    };
    private int sampler;
    private int program;
    private FloatBuffer vertexBuffer;
    private FloatBuffer fragmentBuffer;
    private int vboId;


    public FBORender(Context context) {
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

    public void onCreate() {
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

    }

    public void onChanged(int width, int height) {
        GLES20.glViewport(0,0,width,height);

    }

    /**
     * 绘制fbo纹理
     * @param textureId
     */
    public void onDraw(int textureId) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1,1,0,0);
        GLES20.glUseProgram(program);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);

        //绑定vbo
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        GLES20.glEnableVertexAttribArray(vPosition);
        //使用vbo
        GLES20.glVertexAttribPointer(vPosition,2,GLES20.GL_FLOAT,false,8,0);

        GLES20.glEnableVertexAttribArray(fPosition);
        GLES20.glVertexAttribPointer(fPosition,2,GLES20.GL_FLOAT,false,8,vertexData.length * 4);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);
    }
}
