package com.yu.skybox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGLRenderer implements GLSurfaceView.Renderer {

    Context context;
    float ratio;
    float xRotation,yRotation;
    private SkyBox skyBox;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];

    final int textureObjectID[] = new int[1];


    public OpenGLRenderer(Context context){
        this.context = context;
    }


    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.9f,1f,0.9f,1f);

        skyBox = new SkyBox();
        //GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glGenTextures(1,textureObjectID,0);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled=false;

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        final int cubeResorces[] = {R.drawable.negx,R.drawable.posx,R.drawable.negy,R.drawable.posy,
                R.drawable.negz,R.drawable.posz};
        final int positionInCube[] = {
                GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
                GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
                GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
                GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
                GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z,
                GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
        };


        Bitmap cubeBitmap;

        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP,textureObjectID[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);


        for(int i=0;i<6;i++) {
            cubeBitmap = BitmapFactory.decodeResource(context.getResources(), cubeResorces[i], options);
            GLUtils.texImage2D(positionInCube[i],0,cubeBitmap,0);
            cubeBitmap.recycle();
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP,0); //Unbind
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int w, int h) {
        GLES20.glViewport(0,0,w,h);
        xRotation=0;
        yRotation=0;

        ratio = (float)w/h;

        Matrix.setIdentityM(mModelMatrix,0);
        Matrix.setLookAtM(mViewMatrix,0,
                0.0f,0.0f,0.001f,
                0f,0f,0f,
                0f,1f,0);
        Matrix.perspectiveM(mProjectionMatrix,0,60f,ratio,0.0f,2f);
        Matrix.multiplyMM(mMVPMatrix,0,mViewMatrix,0,mModelMatrix,0);
        Matrix.multiplyMM(mMVPMatrix,0,mProjectionMatrix,0,mMVPMatrix,0);
    }

    public void changeMyView(float dX, float dY,float scale){
        xRotation += dX / 20f;
        yRotation += dY / 20f;
        if (yRotation < -90) {
            yRotation = -90;
        } else if (yRotation > 90) {
            yRotation = 90;
        }

        Matrix.setIdentityM(mModelMatrix,0);
        Matrix.rotateM(mModelMatrix,0,-yRotation,1f,0f,0f);
        Matrix.rotateM(mModelMatrix,0,-xRotation,0f,1f,0f);

        Matrix.setLookAtM(mViewMatrix,0,
                0.0f,0.0f,0.0001f,
                0f,0f,0f,
                0f,1f,0);
        Matrix.perspectiveM(mProjectionMatrix,0,90f-10f*scale,ratio,0.0f,2f);
        Matrix.multiplyMM(mMVPMatrix,0,mViewMatrix,0,mModelMatrix,0);
        Matrix.multiplyMM(mMVPMatrix,0,mProjectionMatrix,0,mMVPMatrix,0);
    }





    @Override
    public void onDrawFrame(GL10 gl10) {
        //GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        skyBox.draw(mMVPMatrix,textureObjectID[0]);

    }
}
