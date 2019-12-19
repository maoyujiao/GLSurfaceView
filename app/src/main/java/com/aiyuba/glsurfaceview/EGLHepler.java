package com.aiyuba.glsurfaceview;

import android.opengl.EGL14;
import android.view.Surface;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * Created by maoyujiao on 2019/12/19.
 * EGl环境创建
 * 1、得到Egl实例：
 2、得到默认的显示设备（就是窗口）
 3、初始化默认显示设备
 4、设置显示设备的属性
 5、从系统中获取对应属性的配置
 6、创建EglContext
 7、创建渲染的Surface
 8、绑定EglContext和Surface到显示设备中
 9、刷新数据，显示渲染场景


 */

public class EGLHepler {
    private EGL10 mEgl;
    private EGLDisplay mEglDisplay;
    private EGLContext mEglContext;
    private EGLSurface mEGLSurface;

    public EGLContext getmEglContext() {
        return mEglContext;
    }

    public void initEGl(Surface surface, EGLContext eglContext){
        /*
             * Get an EGL instance
             */
        mEgl = (EGL10) EGLContext.getEGL();

            /*
             * Get to the default display.
             * 2、得到默认的显示设备（就是窗口）
             *
             */
        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }

            /*
             * We can now initialize EGL for that display
             * 3、初始化默认显示设备
             */
        int[] version = new int[2];
        if(!mEgl.eglInitialize(mEglDisplay, version)) {
            throw new RuntimeException("eglInitialize failed");
        }

        //4、设置显示设备的属性
        int[] attributs = new int[]{
                EGL10.EGL_RED_SIZE,8,
                EGL10.EGL_GREEN_SIZE,8,
                EGL10.EGL_BLUE_SIZE,8,
                EGL10.EGL_ALPHA_SIZE,8,
                EGL10.EGL_DEPTH_SIZE,8,
                EGL10.EGL_STENCIL_SIZE,8,
                EGL10.EGL_RENDERABLE_TYPE,4,
                EGL10.EGL_NONE
        };

//        5、从系统中获取对应属性的配置
        int[] num_config = new int[1];
        if(!mEgl.eglChooseConfig(mEglDisplay,attributs,null,1,num_config)){
            throw new IllegalArgumentException("choose connfig fail");
        }
        int numConfig = num_config[0];
        if(numConfig < 0){
            throw new IllegalArgumentException("No configs match configSpec");
        }
        EGLConfig[] eglConfig = new EGLConfig[numConfig];
        if (!mEgl.eglChooseConfig(mEglDisplay, attributs, eglConfig, numConfig,
                num_config)) {
            throw new IllegalArgumentException("eglChooseConfig#2 failed");
        }

        //6、创建EglContext
        int[] attrib_list = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE
        };

        if(eglContext !=null) {
            mEglContext = mEgl.eglCreateContext(mEglDisplay, eglConfig[0], eglContext, attrib_list);
        } else {
            mEglContext = mEgl.eglCreateContext(mEglDisplay, eglConfig[0], EGL10.EGL_NO_CONTEXT, attrib_list);

        }

//        7、创建渲染的Surface
         mEGLSurface = mEgl.eglCreateWindowSurface(mEglDisplay,eglConfig[0],surface,null);

        //8、绑定EglContext和Surface到显示设备中
        if(!mEgl.eglMakeCurrent(mEglDisplay,mEGLSurface,mEGLSurface,mEglContext)){
            throw new RuntimeException("eglMakeCurrent fail");
        }

     }

//     9、刷新数据，显示渲染场景
     public void swapBuffer(){
        if(mEgl!=null){
            mEgl.eglSwapBuffers(mEglDisplay,mEGLSurface);
        } else{
            throw new RuntimeException("egl is null");
        }
     }


     public void destoryEgl(){
         if(mEgl !=null){
             mEgl.eglMakeCurrent(mEglDisplay,EGL10.EGL_NO_SURFACE,EGL10.EGL_NO_SURFACE,EGL10.EGL_NO_CONTEXT);
             mEgl.eglDestroyContext(mEglDisplay,EGL10.EGL_NO_CONTEXT);
             mEglContext = null;
             mEgl.eglDestroySurface(mEglDisplay,EGL10.EGL_NO_SURFACE);
             mEGLSurface = null;
             mEgl = null;
         }
     }

}
