package com.find.guide.activity;

import java.util.Calendar;

import com.find.guide.R;
import com.find.guide.config.AppRuntime;
import com.find.guide.model.helper.InviteHelper;
import com.find.guide.model.helper.InviteHelper.OnInviteListener;
import com.find.guide.setting.SettingManager;
import com.find.guide.view.TipsDialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class BroadcastActivity extends BaseActivity implements OnClickListener {

    private EditText mDestinationEt;
    private RadioGroup mGenderRadio;
    private TextView mStartTimeTv;
    private View mStartTimeView;
    private TextView mEndTimeTv;
    private View mEndTimeView;
    private Button mBroadcastBtn;

    private DatePickerDialog mDateDialog;
    private AlertDialog mAlertDialog;

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
        if (mDateDialog != null && mDateDialog.isShowing()) {
            mDateDialog.dismiss();
            mDateDialog = null;
        }
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
        if (TextUtils.isEmpty(destination)) {
            TipsDialog.getInstance().show(this, R.drawable.tips_fail, R.string.booking_destination_hint, true);
            return;
        }
        int gender = 0;
        if (mGenderRadio.getCheckedRadioButtonId() == R.id.male_radio) {
            gender = 1;
        } else if (mGenderRadio.getCheckedRadioButtonId() == R.id.female_radio) {
            gender = 2;
        }

        TipsDialog.getInstance().show(this, R.drawable.tips_loading, R.string.sending_broadcast, true, false);
        mInviteHelper.inviteAll(destination, mStartTimeStamp, mEndTimeStamp, AppRuntime.gLocation, gender);
    }

    private OnInviteListener mOnInviteListener = new OnInviteListener() {
        @Override
        public void onInviteAll(final int result) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TipsDialog.getInstance().dismiss();
                    if (result == InviteHelper.SUCCESS) {
                        finish();
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

    private void selectStartTime() {
        if (mDateDialog != null) {
            mDateDialog.dismiss();
            mDateDialog = null;
        }
        Calendar calendar = Calendar.getInstance();
        mDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year + "/" + (monthOfYear + 1 < 10 ? "0" + (monthOfYear + 1) : monthOfYear + 1) + "/"
                        + (dayOfMonth < 10 ? "0" + (dayOfMonth) : dayOfMonth);
                mStartTimeTv.setText(date);
                mStartTimeStamp = getTimestamp(year, monthOfYear, dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        mDateDialog.show();
    }

    private void selectEndTime() {
        if (mDateDialog != null) {
            mDateDialog.dismiss();
            mDateDialog = null;
        }
        Calendar calendar = Calendar.getInstance();
        mDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year + "/" + (monthOfYear + 1 < 10 ? "0" + (monthOfYear + 1) : monthOfYear + 1) + "/"
                        + (dayOfMonth < 10 ? "0" + (dayOfMonth) : dayOfMonth);
                mEndTimeTv.setText(date);
                mEndTimeStamp = getTimestamp(year, monthOfYear, dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        mDateDialog.show();
    }

    private long getTimestamp(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    private void login() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
