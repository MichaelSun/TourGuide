package com.find.guide.fragment;

import java.util.List;

import com.find.guide.model.GuideEvent;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class GuideRecordAdapter extends BaseAdapter {

    private List<GuideEvent> mGuideEvents;

    public GuideRecordAdapter(List<GuideEvent> guideEvents) {
        mGuideEvents = guideEvents;
    }

    @Override
    public int getCount() {
        return 0;
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
        return null;
    }

}
