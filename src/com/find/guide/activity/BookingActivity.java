package com.find.guide.activity;

import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.find.guide.R;
import com.find.guide.model.CityItem;
import com.find.guide.model.TourGuide;
import com.find.guide.model.helper.CityManager;
import com.find.guide.model.helper.InviteHelper;
import com.find.guide.model.helper.InviteHelper.OnInviteListener;
import com.find.guide.setting.SettingManager;
import com.find.guide.view.DateTimePickerDialog;
import com.find.guide.view.TipsDialog;
import com.find.guide.view.DateTimePickerDialog.ICustomDateTimeListener;
import com.plugin.common.view.WebImageView;

public class BookingActivity extends BaseActivity implements OnClickListener {

    public static final String INTENT_EXTRA_GUIDE = "intent_extra_guide";

    private TourGuide mTourGuide = null;

    private long mStartTimeStamp = 0;
    private long mEndTimeStamp = 0;

    private WebImageView mGuideHeaderIv;
    private TextView mGuideNameTv;
    private ImageView mGuideGenderIv;
    private TextView mCityTv;
    private TextView mGoodAtScenicTv;
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

    private DateTimePickerDialog mDateTimePickerDialog;

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
        mGuideHeaderIv = (WebImageView) findViewById(R.id.guide_header_iv);
        mGuideNameTv = (TextView) findViewById(R.id.guide_name_tv);
        mGuideGenderIv = (ImageView) findViewById(R.id.guide_gender_iv);
        mCityTv = (TextView) findViewById(R.id.city);
        mGoodAtScenicTv = (TextView) findViewById(R.id.good_scenic);
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
            mGuideHeaderIv.setImageURI(new Uri.Builder().path(mTourGuide.getHeadUrl()).build());
            mGuideNameTv.setText(mTourGuide.getUserName());
            CityItem cityItem = CityManager.getInstance().getCityById(mTourGuide.getCity());
            if (cityItem != null) {
                mCityTv.setText(cityItem.getCityName());
            }
            mGoodAtScenicTv.setText(mTourGuide.getGoodAtScenic());
            mGuideCardIdTv.setText(mTourGuide.getGuideCardId());
            if (mTourGuide.getEvaluateCount() > 0) {
                mEvaluateNumTv.setText(mTourGuide.getEvaluateCount() + "次评价");
            } else {
                mEvaluateNumTv.setText("暂无评价");
            }
            float rating = 0f;
            if (mTourGuide.getEvaluateCount() > 0) {
                rating = mTourGuide.getEvaluateScore() / mTourGuide.getEvaluateCount();
            }
            setStar(rating);

