package com.find.guide.adapter;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.find.guide.R;
import com.find.guide.guide.GuideEvent;

public class GuideRecordAdapter extends BaseAdapter {

    private List<GuideEvent> mGuideEvents;

    private Activity mActivity;

    public static enum VisitMode {
        ONESELF, OTHERS
    }

    public GuideRecordAdapter(Activity activity, List<GuideEvent> guideEvents) {
        mGuideEvents = guideEvents;
        mActivity = activity;
    }

    @Override
    public int getCount() {
        if (mGuideEvents == null)
            return 0;
        return mGuideEvents.size();
    }

    @Override
    public Object getItem(int position) {
        if (mGuideEvents != null && position >= 0 && position < mGuideEvents.size()) {
            return mGuideEvents.get(position);
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
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.guide_record_item, null);
            holder = new ViewHolder();
            holder.content = convertView.findViewById(R.id.content);
            holder.scenicTv = (TextView) convertView.findViewById(R.id.scenic);
            holder.startTimeTv = (TextView) convertView.findViewById(R.id.start_time);
            holder.NameTv = (TextView) convertView.findViewById(R.id.name);
            holder.eventStatusTv = (TextView) convertView.findViewById(R.id.event_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final GuideEvent event = (GuideEvent) getItem(position);
        holder.scenicTv.setText(event.getScenic());
        holder.startTimeTv.setText(getTimeStr(event.getStartTime()));
        holder.NameTv.setText(event.getUserName());
        setEventStatus(holder, event);

        return convertView;
    }

    static final class ViewHolder {
        TextView scenicTv;
        TextView startTimeTv;
        TextView NameTv;
        TextView eventStatusTv;
        View content;
    }

    private void setEventStatus(ViewHolder holder, GuideEvent event) {
        Resources res = mActivity.getResources();
        switch (event.getEventStatus()) {
        case GuideEvent.EVENT_STATUS_WAITING:
            holder.eventStatusTv.setText(R.string.guide_waiting);
            holder.eventStatusTv.setTextColor(res.getColor(R.color.record_important));
            break;
        case GuideEvent.EVENT_STATUS_ACCEPTED:
            holder.eventStatusTv.setText(R.string.guide_accepted);
            holder.eventStatusTv.setTextColor(res.getColor(R.color.record_normal));
            break;
        case GuideEvent.EVENT_STATUS_REFUSED:
            holder.eventStatusTv.setText(R.string.guide_refused);
            holder.eventStatusTv.setTextColor(res.getColor(R.color.record_normal));
            break;
        case GuideEvent.EVENT_STATUS_SATISFACTION:
            if (event.getSatisfaction() == GuideEvent.SATISFACTION_BAD) {
                holder.eventStatusTv.setText(R.string.satisfaction_bad);
            } else {
                holder.eventStatusTv.setText(R.string.satisfaction_good);
            }
            holder.eventStatusTv.setTextColor(res.getColor(R.color.record_normal));
            break;
        case GuideEvent.EVENT_STATUS_CANCEL:
            holder.eventStatusTv.setText(R.string.guide_cancel);
            holder.eventStatusTv.setTextColor(res.getColor(R.color.record_normal));
            break;
        case GuideEvent.EVENT_STATUS_ACCEPTED_BY_OTHER:
            holder.eventStatusTv.setText(R.string.guide_accpeted_by_other);
            holder.eventStatusTv.setTextColor(res.getColor(R.color.record_normal));
            break;
        default:
            holder.eventStatusTv.setText("");
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

}
