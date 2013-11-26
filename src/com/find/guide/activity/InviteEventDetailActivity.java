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
import android.widget.EditText;
import android.widget.TextView;

import com.find.guide.R;
import com.find.guide.invite.InviteEvent;
import com.find.guide.invite.InviteHelper;
import com.find.guide.invite.InviteHelper.OnInviteListener;
import com.find.guide.invite.InviteHelper.OnSetSatisfactionListener;
import com.find.guide.view.TipsDialog;

public class InviteEventDetailActivity extends BaseActivity implements OnClickListener {

    public static final String INTENT_EXTRA_INVITE_EVENT_OBJ = "intent_extra_invite_event_obj";

    private InviteEvent mInviteEvent = null;

    private View mGuideNameLayout;
    private View mPhoneLayout;
    private View mDestinationLayout;
    private TextView mGuideName;
    private TextView mPhoneTv;
    private TextView mDestinationTv;
    private TextView mStartTimeTv;
    private TextView mEndTimeTv;
    private View mEndTimeLayout;
    private View mSatisfactionLayout;
    private EditText mSatisfactionEt;
    private TextView mSatisfactionTv;
    private Button mButton1;
    private Button mButton2;

    private InviteHelper mInviteHelper = null;

    private Dialog mAlertDialog;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getIntent() != null) {
                mInviteEvent = (InviteEvent) getIntent().getSerializableExtra(INTENT_EXTRA_INVITE_EVENT_OBJ);
            }
        } else {
            mInviteEvent = (InviteEvent) savedInstanceState.getSerializable(INTENT_EXTRA_INVITE_EVENT_OBJ);
        }

        if (mInviteEvent == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_invite_event_detail);
        initUI();

        mInviteHelper = new InviteHelper(getApplicationContext());
        mInviteHelper.setOnInviteListener(mOnInviteListener);
    }

    private void initUI() {
        mGuideNameLayout = findViewById(R.id.guide_layout);
        mDestinationLayout = findViewById(R.id.destination_layout);
        mPhoneLayout = findViewById(R.id.phone_layout);
        mPhoneTv = (TextView) findViewById(R.id.phone_tv);
        mGuideName = (TextView) findViewById(R.id.guide_name);
        mDestinationTv = (TextView) findViewById(R.id.destination_tv);
        mStartTimeTv = (TextView) findViewById(R.id.start_time_tv);
        mEndTimeTv = (TextView) findViewById(R.id.end_time_tv);
        mEndTimeLayout = findViewById(R.id.end_time_layout);
        mSatisfactionLayout = findViewById(R.id.statisfaction_layout);
        mSatisfactionEt = (EditText) findViewById(R.id.satisfaction_et);
        mSatisfactionTv = (TextView) findViewById(R.id.satisfaction_tv);
        mButton1 = (Button) findViewById(R.id.button1);
        mButton2 = (Button) findViewById(R.id.button2);

        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mPhoneLayout.setOnClickListener(this);
        mGuideNameLayout.setOnClickListener(this);

        mGuideName.setText(mInviteEvent.getGuideName());
        mPhoneTv.setText(mInviteEvent.getMobile());
        mDestinationTv.setText(mInviteEvent.getScenic());
        mStartTimeTv.setText(getTimeStr(mInviteEvent.getStartTime()));
        mEndTimeTv.setText(getTimeStr(mInviteEvent.getEndTime()));

        if (mInviteEvent.getEventType() == InviteEvent.EVENT_TYPE_BROADCASR
                && (mInviteEvent.getEventStatus() == InviteEvent.EVENT_STATUS_BOOKING || mInviteEvent.getEventStatus() == InviteEvent.EVENT_STATUS_CANCELED)) {
            mGuideNameLayout.setVisibility(View.GONE);
            mPhoneLayout.setVisibility(View.GONE);
            mDestinationLayout.setBackgroundResource(R.drawable.bg_top);
        } else {
            mGuideNameLayout.setVisibility(View.VISIBLE);
            mPhoneLayout.setVisibility(View.VISIBLE);
            mDestinationLayout.setBackgroundResource(R.drawable.bg_middle);
        }

        if (mInviteEvent.getEventStatus() == InviteEvent.EVENT_STATUS_ACCEPTED) {
            mEndTimeLayout.setBackgroundResource(R.drawable.bg_middle);
            mSatisfactionLayout.setVisibility(View.VISIBLE);
            mSatisfactionEt.setVisibility(View.VISIBLE);
            mSatisfactionTv.setVisibility(View.GONE);
        } else if (mInviteEvent.getEventStatus() == InviteEvent.EVENT_STATUS_EVALUATE) {
            mEndTimeLayout.setBackgroundResource(R.drawable.bg_middle);
            mSatisfactionLayout.setVisibility(View.VISIBLE);
            mSatisfactionEt.setVisibility(View.GONE);
            mSatisfactionTv.setVisibility(View.VISIBLE);
            String satisfaction = "";
            if (mInviteEvent.getSatisfaction() == InviteEvent.SATISFACTION_GOOD) {
                satisfaction = "(满意) ";
            } else if (mInviteEvent.getSatisfaction() == InviteEvent.SATISFACTION_BAD) {
                satisfaction = "(不满意) ";
            }
            if (!TextUtils.isEmpty(mInviteEvent.getSaContent())) {
                satisfaction += mInviteEvent.getSaContent();
            }
            mSatisfactionTv.setText(satisfaction);
        } else {
            mEndTimeLayout.setBackgroundResource(R.drawable.bg_bottom);
            mSatisfactionLayout.setVisibility(View.GONE);
        }

        switch (mInviteEvent.getEventStatus()) {
        case InviteEvent.EVENT_STATUS_BOOKING:
            mButton1.setVisibility(View.VISIBLE);
            mButton2.setVisibility(View.GONE);
            mButton1.setText(R.string.cancel);
            break;
        case InviteEvent.EVENT_STATUS_ACCEPTED:
            mButton1.setVisibility(View.VISIBLE);
            mButton2.setVisibility(View.VISIBLE);
            mButton1.setText(R.string.satisfaction_good);
            mButton2.setText(R.string.satisfaction_bad);
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
        outState.putSerializable(INTENT_EXTRA_INVITE_EVENT_OBJ, mInviteEvent);
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
        TipsDialog.getInstance().dismiss();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.button1:
            if (mInviteEvent.getEventStatus() == InviteEvent.EVENT_STATUS_BOOKING) {
                cancel();
            } else if (mInviteEvent.getEventStatus() == InviteEvent.EVENT_STATUS_ACCEPTED) {
                setSatisfaction(1);
            }
            break;
        case R.id.button2:
            if (mInviteEvent.getEventStatus() == InviteEvent.EVENT_STATUS_ACCEPTED) {
                setSatisfaction(2);
            }
            break;
        case R.id.phone_layout:
            contactGuide();
            break;
        case R.id.guide_layout:
            guideProfile();
            break;
        }
    }

    private void guideProfile() {
        Intent intent = new Intent(this, GuideProfileActivity.class);
        intent.putExtra(GuideProfileActivity.INTENT_EXTRA_GUIDE_ID, mInviteEvent.getGuideId());
        startActivity(intent);
    }

    private void contactGuide() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        if (TextUtils.isEmpty(mInviteEvent.getMobile())) {
            return;
        }

        String[] items = new String[] { "打电话", "发短信" };
        mAlertDialog = new AlertDialog.Builder(this).setTitle("联系导游")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                                        + mInviteEvent.getMobile()));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {

                            }
                        } else if (which == 1) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"
                                        + mInviteEvent.getMobile()));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {

                            }
                        }
                    }
                }).create();
        mAlertDialog.show();
    }

    private void cancel() {
        TipsDialog.getInstance().show(this, R.drawable.tips_loading, R.string.in_cancel, true, false);
        mInviteHelper.cancelInvite(mInviteEvent.getEventId(), mInviteEvent.getGuideId());
    }

    OnInviteListener mOnInviteListener = new OnInviteListener() {

        @Override
        public void onInviteAll(int result) {
        }

        @Override
        public void onInvite(int result) {
        }

        @Override
        public void onCancelInvite(final int result) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TipsDialog.getInstance().dismiss();
                    if (result == InviteHelper.SUCCESS) {
                        TipsDialog.getInstance().show(InviteEventDetailActivity.this, R.drawable.tips_saved, "", true);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setResult(RESULT_OK);
                                finish();
                            }
                        }, 1000);
                    } else if (result == InviteHelper.FAILED) {
                        TipsDialog.getInstance().show(InviteEventDetailActivity.this, R.drawable.tips_fail,
                                R.string.cancel_invite_failed, true);
                    }

                }
            });
        }
    };

    private void setSatisfaction(int satisfaction) {
        if (mInviteEvent != null) {
            String content = mSatisfactionEt.getText().toString();
            TipsDialog.getInstance().show(this, R.drawable.tips_loading, R.string.in_statisfaction, true, false);
            mInviteHelper.setSatisfaction(mInviteEvent.getEventId(), mInviteEvent.getGuideId(), satisfaction, content,
                    mOnSetStatisfactionListener);
        }
    }

    OnSetSatisfactionListener mOnSetStatisfactionListener = new OnSetSatisfactionListener() {
        @Override
        public void onSetSatisfaction(final int result) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TipsDialog.getInstance().dismiss();
                    if (result == InviteHelper.SUCCESS) {
                        TipsDialog.getInstance().show(InviteEventDetailActivity.this, R.drawable.tips_saved, "", true);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setResult(RESULT_OK);
                                finish();
                            }
                        }, 1000);
                    } else if (result == InviteHelper.FAILED) {
                        TipsDialog.getInstance().show(InviteEventDetailActivity.this, R.drawable.tips_fail,
                                R.string.satisfaction_failed, true);
                    }
                }
            });
        }
    };

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
}
