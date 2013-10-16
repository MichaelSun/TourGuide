package com.find.guide.fragment;

import java.util.List;

import com.find.guide.R;
import com.find.guide.activity.SelectCityActivity;
import com.find.guide.app.TourGuideApplication;
import com.find.guide.model.CityItem;
import com.find.guide.model.TourGuide;
import com.find.guide.model.helper.UserHelper;
import com.find.guide.model.helper.UserHelper.OnSearchGuideListener;
import com.find.guide.view.TipsDialog;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SearchFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CODE_SELECT_CITY = 1;

    private View mCityView;
    private TextView mCityTv;
    private EditText mScenicEt;
    private Button mSearchBtn;
    private RadioGroup mGenderGroup;

    private CityItem mCityItem;
    private UserHelper mUserHelper;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserHelper = new UserHelper(TourGuideApplication.getInstance());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, null);

        mCityView = view.findViewById(R.id.city_layout);
        mCityTv = (TextView) view.findViewById(R.id.city_tv);
        mScenicEt = (EditText) view.findViewById(R.id.scenic_et);
        mSearchBtn = (Button) view.findViewById(R.id.search_btn);
        mGenderGroup = (RadioGroup) view.findViewById(R.id.gender_radio);

        mCityView.setOnClickListener(this);
        mSearchBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        TipsDialog.getInstance().dismiss();
        if (mUserHelper != null) {
            mUserHelper.destroy();
            mUserHelper = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.city_layout) {
            selectCity();
        } else if (v.getId() == R.id.search_btn) {
            searchGuide();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_CITY && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mCityItem = (CityItem) data.getSerializableExtra(SelectCityActivity.INTENT_EXTRA_CITY);
                if (mCityItem != null) {
                    mCityTv.setText(mCityItem.getCityName());
                }
            }
        }
    }

    private void selectCity() {
        Intent intent = new Intent(getActivity(), SelectCityActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_CITY);
    }

    private void searchGuide() {
        int gender = 0;
        int checkId = mGenderGroup.getCheckedRadioButtonId();
        if (checkId == R.id.male_radio) {
            gender = 1;
        } else if (checkId == R.id.female_radio) {
            gender = 2;
        }

        String scenic = mScenicEt.getText().toString();

        if (mCityItem == null) {
            TipsDialog.getInstance().show(getActivity(), R.drawable.tips_fail, R.string.need_select_city, false, true);
        } else {
            TipsDialog.getInstance().show(getActivity(), R.drawable.tips_loading, R.string.searching, true, true);
            mUserHelper.searchGuide(mCityItem.getCityCode(), gender, scenic, 0, 100, mOnSearchGuideListener);
        }
    }

    private OnSearchGuideListener mOnSearchGuideListener = new OnSearchGuideListener() {

        @Override
        public void onSearchGuide(final int result, final List<TourGuide> guides) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    if (result == UserHelper.SEARCH_GUIDE_SUCCESS) {
                        enterGuideList(guides);
                    } else {

                    }
                }
            });
        }
    };

    private void enterGuideList(List<TourGuide> guides) {

    }
}
