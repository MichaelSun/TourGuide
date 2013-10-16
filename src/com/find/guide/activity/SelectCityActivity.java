package com.find.guide.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import com.find.guide.R;
import com.find.guide.model.CityItem;
import com.find.guide.model.helper.CityManager;
import com.find.guide.view.SlideView;
import com.find.guide.view.SlideView.OnTouchingLetterChangedListener;

public class SelectCityActivity extends BaseActivity implements OnItemClickListener {

    public static final String INTENT_EXTRA_CITY = "intent_extra_city";

    private ListView mListView;

    private TextView mOverlay;

    private SlideView mSlideView;

    private List<CityItem> mCityItems = new ArrayList<CityItem>();

    private CityAdapter mCityAdapter;

    private OverlayThread mOverlayThread;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        initData();
        initUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.removeView(mOverlay);
    }

    private void initData() {
        Hashtable<String, List<CityItem>> cities = CityManager.getInstance().getAllCities();
        if (cities != null) {
            // sort
            List<Entry<String, List<CityItem>>> list = new ArrayList<Entry<String, List<CityItem>>>(cities.entrySet());
            Collections.sort(list, new Comparator<Entry<String, List<CityItem>>>() {
                @Override
                public int compare(Entry<String, List<CityItem>> lhs, Entry<String, List<CityItem>> rhs) {
                    return lhs.getKey().compareToIgnoreCase(rhs.getKey());
                }
            });

            for (Entry<String, List<CityItem>> entry : list) {
                List<CityItem> citylist = entry.getValue();
                if (citylist != null) {
                    mCityItems.addAll(citylist);
                }
            }
        }
    }

    private void initUI() {
        mListView = (ListView) this.findViewById(R.id.listview);
        mCityAdapter = new CityAdapter(this, mCityItems);
        mListView.setAdapter(mCityAdapter);

        this.mOverlayThread = new OverlayThread();
        initOverlay();
        this.mSlideView = (SlideView) this.findViewById(R.id.slideview);
        this.mSlideView.setOnTouchingLetterChangedListener(new LetterListViewListener());

        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= 0 && position < mCityItems.size()) {
            CityItem cityItem = mCityItems.get(position);
            Intent data = new Intent();
            data.putExtra(INTENT_EXTRA_CITY, cityItem);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private class LetterListViewListener implements OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(final String s) {
            mOverlay.setText(s);
            mOverlay.setVisibility(View.VISIBLE);

            mHandler.removeCallbacks(mOverlayThread);
            mHandler.postDelayed(mOverlayThread, 1500);

            if (!TextUtils.isEmpty(s)) {
                char section = s.toUpperCase().charAt(0);
                int position = mCityAdapter.getPositionForSection(section);
                mListView.setSelection(position);
            }
        }
    }

    private void initOverlay() {
        LayoutInflater inflater = LayoutInflater.from(this);
        mOverlay = (TextView) inflater.inflate(R.layout.listview_overlay, null);
        mOverlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(mOverlay, lp);
    }

    private class OverlayThread implements Runnable {

        @Override
        public void run() {
            mOverlay.setVisibility(View.GONE);
        }

    }

    static class CityAdapter extends BaseAdapter implements SectionIndexer {

        private Context mContext;
        private List<CityItem> mCityItems;

        public CityAdapter(Context context, List<CityItem> cityItems) {
            mContext = context;
            mCityItems = cityItems;
        }

        @Override
        public int getCount() {
            if (mCityItems == null)
                return 0;
            return mCityItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.city_item, null);
                holder = new ViewHolder();
                holder.section = (TextView) convertView.findViewById(R.id.section);
                holder.cityName = (TextView) convertView.findViewById(R.id.city_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            int section = getSectionForPosition(position);
            if (position == 0) {
                holder.section.setVisibility(View.VISIBLE);
            } else {
                int nextSection = getSectionForPosition(position - 1);
                if (section == nextSection) {
                    holder.section.setVisibility(View.GONE);
                } else {
                    holder.section.setVisibility(View.VISIBLE);
                }
            }

            CityItem cityItem = mCityItems.get(position);
            holder.section.setText("" + (char) section);
            holder.cityName.setText(cityItem.getCityName());

            return convertView;
        }

        @Override
        public Object[] getSections() {
            return null;
        }

        @Override
        public int getPositionForSection(int section) {
            if (mCityItems != null) {
                for (int i = 0; i < mCityItems.size(); i++) {
                    String pinyin = mCityItems.get(i).getCityIndex();
                    char firstChar = pinyin.toUpperCase().charAt(0);
                    if (firstChar == section) {
                        return i;
                    }
                }
            }
            return 0;
        }

        @Override
        public int getSectionForPosition(int position) {
            if (mCityItems != null) {
                String pinyin = mCityItems.get(position).getCityIndex();
                if (pinyin != null && pinyin.length() > 0) {
                    return pinyin.toUpperCase().charAt(0);
                }
            }
            return '#';
        }

        static class ViewHolder {
            TextView section;
            TextView cityName;
        }

    }

}