package com.aiyuba.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import com.aiyuba.glsurfaceview.R;
import com.aiyuba.glsurfaceview.WlEglGLSurfaceView;
import com.aiyuba.glsurfaceview.WlShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by maoyujiao on 2019/12/24.
 */

public class CameraRender implements WlEglGLSurfaceView.Renderer,SurfaceTexture.OnFrameAvailableListener {

    private CameraFboRender fboRender;
    private OnSurfaceiewCreateListener onSurfaceiewCreateListener;
    private int fboTextureId;
    private int vboId;
    private int fboId;
    private FloatBuffer vertexBuffer;
    private FloatBuffer fragmnetBuffer;
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
    private int vertexPosition;
    private int fragmentPosition;
    private int program;
    private int cameraTextureId;
    private SurfaceTexture surfaceTexture;

    public CameraRender(Context context) {
        this.context = context;
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4).order(ByteOrder.nativeOrder()).
                asFloatBuffer().put(vertexData);
        vertexBuffer.position(0);
        fragmnetBuffer = ByteBuffer.allocateDirect(fragmentData.length * 4).order(ByteOrder.nativeOrder()).
                asFloatBuffer().put(fragmentData);
        fragmnetBuffer.position(0);
        fboRender = new CameraFboRender(context);
    }


    public void setOnSurfaceiewCreateListener(OnSurfaceiewCreateListener onSurfaceiewCreateListener) {
        this.onSurfaceiewCreateListener = onSurfaceiewCreateListener;
    }

    @Override
    public void onSurfaceCreated() {
        fboRender.onCreate();
        String vettextShader = WlShaderUtil.getRawResource(context, R.raw.vertex_shader);
        String fragmentShader = WlShaderUtil.getRawResource(context, R.raw.fragment_shader);
        program = WlShaderUtil.createProgram(vettextShader,fragmentShader);
        vertexPosition = GLES20.glGetAttribLocation(program,"v_Position");
        fragmentPosition = GLES20.glGetAttribLocation(program,"f_Position");
        fragmentPosition = GLES20.glGetAttribLocation(program,"s");

        int[] vboids = new int[1];
        GLES20.glGenBuffers(1,vboids,0);
        vboId = vboids[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,vboId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,vertexData.length * 4 + fragmentData.length * 4,null,GLES20.GL_STATIC_DRAW);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER,0,vertexData.length * 4,vertexBuffer);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER,vertexData.length * 4,fragmentData.length * 4,fragmnetBuffer);



        int[] textureIds = new int[1];
        GLES20.glGenTextures(1,textureIds,0);
        fboTextureId = textureIds[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTextureId);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,WlShaderUtil.getDisplayMetric(context).widthPixels
                ,WlShaderUtil.getDisplayMetric(context).heightPixels,0
                ,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,null);

        int[] fboIds = new int[1];
        GLES20.glGenBuffers(1,fboIds,0);
        fboId = fboIds[0];
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,fboId);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,GLES20.GL_COLOR_ATTACHMENT0,GLES20.GL_TEXTURE_2D, fboTextureId,0);

        if(GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE)
        {
            Log.e("maoyujiao", "fbo wrong");
        }
        else
        {
            Log.e("maoyujiao", "fbo success");
        }

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);

        int[] textureIdseos = new int[1];
        GLES20.glGenTextures(1,textureIdseos,0);
        cameraTextureId = textureIdseos[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,cameraTextureId);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);

        surfaceTexture = new SurfaceTexture(cameraTextureId);
        surfaceTexture.setOnFrameAvailableListener(this);

        if(onSurfaceiewCreateListener != null){
            onSurfaceiewCreateListener.onSurfaceViewCreate(surfaceTexture);
        }
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,0);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0,0,width,height);
        fboRender.onChange(width,height);

    }

    @Override
    public void onDrawFrame() {
        surfaceTexture.updateTexImage();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1,0,0,1);
        GLES20.glUseProgram(program);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,fboId);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,vboId);
        GLES20.glEnableVertexAttribArray(vertexPosition);
        GLES20.glVertexAttribPointer(vertexPosition,2,GLES20.GL_FLOAT,false,8,0);
        GLES20.glEnableVertexAttribArray(fragmentPosition);
        GLES20.glVertexAttribPointer(vertexPosition,2,GLES20.GL_FLOAT,false,8,vertexData.length * 4);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);
        fboRender.onDraw(fboTextureId);



    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

    }

    interface OnSurfaceiewCreateListener{
        void onSurfaceViewCreate(SurfaceTexture surfaceTexture);
    }
}
