package com.find.guide.activity;

import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.find.guide.R;
import com.find.guide.config.AppRuntime;
import com.find.guide.invite.InviteHelper;
import com.find.guide.invite.InviteHelper.OnInviteListener;
import com.find.guide.setting.SettingManager;
import com.find.guide.view.DateTimePickerDialog;
import com.find.guide.view.DateTimePickerDialog.ICustomDateTimeListener;
import com.find.guide.view.TipsDialog;

public class BroadcastActivity extends BaseActivity implements OnClickListener {

    private EditText mDestinationEt;
    private RadioGroup mGenderRadio;
    private TextView mStartTimeTv;
    private View mStartTimeView;
    private TextView mEndTimeTv;
    private View mEndTimeView;
    private Button mBroadcastBtn;

    private AlertDialog mAlertDialog;
    private DateTimePickerDialog mDateTimePickerDialog;

    private InviteHelper mInviteHelper;

    private long mStartTimeStamp = 0;
    private long mEndTimeStamp = 0;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_broadcast);
        initUI();
        initDate();

        mInviteHelper = new InviteHelper(getApplicationContext());
        mInviteHelper.setOnInviteListener(mOnInviteListener);
    }

    private void initUI() {
        mDestinationEt = (EditText) findViewById(R.id.destination_et);
        mGenderRadio = (RadioGroup) findViewById(R.id.gender_radio);
        mStartTimeTv = (TextView) findViewById(R.id.start_time_tv);
        mStartTimeView = findViewById(R.id.start_time_layout);
        mEndTimeTv = (TextView) findViewById(R.id.end_time_tv);
        mEndTimeView = findViewById(R.id.end_time_layout);
        mBroadcastBtn = (Button) findViewById(R.id.broadcast_btn);

        mBroadcastBtn.setOnClickListener(this);
        mStartTimeView.setOnClickListener(this);
        mEndTimeView.setOnClickListener(this);

    }

    private void initDate() {
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

    @Override
    protected void onDestroy() {
        if (mInviteHelper != null) {
            mInviteHelper.destroy();
            mInviteHelper = null;
        }
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
        dismissDateTimePicker();
        TipsDialog.getInstance().dismiss();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.start_time_layout:
            selectStartTime();
            break;
        case R.id.end_time_layout:
            selectEndTime();
            break;
        case R.id.broadcast_btn:
            sendBroadcast();
            break;
        }
    }

    private void sendBroadcast() {
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

        String destination = mDestinationEt.getText().toString();
        if (TextUtils.isEmpty(destination) || TextUtils.isEmpty(destination.trim())) {
            TipsDialog.getInstance().show(this, R.drawable.tips_fail, R.string.booking_destination_hint, true);
        } else if (mStartTimeStamp <= 0) {
            TipsDialog.getInstance().show(this, R.drawable.tips_fail, "请输入开始时间", true);
        } else if (mEndTimeStamp <= 0) {
            TipsDialog.getInstance().show(this, R.drawable.tips_fail, "请输入结束时间", true);
        } else {
            int gender = 0;
            if (mGenderRadio.getCheckedRadioButtonId() == R.id.male_radio) {
                gender = 1;
            } else if (mGenderRadio.getCheckedRadioButtonId() == R.id.female_radio) {
                gender = 2;
            }
            destination = destination.trim();
            if (destination.length() > 20)
                destination = destination.substring(0, 20);
            TipsDialog.getInstance().show(this, R.drawable.tips_loading, R.string.sending_broadcast, true, false);
            mInviteHelper.inviteAll(destination.trim(), mStartTimeStamp, mEndTimeStamp, AppRuntime.gLocation, gender);
        }
    }

    private OnInviteListener mOnInviteListener = new OnInviteListener() {
        @Override
        public void onInviteAll(final int result) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TipsDialog.getInstance().dismiss();
                    if (result == InviteHelper.SUCCESS) {
                        broadcastSuccess();
                    } else if (result == InviteHelper.FAILED) {
                        TipsDialog.getInstance().show(BroadcastActivity.this, R.drawable.tips_fail,
                                R.string.broadcast_failed, true);
                    }
                }
            });
        }

        @Override
        public void onInvite(final int result) {
        }

        @Override
        public void onCancelInvite(int result) {
        }
    };

    private void broadcastSuccess() {
        TipsDialog.getInstance().show(BroadcastActivity.this, R.drawable.tips_saved, R.string.broadcast_success, true);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
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
                    TipsDialog.getInstance().show(BroadcastActivity.this, R.drawable.tips_fail, "不能输入过去的时间", true);
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
                    TipsDialog.getInstance().show(BroadcastActivity.this, R.drawable.tips_fail, "结束时间需要至少比开始时间晚30分钟",
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

    private void login() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
