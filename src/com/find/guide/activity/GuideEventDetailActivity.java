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
import com.find.guide.model.GuideEvent;
import com.find.guide.model.helper.GuideHelper;
import com.find.guide.model.helper.GuideHelper.OnAcceptedListener;
import com.find.guide.model.helper.GuideHelper.OnRefusedListener;
import com.find.guide.view.TipsDialog;

public class GuideEventDetailActivity extends BaseActivity implements OnClickListener {

    public static final String INTENT_EXTRA_GUIDE_EVENT_OBJ = "intent_extra_guide_event_obj";

    private GuideEvent mGuideEvent = null;

    private TextView mTouristName;
    private TextView mDestinationTv;
    private TextView mStartTimeTv;
    private TextView mEndTimeTv;
    private Button mButton1;
    private Button mButton2;

    private GuideHelper mGuideHelper = null;

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
        mDestinationTv = (TextView) findViewById(R.id.destination_tv);
        mStartTimeTv = (TextView) findViewById(R.id.start_time_tv);
        mEndTimeTv = (TextView) findViewById(R.id.end_time_tv);
        mButton1 = (Button) findViewById(R.id.button1);
        mButton2 = (Button) findViewById(R.id.button2);

        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);

        mTouristName.setText(mGuideEvent.getUserName());
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
        }
    }

    private String getTimeStr(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "/" + (month + 1 < 10 ? "0" + (month + 1) : month + 1) + "/" + (day < 10 ? "0" + (day) : day);
    }

    private void accept() {
        TipsDialog.getInstance().show(this, R.drawable.tips_loading, "接受中...", true, false);
        mGuideHelper.accept(mGuideEvent.getEventId(), mGuideEvent.getUserId(), mOnAcceptedListener);
    }

    private void refuse() {
        TipsDialog.getInstance().show(this, R.drawable.tips_loading, "忽略中...", true, false);
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
                        finish();
                    } else {

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
                        finish();
                    } else {

                    }
                }
            });
        }
    };
}
