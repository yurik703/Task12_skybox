package com.yu.skybox;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class CustomGLSurfaceView  extends GLSurfaceView
        implements View.OnTouchListener
{
    private final OpenGLRenderer mRenderer;

    //Touch
    float previousX, previousY;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 3.0f;

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *=detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 8.5f));
            return true;
        }
    }

    public CustomGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        mRenderer = new OpenGLRenderer(context);
        setRenderer(mRenderer);
        setOnTouchListener(this);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

    }

    public OpenGLRenderer getRenderer() {
        return mRenderer;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent != null) {

            mScaleDetector.onTouchEvent(motionEvent);
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                previousX = motionEvent.getX();
                previousY = motionEvent.getY();
            }
            else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                final float deltaX = motionEvent.getX() - previousX;
                final float deltaY = motionEvent.getY() - previousY;
                previousX = motionEvent.getX();
                previousY = motionEvent.getY();
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mRenderer.changeMyView(deltaX,deltaY,mScaleFactor);
                    }
                });
            }
            return true;
        } else {
            return false;

        }

    }
}