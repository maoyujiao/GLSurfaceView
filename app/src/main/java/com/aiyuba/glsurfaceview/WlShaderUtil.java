package com.aiyuba.glsurfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by maoyujiao on 2019/12/19.
 *
 * OpenGL ES加载Shader

 * 1、创建shader（着色器：顶点或片元）
 int shader = GLES20.glCreateShader(shaderType);
 2、加载shader源码并编译shader
 GLES20.glShaderSource(shader, source);
 GLES20.glCompileShader(shader);
 3、检查是否编译成功：
 GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
 4、创建一个渲染程序：
 int program = GLES20.glCreateProgram();
 5、将着色器程序添加到渲染程序中：
 GLES20.glAttachShader(program, vertexShader);
 6、链接源程序：
 GLES20.glLinkProgram(program);

 */

public class WlShaderUtil {
    public static String getRawResource(Context context,int resId){
        InputStream inputStream = context.getResources().openRawResource(resId);
        StringBuilder sb = new StringBuilder();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null){
                sb.append(line).append("\n");
            }
            bufferedReader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int loadShader(int shaderType,String source){
//        1、创建shader（着色器：顶点或片元）
        int shader = GLES20.glCreateShader(shaderType);
        if(shader != 0) {
//            2、加载shader源码并编译shader
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);

//            3、检查是否编译成功：
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] != GLES20.GL_TRUE) {
                Log.d("maoyujiao","shader编译失败");
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;

    }

    public static int createProgram(String vertexSource,String fragmentSorce){
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexSource);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentSorce);
        int progrgam = 0;
        if(vertexShader != 0 && fragmentShader != 0) {
//        4、创建一个渲染程序：
            progrgam = GLES20.glCreateProgram();
//        5、将着色器程序添加到渲染程序中：
            GLES20.glAttachShader(progrgam, vertexShader);
            GLES20.glAttachShader(progrgam, fragmentShader);
//        6、链接源程序：
            GLES20.glLinkProgram(progrgam);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(progrgam, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if(linkStatus[0] != GLES20.GL_TRUE){
                Log.d("maoyujiao","程序链接失败");
                progrgam = 0;
            }
        }
        return progrgam;
    }

    public static int loadTexture(Context context,int src){
        int[] textureid = new int[1];
        GLES20.glGenTextures(1,textureid,0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureid[0]);
        GLES20.glActiveTexture(textureid[0]);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),src);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        return textureid[0];
    }

    public static DisplayMetrics getDisplayMetric(Context context){
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    public static int[] getBitmapSize(Context context,int resId){
        int[] size = new int[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(),resId,options);
        size[0] = options.outWidth;
        size[1] = options.outHeight;
//        options.inJustDecodeBounds = false;
        return size;
    }


}
