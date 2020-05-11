package com.yu.skybox;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class SkyBox {
    //Geometry
    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    private final int POSITION_COUNT = 3; //number of position coordinates
    private final int COORDINATES_PER_VERTEX = POSITION_COUNT;


    static float cubeCoords[] = {
            -1, 1, 1,    // (0) Top-left near
            1, 1, 1,     // (1) Top-right near
            -1, -1, 1,   // (2) Bottom-left near
            1, -1, 1,    // (3) Bottom-right near
            -1, 1, -1,   // (4) Top-left far
            1, 1, -1,    // (5) Top-right far
            -1, -1, -1,  // (6) Bottom-left far
            1, -1, -1    // (7) Bottom-right fa
    };

    static short indexArray[] = {
            // Front
            1, 3, 0,
            0, 3, 2,
            // Back
            4, 6, 5,
            5, 6, 7,
            // Left
            0, 2, 4,
            4, 2, 6,
            // Right
            5, 7, 1,
            1, 7, 3,
            // Top
            5, 1, 4,
            4, 1, 0,
            // Bottom
            6, 2, 7,
            7, 2, 3
    };

    //Rendering
    private final String vertexShaderCode =
            "attribute vec3 aPosition;" +
                    "uniform mat4 uMVPMatrix;" +
                    "varying vec3 vPosition;" +
                    "void main() {" +
                    "vPosition = aPosition;" +
                    "gl_Position = uMVPMatrix*vec4(aPosition, 1.0);" +
                    "gl_Position = gl_Position.xyww;"+
                    "}";


    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform samplerCube uTextureUnit;" +
                    "varying vec3 vPosition;" +
                    "void main() {" +
                    "  gl_FragColor = textureCube(uTextureUnit,vPosition);" +
                    "}";


    //Draw
    private final int mProgram;
    private int mPositionHandle;
    private int mTextureUnitHandle;
    private int mMVPMatrixHandle;
    private final int vertexStride = COORDINATES_PER_VERTEX * 4; // 4 bytes per vertex


    public SkyBox() {
        ByteBuffer bb = ByteBuffer.allocateDirect(cubeCoords.length*4);//4 bytes for 1 float
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(cubeCoords);

        ByteBuffer ib = ByteBuffer.allocateDirect(indexArray.length*2);//2 bytes for 1 short
        ib.order(ByteOrder.nativeOrder());
        indexBuffer = ib.asShortBuffer();
        indexBuffer.put(indexArray);



        int vertexShader = OpenGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = OpenGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix,int textureID){
        GLES20.glUseProgram(mProgram);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mTextureUnitHandle = GLES20.glGetUniformLocation(mProgram, "uTextureUnit");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle,1,false,mvpMatrix,0);

        vertexBuffer.position(0);
        indexBuffer.position(0);

        GLES20.glVertexAttribPointer(mPositionHandle, POSITION_COUNT,
                GLES20.GL_FLOAT, false,vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP,textureID);
        GLES20.glUniform1i(mTextureUnitHandle,0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP,0);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
