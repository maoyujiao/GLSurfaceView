package com.aiyuba.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.DisplayMetrics;

import com.aiyuba.glsurfaceview.WlShaderUtil;

import java.io.IOException;
import java.util.List;

/**
 * Created by maoyujiao on 2019/12/24.
 */

public class WlCamera {
    private Context context;
    private Camera camera;
    private int with;
    private int height;
    private SurfaceTexture surfaceTexture;

    public WlCamera(Context context) {
        this.context = context;
        with = WlShaderUtil.getDisplayMetric(context).widthPixels;
        height = WlShaderUtil.getDisplayMetric(context).heightPixels;
    }

    public void init(SurfaceTexture surfaceTexture, int cameraId) {
        this.surfaceTexture = surfaceTexture;
        initParams(cameraId);
    }

    private void initParams(int cameraId) {
        try {
            camera = Camera.open(cameraId);
            camera.setPreviewTexture(surfaceTexture);

            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode("off");
            parameters.setPreviewFormat(ImageFormat.NV21);
            Camera.Size size = getFitSize(parameters.getSupportedPictureSizes());
            parameters.setPictureSize(size.width, size.height);

            size = getFitSize(parameters.getSupportedPreviewSizes());
            parameters.setPreviewSize(size.width, size.height);
            camera.setParameters(parameters);

            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Camera.Size getFitSize(List<Camera.Size> supportedPictureSizes) {
        //?干嘛宽高置换
        if (with < height) {
            int t = height;
            height = with;
            with = t;
        }

        for (Camera.Size size : supportedPictureSizes) {
            if (1.0f * size.width / size.height == 1.0f * with / height) {
                return size;
            }
        }
        return supportedPictureSizes.get(0);

    }

    public void stopPreview() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void changeCamera(int cameraId) {
        if (camera != null){
            stopPreview();
        }
        initParams(cameraId);
    }


}
