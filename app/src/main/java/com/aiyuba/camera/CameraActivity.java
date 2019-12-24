package com.aiyuba.camera;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aiyuba.glsurfaceview.R;
import com.aiyuba.glsurfaceview.WlEglGLSurfaceView;

public class CameraActivity extends AppCompatActivity {

    private CameraView surfaceView;
    private Button btn_change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        surfaceView = findViewById(R.id.surfaceView);
        btn_change = findViewById(R.id.btn_change);
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surfaceView.changeCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        surfaceView.onDestory();
    }
}
