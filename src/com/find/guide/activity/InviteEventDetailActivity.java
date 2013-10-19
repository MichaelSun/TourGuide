package com.find.guide.activity;

import java.util.Calendar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.find.guide.R;
import com.find.guide.model.InviteEvent;
import com.find.guide.model.helper.InviteHelper;
import com.find.guide.model.helper.InviteHelper.OnInviteListener;
import com.find.guide.model.helper.InviteHelper.OnSetStatisfactionListener;
import com.find.guide.view.TipsDialog;

public class InviteEventDetailActivity extends BaseActivity implements OnClickListener {

    public static final String INTENT_EXTRA_INVITE_EVENT_OBJ = "intent_extra_invite_event_obj";

    private InviteEvent mInviteEvent = null;

    private TextView mGuideName;
    private TextView mDestinationTv;
    private TextView mStartTimeTv;
    private TextView mEndTimeTv;
    private Button mButton1;
    private Button mButton2;

    private InviteHelper mInviteHelper = null;

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
        mGuideName = (TextView) findViewById(R.id.guide_name);
        mDestinationTv = (TextView) findViewById(R.id.destination_tv);
        mStartTimeTv = (TextView) findViewById(R.id.start_time_tv);
        mEndTimeTv = (TextView) findViewById(R.id.end_time_tv);
        mButton1 = (Button) findViewById(R.id.button1);
        mButton2 = (Button) findViewById(R.id.button2);

        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);

        mGuideName.setText(mInviteEvent.getGuideName());
        mDestinationTv.setText(mInviteEvent.getScenic());
        mStartTimeTv.setText(getTimeStr(mInviteEvent.getStartTime()));
        mEndTimeTv.setText(getTimeStr(mInviteEvent.getEndTime()));

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
        }
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
                        finish();
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
            TipsDialog.getInstance().show(this, R.drawable.tips_loading, R.string.in_statisfaction, true, false);
            mInviteHelper.setStatisfaction(mInviteEvent.getEventId(), mInviteEvent.getGuideId(), satisfaction,
                    mOnSetStatisfactionListener);
        }
    }

    OnSetStatisfactionListener mOnSetStatisfactionListener = new OnSetStatisfactionListener() {
        @Override
        public void onSetStatisfaction(final int result) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TipsDialog.getInstance().dismiss();
                    if (result == InviteHelper.SUCCESS) {
                        finish();
                    } else if (result == InviteHelper.FAILED) {
                        TipsDialog.getInstance().show(InviteEventDetailActivity.this, R.drawable.tips_loading,
                                R.string.statisfaction_failed, true);
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
        return year + "/" + (month + 1 < 10 ? "0" + (month + 1) : month + 1) + "/" + (day < 10 ? "0" + (day) : day);
    }
}
