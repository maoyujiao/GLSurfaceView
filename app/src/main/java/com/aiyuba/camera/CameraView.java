package com.aiyuba.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;

import com.aiyuba.glsurfaceview.WlEglGLSurfaceView;

/**
 * Created by maoyujiao on 2019/12/24.
 */

public class CameraView extends WlEglGLSurfaceView {
    private CameraRender render;
    private WlCamera wlCamera;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    public CameraView(Context context) {
        this(context,null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        render = new CameraRender(context);
        setRender(render);
        wlCamera = new WlCamera(context);
        render.setOnSurfaceiewCreateListener(new CameraRender.OnSurfaceiewCreateListener() {
            @Override
            public void onSurfaceViewCreate(SurfaceTexture surfaceTexture) {
                wlCamera.init(surfaceTexture, cameraId);
            }
        });

    }

    public void onDestory(){
        if(wlCamera != null) {
            wlCamera.stopPreview();
        }
    }

    public void changeCamera(int cameraId){
        if(wlCamera != null){
            wlCamera.changeCamera(cameraId);
        }
    }
}
