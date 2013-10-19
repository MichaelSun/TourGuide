package com.find.guide.activity;

import java.util.Calendar;

import com.find.guide.R;
import com.find.guide.model.CityItem;
import com.find.guide.model.TourGuide;
import com.find.guide.model.helper.CityManager;
import com.find.guide.model.helper.InviteHelper;
import com.find.guide.model.helper.InviteHelper.OnInviteListener;
import com.find.guide.setting.SettingManager;
import com.find.guide.view.TipsDialog;
import com.plugin.common.view.WebImageView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class BookingActivity extends BaseActivity implements OnClickListener {

    public static final String INTENT_EXTRA_GUIDE = "intent_extra_guide";

    private TourGuide mTourGuide = null;

    private long mStartTimeStamp = 0;
    private long mEndTimeStamp = 0;

    private WebImageView mGuideCardIv;
    private TextView mGuideNameGenderTv;
    private TextView mCityTv;
    private TextView mGoodAtScenicTv;
    private TextView mGuideHistoryTv;
    private View mGuideHistoryView;
    private TextView mGuideCardIdTv;
    private EditText mDestinationEt;
    private TextView mStartTimeTv;
    private View mStartTimeView;
    private TextView mEndTimeTv;
    private View mEndTimeView;
    private Button mBookingBtn;
    private TextView mEvaluateNumTv;
    private View mStar1;
    private View mStar2;
    private View mStar3;
    private View mStar4;
    private View mStar5;

    private DatePickerDialog mDialog;

    private AlertDialog mAlertDialog;

    private InviteHelper mInviteHelper;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_booking);
        initUI();
        if (getIntent() != null) {
            mTourGuide = (TourGuide) getIntent().getSerializableExtra(INTENT_EXTRA_GUIDE);
        }
        setData();

        mInviteHelper = new InviteHelper(getApplicationContext());
        mInviteHelper.setOnInviteListener(mOnInviteListener);
    }

    private void initUI() {
        mGuideCardIv = (WebImageView) findViewById(R.id.guide_card_iv);
        mGuideNameGenderTv = (TextView) findViewById(R.id.guide_name_gender_tv);
        mCityTv = (TextView) findViewById(R.id.city);
        mGoodAtScenicTv = (TextView) findViewById(R.id.good_scenic);
        mGuideHistoryTv = (TextView) findViewById(R.id.guide_history);
        mGuideHistoryView = findViewById(R.id.guide_history_layout);
        mGuideCardIdTv = (TextView) findViewById(R.id.guide_card_id);
        mDestinationEt = (EditText) findViewById(R.id.destination_et);
        mStartTimeTv = (TextView) findViewById(R.id.start_time_tv);
        mStartTimeView = findViewById(R.id.start_time_layout);
        mEndTimeTv = (TextView) findViewById(R.id.end_time_tv);
        mEndTimeView = findViewById(R.id.end_time_layout);
        mBookingBtn = (Button) findViewById(R.id.booking_btn);
        mStar1 = findViewById(R.id.guide_star1);
        mStar2 = findViewById(R.id.guide_star2);
        mStar3 = findViewById(R.id.guide_star3);
        mStar4 = findViewById(R.id.guide_star4);
        mStar5 = findViewById(R.id.guide_star5);
        mEvaluateNumTv = (TextView) findViewById(R.id.star_evaluate_num);

        mGuideHistoryView.setOnClickListener(this);
        mStartTimeView.setOnClickListener(this);
        mEndTimeView.setOnClickListener(this);
        mBookingBtn.setOnClickListener(this);
    }

    private void setData() {
        if (mTourGuide != null) {
            mGuideCardIv.setImageURI(new Uri.Builder().path(mTourGuide.getHeadUrl()).build());
            mGuideNameGenderTv.setText(mTourGuide.getUserName());
            CityItem cityItem = CityManager.getInstance().getCityById(mTourGuide.getCity());
            if (cityItem != null) {
                mCityTv.setText(cityItem.getCityName());
            }
            mGoodAtScenicTv.setText(mTourGuide.getGoodAtScenic());
            mGuideCardIdTv.setText(mTourGuide.getGuideCardId());
            mEvaluateNumTv.setText(mTourGuide.getEvaluateCount() + "次");
            setStar(mTourGuide.getEvaluateScore());
        }

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mStartTimeTv.setText(year + "/" + (month + 1 < 10 ? "0" + (month + 1) : month + 1) + "/"
                + (day < 10 ? "0" + (day) : day));
        mEndTimeTv.setText(year + "/" + (month + 1 < 10 ? "0" + (month + 1) : month + 1) + "/"
                + (day < 10 ? "0" + (day) : day));
        mStartTimeStamp = System.currentTimeMillis();
        mEndTimeStamp = System.currentTimeMillis() + 1;
    }

    private void setStar(int score) {
        if (score <= 0) {
            mStar1.setBackgroundResource(R.drawable.star_silver);
            mStar2.setBackgroundResource(R.drawable.star_silver);
            mStar3.setBackgroundResource(R.drawable.star_silver);
            mStar4.setBackgroundResource(R.drawable.star_silver);
            mStar5.setBackgroundResource(R.drawable.star_silver);
        } else if (score == 1) {
            mStar1.setBackgroundResource(R.drawable.star_gold);
            mStar2.setBackgroundResource(R.drawable.star_silver);
            mStar3.setBackgroundResource(R.drawable.star_silver);
            mStar4.setBackgroundResource(R.drawable.star_silver);
            mStar5.setBackgroundResource(R.drawable.star_silver);
        } else if (score == 2) {
            mStar1.setBackgroundResource(R.drawable.star_gold);
            mStar2.setBackgroundResource(R.drawable.star_gold);
            mStar3.setBackgroundResource(R.drawable.star_silver);
            mStar4.setBackgroundResource(R.drawable.star_silver);
            mStar5.setBackgroundResource(R.drawable.star_silver);
        } else if (score == 3) {
            mStar1.setBackgroundResource(R.drawable.star_gold);
            mStar2.setBackgroundResource(R.drawable.star_gold);
            mStar3.setBackgroundResource(R.drawable.star_gold);
            mStar4.setBackgroundResource(R.drawable.star_silver);
            mStar5.setBackgroundResource(R.drawable.star_silver);
        } else if (score == 4) {
            mStar1.setBackgroundResource(R.drawable.star_gold);
            mStar2.setBackgroundResource(R.drawable.star_gold);
            mStar3.setBackgroundResource(R.drawable.star_gold);
            mStar4.setBackgroundResource(R.drawable.star_gold);
            mStar5.setBackgroundResource(R.drawable.star_silver);
        } else {
            mStar1.setBackgroundResource(R.drawable.star_gold);
            mStar2.setBackgroundResource(R.drawable.star_gold);
            mStar3.setBackgroundResource(R.drawable.star_gold);
            mStar4.setBackgroundResource(R.drawable.star_gold);
            mStar5.setBackgroundResource(R.drawable.star_gold);
        }
    }

    @Override
    public void onDestroy() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
        if (mInviteHelper != null) {
            mInviteHelper.destroy();
            mInviteHelper = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.booking_btn:
            bookingGuide();
            break;
        case R.id.guide_history_layout:
            enterGuideHistory();
            break;
        case R.id.start_time_layout:
            selectStartTime();
            break;
        case R.id.end_time_layout:
            selectEndTime();
            break;
        }
    }

    private void bookingGuide() {
        if (SettingManager.getInstance().getUserId() <= 0) {
            if (mAlertDialog != null && mAlertDialog.isShowing()) {
                mAlertDialog.dismiss();
            }

            mAlertDialog = new AlertDialog.Builder(this).setMessage(R.string.login_dialog_message)
                    .setPositiveButton(R.string.login_dialog_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            login();
                        }
                    }).setNegativeButton(R.string.login_dialog_negative, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
            mAlertDialog.show();
            return;
        }

        if (mTourGuide != null) {
            String destination = mDestinationEt.getText().toString();
            if (TextUtils.isEmpty(destination)) {
                TipsDialog.getInstance().show(this, R.drawable.tips_fail, R.string.booking_destination_hint, true);
                return;
            }
            TipsDialog.getInstance().show(this, R.drawable.tips_loading, R.string.booking, true, false);
            mInviteHelper.invite(mTourGuide.getUserId(), destination, mStartTimeStamp, mEndTimeStamp);
        }
    }

    private OnInviteListener mOnInviteListener = new OnInviteListener() {

        @Override
        public void onInviteAll(int result) {

        }

        @Override
        public void onInvite(final int result) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TipsDialog.getInstance().dismiss();
                    if (result == InviteHelper.SUCCESS) {
                        finish();
                    } else {

                    }
                }
            });
        }

        @Override
        public void onCancelInvite(int result) {

        }
    };

    private void login() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void enterGuideHistory() {

    }

    private void selectStartTime() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        Calendar calendar = Calendar.getInstance();
        mDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year + "/" + (monthOfYear + 1 < 10 ? "0" + (monthOfYear + 1) : monthOfYear + 1) + "/"
                        + (dayOfMonth < 10 ? "0" + (dayOfMonth) : dayOfMonth);
                mStartTimeTv.setText(date);
                mStartTimeStamp = getTimestamp(year, monthOfYear, dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        mDialog.show();
    }

    private void selectEndTime() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        Calendar calendar = Calendar.getInstance();
        mDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year + "/" + (monthOfYear + 1 < 10 ? "0" + (monthOfYear + 1) : monthOfYear + 1) + "/"
                        + (dayOfMonth < 10 ? "0" + (dayOfMonth) : dayOfMonth);
                mEndTimeTv.setText(date);
                mEndTimeStamp = getTimestamp(year, monthOfYear, dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        mDialog.show();
    }

    private long getTimestamp(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTimeInMillis();
    }
}
