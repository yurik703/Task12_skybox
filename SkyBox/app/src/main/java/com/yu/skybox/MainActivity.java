package com.yu.skybox;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CustomGLSurfaceView view = new CustomGLSurfaceView(this);
        setContentView(view);
    }
}
