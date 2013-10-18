package com.find.guide.activity;

import java.util.Calendar;

import com.find.guide.R;
import com.find.guide.model.InviteEvent;
import com.find.guide.model.helper.InviteHelper;
import com.find.guide.model.helper.InviteHelper.OnSetStatisfactionListener;
import com.find.guide.view.TipsDialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SatisfactionActivity extends BaseActivity implements OnClickListener {

    public static final String INTENT_EXTRA_INVITE_EVENT = "intent_extra_invite_event";
    private InviteEvent mInviteEvent = null;

    private TextView mTourguideName;
    private TextView mDestinationTv;
    private TextView mStartTimeTv;
    private TextView mEndTimeTv;
    private Button mGoodBtn;
    private Button mBadBtn;

    private InviteHelper mInviteHelper = null;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satisfaction);

        if (savedInstanceState == null) {
            if (getIntent() != null) {
                mInviteEvent = (InviteEvent) getIntent().getSerializableExtra(INTENT_EXTRA_INVITE_EVENT);
            }
        } else {
            mInviteEvent = (InviteEvent) savedInstanceState.getSerializable(INTENT_EXTRA_INVITE_EVENT);
        }

        mInviteHelper = new InviteHelper(getApplicationContext());

        initUI();
        setData();

    }

    private void initUI() {
        mTourguideName = (TextView) findViewById(R.id.tourguide_name);
        mDestinationTv = (TextView) findViewById(R.id.destination_tv);
        mStartTimeTv = (TextView) findViewById(R.id.start_time_tv);
        mEndTimeTv = (TextView) findViewById(R.id.end_time_tv);
        mGoodBtn = (Button) findViewById(R.id.satisfaction_good);
        mBadBtn = (Button) findViewById(R.id.satisfaction_bad);

        mGoodBtn.setOnClickListener(this);
        mBadBtn.setOnClickListener(this);
    }

    private void setData() {
        if (mInviteEvent != null) {
            mTourguideName.setText(mInviteEvent.getGuideName());
            mDestinationTv.setText(mInviteEvent.getScenic());
            mStartTimeTv.setText(getTimeStr(mInviteEvent.getStartTime()));
            mEndTimeTv.setText(getTimeStr(mInviteEvent.getEndTime()));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(INTENT_EXTRA_INVITE_EVENT, mInviteEvent);
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
        if (v.getId() == R.id.satisfaction_good) {
            setSatisfaction(1);
        } else if (v.getId() == R.id.satisfaction_bad) {
            setSatisfaction(2);
        }
    }

    private void setSatisfaction(int satisfaction) {
        if (mInviteEvent != null) {
            TipsDialog.getInstance().show(this, R.drawable.tips_loading, "评价中...", true, false);
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
