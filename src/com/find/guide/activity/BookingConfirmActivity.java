package com.find.guide.activity;

import com.find.guide.R;
import com.find.guide.model.TourGuide;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BookingConfirmActivity extends BaseActivity implements OnClickListener {

    public static final String INTENT_EXTRA_GUIDE = "intent_extra_guide";

    private TourGuide mTourGuide;

    private TextView mGuideName;
    private EditText mDestinationEt;
    private EditText mStartTimeEt;
    private Button mBookingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_booking_confirm);
        initUI();

        if (getIntent() != null) {
            mTourGuide = (TourGuide) getIntent().getSerializableExtra(INTENT_EXTRA_GUIDE);
        }
        setData();

    }

    private void initUI() {
        mGuideName = (TextView) findViewById(R.id.guide_name);
        mDestinationEt = (EditText) findViewById(R.id.destination_et);
        mStartTimeEt = (EditText) findViewById(R.id.start_time_et);
        mBookingBtn = (Button) findViewById(R.id.booking_btn);

        mBookingBtn.setOnClickListener(this);
    }

    private void setData() {
        if (mTourGuide != null) {
            mGuideName.setText(mTourGuide.getUserName());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.booking_btn) {
            
        }
    }

}
