package com.find.guide.fragment;

import java.util.List;

import com.find.guide.R;
import com.find.guide.app.TourGuideApplication;
import com.find.guide.model.InviteEvent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class InviteRecordAdapter extends BaseAdapter {

    private List<InviteEvent> mInviteEvents;

    private Context mContext;

    public InviteRecordAdapter(List<InviteEvent> inviteEvents) {
        mInviteEvents = inviteEvents;
        mContext = TourGuideApplication.getInstance();
    }

    @Override
    public int getCount() {
        if (mInviteEvents == null)
            return 0;
        return mInviteEvents.size();
    }

    @Override
    public Object getItem(int position) {
        if (mInviteEvents != null && position >= 0 && position < mInviteEvents.size()) {
            return mInviteEvents.get(position);
        }
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.invite_record_item, null);
            holder = new ViewHolder();
            holder.scenicTv = (TextView) convertView.findViewById(R.id.scenic);
            holder.startTimeTv = (TextView) convertView.findViewById(R.id.start_time);
            holder.guideNameTv = (TextView) convertView.findViewById(R.id.guide_name);
            holder.eventStatusTv = (TextView) convertView.findViewById(R.id.event_status);
            holder.content = convertView.findViewById(R.id.content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            if (getCount() == 1) {
                holder.content.setBackgroundResource(R.drawable.bg_border);
            } else {
                holder.content.setBackgroundResource(R.drawable.bg_top);
            }
        } else if (position == getCount() - 1) {
            holder.content.setBackgroundResource(R.drawable.bg_bottom);
        } else {
            holder.content.setBackgroundResource(R.drawable.bg_middle);
        }

        InviteEvent event = (InviteEvent) getItem(position);
        holder.scenicTv.setText(event.getScenic());
        holder.startTimeTv.setText("" + event.getStartTime());
        holder.guideNameTv.setText("" + event.getGuideId());
        holder.eventStatusTv.setText(getEventStatus(event.getEventStatus()));

        return convertView;
    }

    static final class ViewHolder {
        TextView scenicTv;
        TextView startTimeTv;
        TextView guideNameTv;
        TextView eventStatusTv;
        View content;
    }

    private String getEventStatus(int eventStatus) {
        switch (eventStatus) {
        case InviteEvent.EVENT_STATUS_BOOKING:
            return mContext.getString(R.string.invite_booking);
        case InviteEvent.EVENT_STATUS_CANCELED:
            return mContext.getString(R.string.invite_cancel);
        case InviteEvent.EVENT_STATUS_ACCEPTED:
            return mContext.getString(R.string.invite_accepted);
        case InviteEvent.EVENT_STATUS_REFUSED:
            return mContext.getString(R.string.invite_refused);
        case InviteEvent.EVENT_STATUS_EVALUATE:
            // TODO
            return mContext.getString(R.string.satisfaction_good);
        }
        return null;
    }

}
