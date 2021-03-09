package com.mksol.assessment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Task()).start();
    }

    class Task implements Runnable {
        @Override
        public void run() {
            startActivity(new Intent(mContext, MainActivity.class));
            finish();

        }
    }

}

