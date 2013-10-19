package com.find.guide.adapter;

import java.util.Calendar;
import java.util.List;

import com.find.guide.R;
import com.find.guide.activity.InviteEventDetailActivity;
import com.find.guide.model.InviteEvent;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class InviteRecordAdapter extends BaseAdapter {

    private List<InviteEvent> mInviteEvents;

    private Activity mActivity;

    public InviteRecordAdapter(Activity activity, List<InviteEvent> inviteEvents) {
        mInviteEvents = inviteEvents;
        mActivity = activity;
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
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.invite_record_item, null);
            holder = new ViewHolder();
            holder.content = convertView.findViewById(R.id.content);
            holder.scenicTv = (TextView) convertView.findViewById(R.id.scenic);
            holder.startTimeTv = (TextView) convertView.findViewById(R.id.start_time);
            holder.guideNameTv = (TextView) convertView.findViewById(R.id.name);
            holder.eventStatusTv = (TextView) convertView.findViewById(R.id.event_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final InviteEvent event = (InviteEvent) getItem(position);
        holder.scenicTv.setText(event.getScenic());
        holder.startTimeTv.setText(getTimeStr(event.getStartTime()));
        holder.guideNameTv.setText(event.getGuideName());
        setEventStatus(holder, event);

        holder.content.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onClickRecord(event);
            }
        });

        return convertView;
    }

    static final class ViewHolder {
        TextView scenicTv;
        TextView startTimeTv;
        TextView guideNameTv;
        TextView eventStatusTv;
        View content;
    }

    private void setEventStatus(ViewHolder holder, InviteEvent inviteEvent) {
        Resources res = mActivity.getResources();
        switch (inviteEvent.getEventStatus()) {
        case InviteEvent.EVENT_STATUS_BOOKING:
            if (inviteEvent.getEventType() == InviteEvent.EVENT_TYPE_BROADCASR) {
                holder.eventStatusTv.setText(R.string.invite_broadcast);
            } else {
                holder.eventStatusTv.setText(R.string.invite_booking);
            }
            holder.eventStatusTv.setTextColor(res.getColor(R.color.record_important));
            break;
        case InviteEvent.EVENT_STATUS_CANCELED:
            holder.eventStatusTv.setText(R.string.invite_cancel);
            holder.eventStatusTv.setTextColor(res.getColor(R.color.record_normal));
            break;
        case InviteEvent.EVENT_STATUS_ACCEPTED:
            holder.eventStatusTv.setText(R.string.invite_accepted);
            holder.eventStatusTv.setTextColor(res.getColor(R.color.record_important));
            break;
        case InviteEvent.EVENT_STATUS_REFUSED:
            holder.eventStatusTv.setText(R.string.invite_refused);
            holder.eventStatusTv.setTextColor(res.getColor(R.color.record_normal));
            break;
        case InviteEvent.EVENT_STATUS_EVALUATE:
            if (inviteEvent.getSatisfaction() == InviteEvent.SATISFACTION_GOOD) {
                holder.eventStatusTv.setText(R.string.satisfaction_good);
            } else if (inviteEvent.getSatisfaction() == InviteEvent.SATISFACTION_BAD) {
                holder.eventStatusTv.setText(R.string.satisfaction_bad);
            }
            holder.eventStatusTv.setTextColor(res.getColor(R.color.record_normal));
            break;
        }
    }

    private void onClickRecord(InviteEvent inviteEvent) {
        Intent intent = new Intent(mActivity, InviteEventDetailActivity.class);
        intent.putExtra(InviteEventDetailActivity.INTENT_EXTRA_INVITE_EVENT_OBJ, inviteEvent);
        mActivity.startActivity(intent);
    }

    private String getTimeStr(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "/" + (month + 1 < 10 ? "0" + (month + 1) : month + 1) + "/" + (day < 10 ? "0" + (day) : day);
    }

}
