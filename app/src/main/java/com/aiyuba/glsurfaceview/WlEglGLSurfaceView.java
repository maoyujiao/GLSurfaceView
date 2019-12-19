package com.aiyuba.glsurfaceview;

import android.content.Context;

import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLContext;

/**
 * Created by maoyujiao on 2019/12/19.
 * 自定义GLSurfaceView
 */

public class WlEglGLSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Renderer mRenderer;
    private GLThread mGLThread;
    private EGLContext mEGLContext;
    private Surface surface;
    private int mRenderMode;
    public final static int RENDERMODE_WHEN_DIRTY = 0;
    public final static int RENDERMODE_CONTINUOUSLY = 1;

    private final WeakReference<WlEglGLSurfaceView> mThisWeakRef = new WeakReference<WlEglGLSurfaceView>(this);

    public WlEglGLSurfaceView(Context context) {
        this(context, null);
    }

    public WlEglGLSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WlEglGLSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
    }

    public void setSurfaceAndEglContext(Surface surface, EGLContext eglContext) {
        this.surface = surface;
        this.mEGLContext = eglContext;
    }

    public void setRendermode(int rendermode) {
        mRenderMode = rendermode;
    }

    public void setRender(Renderer renderer) {
        mRenderer = renderer;

    }

    public void requestRender() {
        mGLThread.requestRender();
    }

    public EGLContext getmEGLContext() {
        return mGLThread.getEglContext();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (surface == null) {
            surface = holder.getSurface();
        }
        mGLThread = new GLThread(mThisWeakRef);
        mGLThread.isCreate = true;
        mGLThread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mGLThread.width = width;
        mGLThread.height = height;
        mGLThread.isChange = true;

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mGLThread.onDestory();
        mGLThread = null;
        surface = null;
        mEGLContext = null;
    }


    public interface Renderer {
        void onSurfaceCreated();

        void onSurfaceChanged(int width, int height);

        void onDrawFrame();
    }

    public static class GLThread extends Thread {
        private WlEglGLSurfaceView surfaceView;
        private EGLHepler eglHepler;

        private Object object = null;

        private boolean isExit = false;
        private boolean isCreate = false;
        private boolean isChange = false;
        private boolean isStart = false;

        private int width;
        private int height;

        public GLThread(WeakReference<WlEglGLSurfaceView> mThisWeakRef) {
            surfaceView = mThisWeakRef.get();
        }

        @Override
        public void run() {
            super.run();
            isExit = false;
            isStart = false;
            object = new Object();
            eglHepler = new EGLHepler();
            eglHepler.initEGl(surfaceView.getHolder().getSurface(), surfaceView.mEGLContext);

            while (true) {
                if (isExit) {
                    release();
                }

                if(isStart) {
                    if (surfaceView.mRenderMode == RENDERMODE_WHEN_DIRTY) {
                        synchronized (object) {
                            try {
                                object.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    } else if (surfaceView.mRenderMode == RENDERMODE_CONTINUOUSLY) {
                        try {
                            Thread.sleep(1000 / 60);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        throw new RuntimeException("render mode参数异常");
                    }
                }

                onCreate();
                onChange(width, height);
                onDraw();
                isStart = true;

            }


        }

        private void onCreate() {
            if (!isCreate && surfaceView.mRenderer != null) {
                surfaceView.mRenderer.onSurfaceCreated();
                isCreate = true;
            }

        }

        private void onChange(int with, int height) {
            if (isChange && surfaceView.mRenderer != null) {
                surfaceView.mRenderer.onSurfaceChanged(with, height);
            }

        }

        private void onDraw() {
            if (surfaceView.mRenderer != null && eglHepler != null) {
                surfaceView.mRenderer.onDrawFrame();
                if(!isStart){
                    surfaceView.mRenderer.onDrawFrame();
                }
                eglHepler.swapBuffer();
            }

        }


        private void requestRender() {
            if (object != null) {
                synchronized (object) {
                    object.notifyAll();
                }
            }
        }

        public void onDestory() {
            isExit = true;
            requestRender();
        }

        private void release() {
            if (eglHepler != null) {
                eglHepler.destoryEgl();
                eglHepler = null;
                object = null;
                surfaceView = null;
            }
        }


        public EGLContext getEglContext() {
            return eglHepler.getmEglContext();
        }
    }
}
