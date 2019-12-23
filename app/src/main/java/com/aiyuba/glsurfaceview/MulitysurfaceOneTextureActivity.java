package com.aiyuba.glsurfaceview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.widget.LinearLayout;

import javax.microedition.khronos.egl.EGLContext;

public class MulitysurfaceOneTextureActivity extends AppCompatActivity {
    private WlGLSurfaceView surfaceView1;
    private LinearLayout ll_layout;
    private Surface surface;
    private int textureId;
    private EGLContext eglContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mulitysurface_one_texture);
        surfaceView1 = findViewById(R.id.surfaceView1);
        ll_layout = findViewById(R.id.ll_layout);

        surfaceView1.getRender().setOnRenderCreateListener(new FBOVBORender.OnRenderCreateListener() {
            @Override
            public void onCreate(final int textureId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(ll_layout.getChildCount() > 0){
                            ll_layout.removeAllViews();
                        }
                        eglContext = surfaceView1.getmEGLContext();
                        surface = surfaceView1.getHolder().getSurface();
                        for (int i = 0; i <3 ; i++) {
                            WlMultiSurfaceView surfaceView = new WlMultiSurfaceView(MulitysurfaceOneTextureActivity.this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.MATCH_PARENT);
                            params.setMargins(10,0,10,0);
                            surfaceView.setLayoutParams(params);
                            surfaceView.setSurfaceAndEglContext(surface,eglContext);
                            surfaceView.setTextureId(textureId,i);
                            ll_layout.addView(surfaceView);
                        }

                    }
                });
            }
        });
    }

}
