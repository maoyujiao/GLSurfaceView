package com.aiyuba.glsurfaceview;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by maoyujiao on 2019/12/23.
 */

public class WlGLSurfaceView extends WlEglGLSurfaceView {
    private FBOVBORender render;
    public WlGLSurfaceView(Context context) {
        this(context,null);
    }

    public WlGLSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WlGLSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        render = new FBOVBORender(context);
        setRender(render);
    }

    public FBOVBORender getRender(){
        if(render != null){
            return render;
        }
        return null;
    }
}