            if (mTourGuide.getGender() == 1) {
                mGuideGenderIv.setImageResource(R.drawable.icon_male);
            } else if (mTourGuide.getGender() == 2) {
                mGuideGenderIv.setImageResource(R.drawable.icon_female);
            } else {
                mGuideGenderIv.setImageDrawable(null);
            }
        }

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        mStartTimeTv.setText(year + "/" + (month + 1 < 10 ? "0" + (month + 1) : month + 1) + "/"
                + (day < 10 ? "0" + (day) : day) + " " + (hour < 10 ? "0" + (hour) : hour) + ":"
                + (min < 10 ? "0" + (min) : min));
        mEndTimeTv.setText(year + "/" + (month + 1 < 10 ? "0" + (month + 1) : month + 1) + "/"
                + (day < 10 ? "0" + (day) : day) + " " + (hour < 10 ? "0" + (hour) : hour) + ":"
                + (min < 10 ? "0" + (min) : min));
        mStartTimeStamp = System.currentTimeMillis();
        mEndTimeStamp = System.currentTimeMillis() + 1;
    }

    private void setStar(float score) {
        if (score < 0.2) {
            mStar1.setBackgroundResource(R.drawable.star_big_silver);
            mStar2.setBackgroundResource(R.drawable.star_big_silver);
            mStar3.setBackgroundResource(R.drawable.star_big_silver);
            mStar4.setBackgroundResource(R.drawable.star_big_silver);
            mStar5.setBackgroundResource(R.drawable.star_big_silver);
        } else if (score < 0.4) {
            mStar1.setBackgroundResource(R.drawable.star_big_gold);
            mStar2.setBackgroundResource(R.drawable.star_big_silver);
            mStar3.setBackgroundResource(R.drawable.star_big_silver);
            mStar4.setBackgroundResource(R.drawable.star_big_silver);
            mStar5.setBackgroundResource(R.drawable.star_big_silver);
        } else if (score < 0.6) {
            mStar1.setBackgroundResource(R.drawable.star_big_gold);
            mStar2.setBackgroundResource(R.drawable.star_big_gold);
            mStar3.setBackgroundResource(R.drawable.star_big_silver);
            mStar4.setBackgroundResource(R.drawable.star_big_silver);
            mStar5.setBackgroundResource(R.drawable.star_big_silver);
        } else if (score < 0.8) {
            mStar1.setBackgroundResource(R.drawable.star_big_gold);
            mStar2.setBackgroundResource(R.drawable.star_big_gold);
            mStar3.setBackgroundResource(R.drawable.star_big_gold);
            mStar4.setBackgroundResource(R.drawable.star_big_silver);
            mStar5.setBackgroundResource(R.drawable.star_big_silver);
        } else if (score < 0.95) {
            mStar1.setBackgroundResource(R.drawable.star_big_gold);
            mStar2.setBackgroundResource(R.drawable.star_big_gold);
            mStar3.setBackgroundResource(R.drawable.star_big_gold);
            mStar4.setBackgroundResource(R.drawable.star_big_gold);
            mStar5.setBackgroundResource(R.drawable.star_big_silver);
        } else {
            mStar1.setBackgroundResource(R.drawable.star_big_gold);
            mStar2.setBackgroundResource(R.drawable.star_big_gold);
            mStar3.setBackgroundResource(R.drawable.star_big_gold);
            mStar4.setBackgroundResource(R.drawable.star_big_gold);
            mStar5.setBackgroundResource(R.drawable.star_big_gold);
        }
    }

    @Override
    public void onDestroy() {
        dismissDateTimePicker();
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
        if (mInviteHelper != null) {
            mInviteHelper.destroy();
            mInviteHelper = null;
        }
        TipsDialog.getInstance().dismiss();
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
            showLoginDialog();
            return;
        }

        if (mTourGuide != null) {
            String destination = mDestinationEt.getText().toString();
            if (TextUtils.isEmpty(destination)) {
                TipsDialog.getInstance().show(this, R.drawable.tips_fail, R.string.booking_destination_hint, true);
                return;
            }
            if (mTourGuide.getUserId() == SettingManager.getInstance().getUserId()) {
                TipsDialog.getInstance().show(this, R.drawable.tips_fail, R.string.booking_cannot_booking_yourself,
                        true);
                return;
            }
            TipsDialog.getInstance().show(this, R.drawable.tips_loading, R.string.in_booking, true, false);
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
                        TipsDialog.getInstance().show(BookingActivity.this, R.drawable.tips_saved,
                                R.string.booking_success, true);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);
                    } else if (result == InviteHelper.FAILED) {
                        TipsDialog.getInstance().show(BookingActivity.this, R.drawable.tips_fail,
                                R.string.booking_failed, true);
                    }
                }
            });
        }

        @Override
        public void onCancelInvite(int result) {

        }
    };

    private void showLoginDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }

        mAlertDialog = new AlertDialog.Builder(this).setMessage(R.string.login_dialog_message)
                .setPositiveButton(R.string.login_dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(BookingActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton(R.string.login_dialog_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        mAlertDialog.show();
    }

    private void enterGuideHistory() {
        if (SettingManager.getInstance().getUserId() > 0) {
            Intent intent = new Intent(this, GuideRecordActivity.class);
            intent.putExtra(GuideRecordActivity.EXTRA_INTENT_GUIDE_ID, mTourGuide.getUserId());
            startActivity(intent);
        } else {
            showLoginDialog();
        }
    }

    private void selectStartTime() {
        dismissDateTimePicker();
        mDateTimePickerDialog = new DateTimePickerDialog(this, new ICustomDateTimeListener() {

            @Override
            public void onSet(Calendar calendarSelected, Date dateSelected, int year, String monthFullName,
                    String monthShortName, int monthNumber, int date, String weekDayFullName, String weekDayShortName,
                    int hour24, int hour12, int min, int sec, String AM_PM) {
                String date1 = year + "/" + (monthNumber + 1 < 10 ? "0" + (monthNumber + 1) : monthNumber + 1) + "/"
                        + (date < 10 ? "0" + (date) : date) + " " + (hour24 < 10 ? "0" + hour24 : hour24) + ":"
                        + (min < 10 ? "0" + min : min);
                mEndTimeTv.setText(date1);
                mEndTimeStamp = dateSelected.getTime();
            }

            @Override
            public void onCancel() {

            }
        });
        mDateTimePickerDialog.showDialog();
    }

    private void selectEndTime() {
        dismissDateTimePicker();
        mDateTimePickerDialog = new DateTimePickerDialog(this, new ICustomDateTimeListener() {

            @Override
            public void onSet(Calendar calendarSelected, Date dateSelected, int year, String monthFullName,
                    String monthShortName, int monthNumber, int date, String weekDayFullName, String weekDayShortName,
                    int hour24, int hour12, int min, int sec, String AM_PM) {
                String date1 = year + "/" + (monthNumber + 1 < 10 ? "0" + (monthNumber + 1) : monthNumber + 1) + "/"
                        + (date < 10 ? "0" + (date) : date) + " " + (hour24 < 10 ? "0" + hour24 : hour24) + ":"
                        + (min < 10 ? "0" + min : min);
                mEndTimeTv.setText(date1);
                mEndTimeStamp = dateSelected.getTime();
            }

            @Override
            public void onCancel() {

            }
        });
        mDateTimePickerDialog.showDialog();
    }

    private void dismissDateTimePicker() {
        if (mDateTimePickerDialog != null) {
            mDateTimePickerDialog.dismissDialog();
            mDateTimePickerDialog = null;
        }
    }

}
