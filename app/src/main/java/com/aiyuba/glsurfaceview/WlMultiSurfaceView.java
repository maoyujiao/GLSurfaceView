package com.aiyuba.glsurfaceview;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by maoyujiao on 2019/12/23.
 */

public class WlMultiSurfaceView extends WlEglGLSurfaceView {
    private MulitySurfaceRender render;
    public WlMultiSurfaceView(Context context) {
        this(context,null);
    }

    public WlMultiSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WlMultiSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        render = new MulitySurfaceRender(context);
        setRender(render);
    }

    public void setTextureId(int textureId,int index){
        if(render != null) {
            render.setTextureId(textureId, index);
        }
    }
}
