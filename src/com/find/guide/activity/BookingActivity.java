package com.find.guide.activity;

import com.find.guide.R;
import com.find.guide.model.TourGuide;
import com.plugin.common.view.WebImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class BookingActivity extends BaseActivity implements OnClickListener {

    public static final String INTENT_EXTRA_GUIDE = "intent_extra_guide";

    private TourGuide mTourGuide = null;

    private WebImageView mGuideCardIv;
    private TextView mGuideNameGenderTv;
    private TextView mCityTv;
    private TextView mGoodAtScenicTv;
    private TextView mGuideHistoryTv;
    private TextView mGuideCardIdTv;
    private Button mBookingBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_booking);
        initUI();

        if (savedInstanceState != null) {

        } else {
            if (getIntent() != null) {
                mTourGuide = (TourGuide) getIntent().getSerializableExtra(INTENT_EXTRA_GUIDE);
            }
        }
        setData();
    }

    private void initUI() {
        mGuideCardIv = (WebImageView) findViewById(R.id.guide_card_iv);
        mGuideNameGenderTv = (TextView) findViewById(R.id.guide_name_gender_tv);
        mCityTv = (TextView) findViewById(R.id.city);
        mGoodAtScenicTv = (TextView) findViewById(R.id.good_scenic);
        mGuideHistoryTv = (TextView) findViewById(R.id.guide_history);
        mGuideCardIdTv = (TextView) findViewById(R.id.guide_card_id);
        mBookingBtn = (Button) findViewById(R.id.booking_btn);

        mBookingBtn.setOnClickListener(this);
    }

    private void setData() {
        if (mTourGuide != null) {
            mGuideCardIv.setImageURI(new Uri.Builder().path(mTourGuide.getGuideCardUrl()).build());
            mGuideNameGenderTv.setText(mTourGuide.getUserName());
            mCityTv.setText("" + mTourGuide.getCity());
            mGoodAtScenicTv.setText(mTourGuide.getGoodAtScenic());
            mGuideCardIdTv.setText(mTourGuide.getGuideCardId());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.booking_btn) {
            Intent intent = new Intent(this, BookingConfirmActivity.class);
            startActivity(intent);
        }
    }
}
