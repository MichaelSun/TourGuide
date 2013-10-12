package com.find.guide.view;

import com.find.guide.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GuideView extends LinearLayout {

    private TextView mNameTv;
    private View mStart1;
    private View mStart2;
    private View mStart3;
    private View mStart4;
    private View mStart5;

    public GuideView(Context context) {
        this(context, null);
    }

    public GuideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.map_guide_view_layout, this);
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundResource(R.drawable.map_guide_bg);

        mNameTv = (TextView) findViewById(R.id.guide_name);
        mStart1 = findViewById(R.id.guide_start1);
        mStart2 = findViewById(R.id.guide_start1);
        mStart3 = findViewById(R.id.guide_start1);
        mStart4 = findViewById(R.id.guide_start1);
        mStart5 = findViewById(R.id.guide_start1);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setGuideInfo(GuideInfo guide) {
        if (guide != null) {
            mNameTv.setText(guide.userName);
        }
    }

    public static class GuideInfo {

        public int userId;
        public String userName;
        public String mobile;
        public int gender;
        public int userType;
        public String headUrl;
        public String goodAtScenic;
        public long birthday;
        public int beGuideYear;
        public String guideCardUrl;
        public String guideCardId;
        public String location;
        public int city;
    }

}
