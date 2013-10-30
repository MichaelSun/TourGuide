package com.find.guide.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.find.guide.R;
import com.find.guide.config.AppRuntime;
import com.find.guide.user.TourGuide;
import com.plugin.common.view.WebImageView;

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
            holder.header = (WebImageView) convertView.findViewById(R.id.header);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.gender = (ImageView) convertView.findViewById(R.id.gender);
            holder.count = (TextView) convertView.findViewById(R.id.num);
            holder.distance = (TextView) convertView.findViewById(R.id.distance);
            holder.star1 = convertView.findViewById(R.id.guide_star1);
            holder.star2 = convertView.findViewById(R.id.guide_star2);
            holder.star3 = convertView.findViewById(R.id.guide_star3);
            holder.star4 = convertView.findViewById(R.id.guide_star4);
            holder.star5 = convertView.findViewById(R.id.guide_star5);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final TourGuide guide = mTourGuides.get(position);

        holder.name.setText(guide.getUserName());

        if (guide.getGender() == 1) {
            holder.gender.setImageResource(R.drawable.icon_male);
        } else if (guide.getGender() == 2) {
            holder.gender.setImageResource(R.drawable.icon_female);
        } else {
            holder.gender.setImageDrawable(null);
        }

        if (guide.getEvaluateCount() > 0) {
            holder.count.setText(guide.getEvaluateCount() + "次评价");
        } else {
            holder.count.setText("暂无评价");
        }

        int star = 0;
        if (guide.getEvaluateCount() > 0) {
            star = guide.getEvaluateScore() / guide.getEvaluateCount();
        }
        setStar(holder, star);

        holder.header.setImageURI(new Uri.Builder().path(guide.getHeadUrl()).build());

        double[] latlng1 = parseLocation(guide.getLocation());
        double[] latlng2 = parseLocation(AppRuntime.gLocation);
        double distance = 0.0;
        if (latlng1 != null && latlng2 != null && latlng1.length == 2 && latlng2.length == 2) {
            GeoPoint pt1 = new GeoPoint((int) (latlng1[0] * 1E6), (int) (latlng1[1] * 1E6));
            GeoPoint pt2 = new GeoPoint((int) (latlng2[0] * 1E6), (int) (latlng2[1] * 1E6));
            distance = DistanceUtil.getDistance(pt1, pt2);
            // distance =
            // com.find.guide.utils.DistanceUtils.getDistatce(latlng1[0],
            // latlng1[1], latlng2[0], latlng2[1]);
        }
        DecimalFormat df = new DecimalFormat("#.##");
        holder.distance.setText(df.format(distance / 1000) + "km");

        return convertView;
    }

    static final class ViewHolder {
        WebImageView header;
        TextView name;
        ImageView gender;
        TextView distance;
        View star1;
        View star2;
        View star3;
        View star4;
        View star5;
        TextView count;
        View content;
    }

    private void setStar(ViewHolder holder, int star) {
        if (star >= 5) {
            holder.star1.setBackgroundResource(R.drawable.star_big_gold);
            holder.star2.setBackgroundResource(R.drawable.star_big_gold);
            holder.star3.setBackgroundResource(R.drawable.star_big_gold);
            holder.star4.setBackgroundResource(R.drawable.star_big_gold);
            holder.star5.setBackgroundResource(R.drawable.star_big_gold);
        } else if (star == 4) {
            holder.star1.setBackgroundResource(R.drawable.star_big_gold);
            holder.star2.setBackgroundResource(R.drawable.star_big_gold);
            holder.star3.setBackgroundResource(R.drawable.star_big_gold);
            holder.star4.setBackgroundResource(R.drawable.star_big_gold);
            holder.star5.setBackgroundResource(R.drawable.star_big_silver);
        } else if (star == 3) {
            holder.star1.setBackgroundResource(R.drawable.star_big_gold);
            holder.star2.setBackgroundResource(R.drawable.star_big_gold);
            holder.star3.setBackgroundResource(R.drawable.star_big_gold);
            holder.star4.setBackgroundResource(R.drawable.star_big_silver);
            holder.star5.setBackgroundResource(R.drawable.star_big_silver);
        } else if (star == 2) {
            holder.star1.setBackgroundResource(R.drawable.star_big_gold);
            holder.star2.setBackgroundResource(R.drawable.star_big_gold);
            holder.star3.setBackgroundResource(R.drawable.star_big_silver);
            holder.star4.setBackgroundResource(R.drawable.star_big_silver);
            holder.star5.setBackgroundResource(R.drawable.star_big_silver);
        } else if (star == 1) {
            holder.star1.setBackgroundResource(R.drawable.star_big_gold);
            holder.star2.setBackgroundResource(R.drawable.star_big_silver);
            holder.star3.setBackgroundResource(R.drawable.star_big_silver);
            holder.star4.setBackgroundResource(R.drawable.star_big_silver);
            holder.star5.setBackgroundResource(R.drawable.star_big_silver);
        } else {
            holder.star1.setBackgroundResource(R.drawable.star_big_silver);
            holder.star2.setBackgroundResource(R.drawable.star_big_silver);
            holder.star3.setBackgroundResource(R.drawable.star_big_silver);
            holder.star4.setBackgroundResource(R.drawable.star_big_silver);
            holder.star5.setBackgroundResource(R.drawable.star_big_silver);
        }
    }

    private double[] parseLocation(String location) {
        double[] lnglat = new double[2];
        if (!TextUtils.isEmpty(location)) {
            String[] s = location.split(",");
            if (s != null && s.length == 2) {
                try {
                    lnglat[0] = Double.parseDouble(s[0]);
                    lnglat[1] = Double.parseDouble(s[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return lnglat;
    }

}
