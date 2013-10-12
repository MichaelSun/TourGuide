package com.find.guide.activity;

import com.find.guide.R;

import android.os.Bundle;

public class BookingActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_booking);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
