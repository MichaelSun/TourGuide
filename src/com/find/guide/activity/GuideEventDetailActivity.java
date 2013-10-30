package com.find.guide.activity;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
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
import android.widget.TextView;

import com.find.guide.R;
import com.find.guide.guide.GuideEvent;
import com.find.guide.guide.GuideHelper;
import com.find.guide.guide.GuideHelper.OnAcceptedListener;
import com.find.guide.guide.GuideHelper.OnRefusedListener;
import com.find.guide.view.TipsDialog;

public class GuideEventDetailActivity extends BaseActivity implements OnClickListener {

    public static final String INTENT_EXTRA_GUIDE_EVENT_OBJ = "intent_extra_guide_event_obj";
    private GuideEvent mGuideEvent = null;

    private TextView mTouristName;
    private View mPhoneLayout;
    private TextView mPhoneTv;
    private TextView mDestinationTv;
    private TextView mStartTimeTv;
    private TextView mEndTimeTv;
    private Button mButton1;
    private Button mButton2;

    private GuideHelper mGuideHelper = null;

    private Dialog mAlertDialog;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getIntent() != null) {
                mGuideEvent = (GuideEvent) getIntent().getSerializableExtra(INTENT_EXTRA_GUIDE_EVENT_OBJ);
            }
        } else {
            mGuideEvent = (GuideEvent) savedInstanceState.getSerializable(INTENT_EXTRA_GUIDE_EVENT_OBJ);
        }

        if (mGuideEvent == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_guide_event_detail);
        initUI();

        mGuideHelper = new GuideHelper(getBaseContext());
    }

    private void initUI() {
        mTouristName = (TextView) findViewById(R.id.tourist_name);
        mPhoneLayout = findViewById(R.id.phone_layout);
        mPhoneTv = (TextView) findViewById(R.id.phone_tv);
        mDestinationTv = (TextView) findViewById(R.id.destination_tv);
        mStartTimeTv = (TextView) findViewById(R.id.start_time_tv);
        mEndTimeTv = (TextView) findViewById(R.id.end_time_tv);
        mButton1 = (Button) findViewById(R.id.button1);
        mButton2 = (Button) findViewById(R.id.button2);

        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mPhoneLayout.setOnClickListener(this);

        mTouristName.setText(mGuideEvent.getUserName());
        mPhoneTv.setText(mGuideEvent.getMobile());
        mDestinationTv.setText(mGuideEvent.getScenic());
        mStartTimeTv.setText(getTimeStr(mGuideEvent.getStartTime()));
        mEndTimeTv.setText(getTimeStr(mGuideEvent.getEndTime()));

        switch (mGuideEvent.getEventStatus()) {
        case GuideEvent.EVENT_STATUS_WAITING:
            mButton1.setVisibility(View.VISIBLE);
            mButton2.setVisibility(View.VISIBLE);
            mButton1.setText(R.string.accept);
            mButton2.setText(R.string.refuse);
            break;
        default:
            mButton1.setVisibility(View.GONE);
            mButton2.setVisibility(View.GONE);
            break;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(INTENT_EXTRA_GUIDE_EVENT_OBJ, mGuideEvent);
    }

    @Override
    protected void onDestroy() {
        if (mGuideHelper != null) {
            mGuideHelper.destroy();
            mGuideHelper = null;
        }
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
        TipsDialog.getInstance().dismiss();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.button1:
            if (mGuideEvent.getEventStatus() == GuideEvent.EVENT_STATUS_WAITING) {
                accept();
            }
            break;
        case R.id.button2:
            if (mGuideEvent.getEventStatus() == GuideEvent.EVENT_STATUS_WAITING) {
                refuse();
            }
            break;
        case R.id.phone_layout:
            dialTourist();
            break;
        }
    }

    private void dialTourist() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        if (TextUtils.isEmpty(mGuideEvent.getMobile())) {
            return;
        }

        String msg = getString(R.string.call_dialog_message);
        if (msg != null) {
            msg = String.format(msg, mGuideEvent.getMobile());
        }
        mAlertDialog = new AlertDialog.Builder(this).setMessage(msg)
                .setPositiveButton(R.string.call_dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mGuideEvent.getMobile()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {

                        }
                    }
                }).setNegativeButton(R.string.call_dialog_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        mAlertDialog.show();
    }

    private String getTimeStr(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        return year + "/" + (month + 1 < 10 ? "0" + (month + 1) : month + 1) + "/" + (day < 10 ? "0" + (day) : day)
                + " " + (hour < 10 ? "0" + (hour) : hour) + ":" + (min < 10 ? "0" + (min) : min);
    }

    private void accept() {
        TipsDialog.getInstance().show(this, R.drawable.tips_loading, R.string.accepting_invite, true, false);
        mGuideHelper.accept(mGuideEvent.getEventId(), mGuideEvent.getUserId(), mOnAcceptedListener);
    }

    private void refuse() {
        TipsDialog.getInstance().show(this, R.drawable.tips_loading, R.string.refusing_invite, true, false);
        mGuideHelper.refuse(mGuideEvent.getEventId(), mGuideEvent.getUserId(), mOnRefusedListener);
    }

    OnAcceptedListener mOnAcceptedListener = new OnAcceptedListener() {

        @Override
        public void onAccepted(final int result) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TipsDialog.getInstance().dismiss();
                    if (result == GuideHelper.SUCCESS) {
                        TipsDialog.getInstance().show(GuideEventDetailActivity.this, R.drawable.tips_saved, "", true);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setResult(RESULT_OK);
                                finish();
                            }
                        }, 1000);
                    } else if (result == GuideHelper.FAILED) {
                        TipsDialog.getInstance().show(GuideEventDetailActivity.this, R.drawable.tips_fail,
                                R.string.accept_invite_failed, true);
                    }
                }
            });
        }
    };

    OnRefusedListener mOnRefusedListener = new OnRefusedListener() {

        @Override
        public void onRefused(final int result) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TipsDialog.getInstance().dismiss();
                    if (result == GuideHelper.SUCCESS) {
                        TipsDialog.getInstance().show(GuideEventDetailActivity.this, R.drawable.tips_saved, "", true);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setResult(RESULT_OK);
                                finish();
                            }
                        }, 1000);
                    } else if (result == GuideHelper.FAILED) {
                        TipsDialog.getInstance().show(GuideEventDetailActivity.this, R.drawable.tips_fail,
                                R.string.refuse_invite_failed, true);
                    }
                }
            });
        }
    };
}
