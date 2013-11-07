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
import com.find.guide.city.CityItem;
import com.find.guide.city.CityManager;
import com.find.guide.invite.InviteHelper;
import com.find.guide.invite.InviteHelper.OnInviteListener;
import com.find.guide.setting.SettingManager;
import com.find.guide.user.TourGuide;
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
    private TextView mTravelAgencyTv;
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

        if (getIntent() != null) {
            mTourGuide = (TourGuide) getIntent().getSerializableExtra(INTENT_EXTRA_GUIDE);
        }
        if (mTourGuide == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_booking);
        initUI();
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
        mTravelAgencyTv = (TextView) findViewById(R.id.travel_agency);

        mGuideHeaderIv.setOnClickListener(this);
        mGuideHistoryView.setOnClickListener(this);
        mStartTimeView.setOnClickListener(this);
        mEndTimeView.setOnClickListener(this);
        mBookingBtn.setOnClickListener(this);
    }

    private void setData() {
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
        mTravelAgencyTv.setText(mTourGuide.getTravelAgency());
        int star = 0;
        if (mTourGuide.getEvaluateCount() > 0) {
            star = mTourGuide.getEvaluateScore() / mTourGuide.getEvaluateCount();
        }
        setStar(star);

        if (mTourGuide.getGender() == 1) {
            mGuideGenderIv.setImageResource(R.drawable.icon_male);
        } else if (mTourGuide.getGender() == 2) {
            mGuideGenderIv.setImageResource(R.drawable.icon_female);
        } else {
            mGuideGenderIv.setImageDrawable(null);
        }

        mStartTimeStamp = System.currentTimeMillis();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(mStartTimeStamp);
        int year1 = calendar1.get(Calendar.YEAR);
        int month1 = calendar1.get(Calendar.MONTH);
        int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
        int hour1 = calendar1.get(Calendar.HOUR_OF_DAY);
        int min1 = calendar1.get(Calendar.MINUTE);
        mStartTimeTv.setText(year1 + "/" + (month1 + 1 < 10 ? "0" + (month1 + 1) : month1 + 1) + "/"
                + (day1 < 10 ? "0" + (day1) : day1) + " " + (hour1 < 10 ? "0" + (hour1) : hour1) + ":"
                + (min1 < 10 ? "0" + (min1) : min1));

        mEndTimeStamp = System.currentTimeMillis() + 60 * 60 * 1000;
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(mEndTimeStamp);
        int year2 = calendar2.get(Calendar.YEAR);
        int month2 = calendar2.get(Calendar.MONTH);
        int day2 = calendar2.get(Calendar.DAY_OF_MONTH);
        int hour2 = calendar2.get(Calendar.HOUR_OF_DAY);
        int min2 = calendar2.get(Calendar.MINUTE);
        mEndTimeTv.setText(year2 + "/" + (month2 + 1 < 10 ? "0" + (month2 + 1) : month2 + 1) + "/"
                + (day2 < 10 ? "0" + (day2) : day2) + " " + (hour2 < 10 ? "0" + (hour2) : hour2) + ":"
                + (min2 < 10 ? "0" + (min2) : min2));
    }

    private void setStar(int star) {
        if (star <= 0) {
            mStar1.setBackgroundResource(R.drawable.star_big_silver);
            mStar2.setBackgroundResource(R.drawable.star_big_silver);
            mStar3.setBackgroundResource(R.drawable.star_big_silver);
            mStar4.setBackgroundResource(R.drawable.star_big_silver);
            mStar5.setBackgroundResource(R.drawable.star_big_silver);
        } else if (star == 1) {
            mStar1.setBackgroundResource(R.drawable.star_big_gold);
            mStar2.setBackgroundResource(R.drawable.star_big_silver);
            mStar3.setBackgroundResource(R.drawable.star_big_silver);
            mStar4.setBackgroundResource(R.drawable.star_big_silver);
            mStar5.setBackgroundResource(R.drawable.star_big_silver);
        } else if (star == 2) {
            mStar1.setBackgroundResource(R.drawable.star_big_gold);
            mStar2.setBackgroundResource(R.drawable.star_big_gold);
            mStar3.setBackgroundResource(R.drawable.star_big_silver);
            mStar4.setBackgroundResource(R.drawable.star_big_silver);
            mStar5.setBackgroundResource(R.drawable.star_big_silver);
        } else if (star == 3) {
            mStar1.setBackgroundResource(R.drawable.star_big_gold);
            mStar2.setBackgroundResource(R.drawable.star_big_gold);
            mStar3.setBackgroundResource(R.drawable.star_big_gold);
            mStar4.setBackgroundResource(R.drawable.star_big_silver);
            mStar5.setBackgroundResource(R.drawable.star_big_silver);
        } else if (star == 4) {
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
        case R.id.guide_header_iv:
            viewLarge();
            break;
        }
    }

    private void viewLarge() {
        if (TextUtils.isEmpty(mTourGuide.getHeadUrl()))
            return;
        Intent intent = new Intent(this, ViewLargeActivity.class);
        intent.putExtra(ViewLargeActivity.INTENT_EXTRA_URL, mTourGuide.getHeadUrl());
        startActivity(intent);
    }

    private void bookingGuide() {
        if (SettingManager.getInstance().getUserId() <= 0) {
            showLoginDialog();
            return;
        }

        String destination = mDestinationEt.getText().toString();
        if (TextUtils.isEmpty(destination) || TextUtils.isEmpty(destination.trim())) {
            TipsDialog.getInstance().show(this, R.drawable.tips_fail, R.string.booking_destination_hint, true);
        } else if (mTourGuide.getUserId() == SettingManager.getInstance().getUserId()) {
            TipsDialog.getInstance().show(this, R.drawable.tips_fail, R.string.booking_cannot_booking_yourself, true);
        } else if (mStartTimeStamp <= 0) {
            TipsDialog.getInstance().show(this, R.drawable.tips_fail, "请输入开始时间", true);
        } else if (mEndTimeStamp <= 0) {
            TipsDialog.getInstance().show(this, R.drawable.tips_fail, "请输入结束时间", true);
        } else {
            destination = destination.trim();
            if (destination.length() > 20)
                destination = destination.substring(0, 20);
            TipsDialog.getInstance().show(this, R.drawable.tips_loading, R.string.in_booking, true, false);
            mInviteHelper.invite(mTourGuide.getUserId(), destination.trim(), mStartTimeStamp, mEndTimeStamp);
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
                if (isDateTimeValid(dateSelected.getTime())) {
                    String date1 = year + "/" + (monthNumber + 1 < 10 ? "0" + (monthNumber + 1) : monthNumber + 1)
                            + "/" + (date < 10 ? "0" + (date) : date) + " " + (hour24 < 10 ? "0" + hour24 : hour24)
                            + ":" + (min < 10 ? "0" + min : min);
                    mStartTimeTv.setText(date1);
                    mStartTimeStamp = dateSelected.getTime();
                } else {
                    TipsDialog.getInstance().show(BookingActivity.this, R.drawable.tips_fail, "不能输入过去的时间", true);
                }

                if (mEndTimeStamp < mStartTimeStamp + 30 * 60 * 1000) {
                    mEndTimeStamp = 0;
                    mEndTimeTv.setText("");
                }
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
                if (!isDateTimeValid(dateSelected.getTime())
                        || (dateSelected.getTime() < mStartTimeStamp + 30 * 60 * 1000)) {
                    TipsDialog.getInstance().show(BookingActivity.this, R.drawable.tips_fail, "结束时间需要至少比开始时间晚30分钟",
                            true);
                } else {
                    String date1 = year + "/" + (monthNumber + 1 < 10 ? "0" + (monthNumber + 1) : monthNumber + 1)
                            + "/" + (date < 10 ? "0" + (date) : date) + " " + (hour24 < 10 ? "0" + hour24 : hour24)
                            + ":" + (min < 10 ? "0" + min : min);
                    mEndTimeTv.setText(date1);
                    mEndTimeStamp = dateSelected.getTime();
                }
            }

            @Override
            public void onCancel() {

            }
        });
        mDateTimePickerDialog.showDialog();
    }

    private boolean isDateTimeValid(long time) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.SECOND, 0);
        return time >= c.getTimeInMillis();
    }

    private void dismissDateTimePicker() {
        if (mDateTimePickerDialog != null) {
            mDateTimePickerDialog.dismissDialog();
            mDateTimePickerDialog = null;
        }
    }

}
