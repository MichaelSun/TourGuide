package com.find.guide.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.find.guide.R;
import com.find.guide.model.TourGuide;

public class GuideView extends LinearLayout {

    private TextView mNameTv;
    private View mStar1;
    private View mStar2;
    private View mStar3;
    private View mStar4;
    private View mStar5;

    public GuideView(Context context) {
        this(context, null);
    }

    public GuideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.map_guide_view_layout, this);
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundResource(R.drawable.map_guide_bg);

        mNameTv = (TextView) findViewById(R.id.guide_name);
        mStar1 = findViewById(R.id.guide_start1);
        mStar2 = findViewById(R.id.guide_start2);
        mStar3 = findViewById(R.id.guide_start3);
        mStar4 = findViewById(R.id.guide_start4);
        mStar5 = findViewById(R.id.guide_start5);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setGuide(TourGuide guide) {
        if (guide != null) {
            mNameTv.setText(guide.getUserName());
            int star = 0;
            if (guide.getEvaluateCount() > 0) {
                star = guide.getEvaluateScore() / guide.getEvaluateCount();
            }
            setStar(star);
        }
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

}
