package com.find.guide.activity;

import com.find.guide.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

@SuppressLint("HandlerLeak")
public class SplashActivity extends Activity {

    private static final int WHAT_ENTER_MAIN = 1;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_ENTER_MAIN) {
                enterMain();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mHandler.sendEmptyMessageDelayed(WHAT_ENTER_MAIN, 1200);
    }

    private void enterMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeMessages(WHAT_ENTER_MAIN);
        super.onDestroy();
    }

}
