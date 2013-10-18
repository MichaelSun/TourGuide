package com.find.guide.adapter;

import java.util.List;

import com.find.guide.R;
import com.find.guide.activity.BookingActivity;
import com.find.guide.model.TourGuide;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GuideAdapter extends BaseAdapter {

    private Activity mActivity;

    private List<TourGuide> mTourGuides;

    public GuideAdapter(Activity activity, List<TourGuide> guides) {
        mTourGuides = guides;
        mActivity = activity;
    }

    @Override
    public int getCount() {
        if (mTourGuides == null)
            return 0;
        return mTourGuides.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.guide_list_item, null);
            holder = new ViewHolder();
            holder.content = convertView.findViewById(R.id.content);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.gender = (TextView) convertView.findViewById(R.id.gender);
            holder.number = (TextView) convertView.findViewById(R.id.num);
            holder.stars = (TextView) convertView.findViewById(R.id.stars);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final TourGuide guide = mTourGuides.get(position);
        holder.name.setText(guide.getUserName());
        if (guide.getGender() == 1) {
            holder.gender.setText(R.string.male);
        } else if (guide.getGender() == 2) {
            holder.gender.setText(R.string.female);
        } else {
            holder.gender.setText(R.string.gender_unknown);
        }

        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, BookingActivity.class);
                intent.putExtra(BookingActivity.INTENT_EXTRA_GUIDE, guide);
                mActivity.startActivity(intent);
            }
        });

        return convertView;
    }

    static final class ViewHolder {
        TextView name;
        TextView gender;
        TextView number;
        TextView stars;
        View content;
    }

}
