package com.find.guide.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import com.find.guide.R;
import com.find.guide.app.TourGuideApplication;
import com.find.guide.fragment.NearByFragment;
import com.find.guide.fragment.RecordFragment;
import com.find.guide.fragment.SearchFragment;
import com.find.guide.fragment.SettingFragment;

public class MainActivity extends BaseActivity {

    private static final String TAG_NEARBY_GUIDE_FRAGMENT = "nearby_guide_fragment";
    private static final String TAG_PROFILE_RECORD_FRAGMENT = "profile_record_fragment";
    private static final String TAG_SEARCH_FRAGMENT = "search_fragment";
    private static final String TAG_SETTING_FRAGMENT = "setting_fragment";

    private static final String KEY_CURR_SELECTED_TAB_ID = "key_curr_selected_tab_id";
    private int mCurrSelectedTabId;

    private FragmentManager mFragmentManager;
    private Fragment mNearbyGuideFragment;
    private Fragment mProfileRecordFragment;
    private Fragment mSearchFragment;
    private Fragment mSettingFragment;

    private View mNearbyGuideBtn;
    private View mProfileRecordBtn;
    private View mSearchBtn;
    private View mSettingBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData(savedInstanceState);
        initUI();
        
        mActionbar.setDisplayHomeAsUpEnabled(false);
        mActionbar.setDisplayShowHomeEnabled(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_CURR_SELECTED_TAB_ID, mCurrSelectedTabId);
    }

    private void initUI() {
        mNearbyGuideBtn = findViewById(R.id.nearby_guide);
        mProfileRecordBtn = findViewById(R.id.profile_record);
        mSearchBtn = findViewById(R.id.search);
        mSettingBtn = findViewById(R.id.setting);

        View.OnClickListener tabClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToTab(v.getId());
            }
        };

        mNearbyGuideBtn.setOnClickListener(tabClickListener);
        mProfileRecordBtn.setOnClickListener(tabClickListener);
        mSearchBtn.setOnClickListener(tabClickListener);
        mSettingBtn.setOnClickListener(tabClickListener);

        switchToTab(mCurrSelectedTabId);
    }

    private void initData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCurrSelectedTabId = savedInstanceState.getInt(KEY_CURR_SELECTED_TAB_ID);
        } else {
            mCurrSelectedTabId = R.id.nearby_guide;
        }

        mFragmentManager = getFragmentManager();

        mNearbyGuideFragment = mFragmentManager.findFragmentByTag(TAG_NEARBY_GUIDE_FRAGMENT);
        if (mNearbyGuideFragment == null) {
            mNearbyGuideFragment = new NearByFragment();

            FragmentTransaction trans = mFragmentManager.beginTransaction();
            trans.add(R.id.fragment_container, mNearbyGuideFragment, TAG_NEARBY_GUIDE_FRAGMENT);
            trans.commit();
        }

        mProfileRecordFragment = mFragmentManager.findFragmentByTag(TAG_PROFILE_RECORD_FRAGMENT);
        if (mProfileRecordFragment == null) {
            mProfileRecordFragment = new RecordFragment();

            FragmentTransaction trans = mFragmentManager.beginTransaction();
            trans.add(R.id.fragment_container, mProfileRecordFragment, TAG_PROFILE_RECORD_FRAGMENT);
            trans.commit();
        }

        mSearchFragment = mFragmentManager.findFragmentByTag(TAG_SEARCH_FRAGMENT);
        if (mSearchFragment == null) {
            mSearchFragment = new SearchFragment();

            FragmentTransaction trans = mFragmentManager.beginTransaction();
            trans.add(R.id.fragment_container, mSearchFragment, TAG_SEARCH_FRAGMENT);
            trans.commit();
        }

        mSettingFragment = mFragmentManager.findFragmentByTag(TAG_SETTING_FRAGMENT);
        if (mSettingFragment == null) {
            mSettingFragment = new SettingFragment();

            FragmentTransaction trans = mFragmentManager.beginTransaction();
            trans.add(R.id.fragment_container, mSettingFragment, TAG_SETTING_FRAGMENT);
            trans.commit();
        }

    }

    private void switchToTab(int tabId) {
        FragmentTransaction tans = mFragmentManager.beginTransaction();

        switch (tabId) {
        case R.id.nearby_guide:
            mCurrSelectedTabId = R.id.nearby_guide;
            mActionbar.setTitle(R.string.nearby_guide);

            mNearbyGuideBtn.setSelected(true);
            mProfileRecordBtn.setSelected(false);
            mSearchBtn.setSelected(false);
            mSettingBtn.setSelected(false);

            tans.show(mNearbyGuideFragment);
            tans.hide(mProfileRecordFragment);
            tans.hide(mSearchFragment);
            tans.hide(mSettingFragment);
            break;

        case R.id.profile_record:
            mCurrSelectedTabId = R.id.profile_record;
            mActionbar.setTitle(R.string.profile_record);

            mNearbyGuideBtn.setSelected(false);
            mProfileRecordBtn.setSelected(true);
            mSearchBtn.setSelected(false);
            mSettingBtn.setSelected(false);

            tans.hide(mNearbyGuideFragment);
            tans.show(mProfileRecordFragment);
            tans.hide(mSearchFragment);
            tans.hide(mSettingFragment);
            break;

        case R.id.search:
            mCurrSelectedTabId = R.id.search;
            mActionbar.setTitle(R.string.search);

            mNearbyGuideBtn.setSelected(false);
            mProfileRecordBtn.setSelected(false);
            mSearchBtn.setSelected(true);
            mSettingBtn.setSelected(false);

            tans.hide(mNearbyGuideFragment);
            tans.hide(mProfileRecordFragment);
            tans.show(mSearchFragment);
            tans.hide(mSettingFragment);
            break;

        case R.id.setting:
            mCurrSelectedTabId = R.id.setting;
            mActionbar.setTitle(R.string.setting);

            mNearbyGuideBtn.setSelected(false);
            mProfileRecordBtn.setSelected(false);
            mSearchBtn.setSelected(false);
            mSettingBtn.setSelected(true);

            tans.hide(mNearbyGuideFragment);
            tans.hide(mProfileRecordFragment);
            tans.hide(mSearchFragment);
            tans.show(mSettingFragment);
            break;
        }

        tans.commitAllowingStateLoss();
    }

    public void onDestory() {
        TourGuideApplication app = (TourGuideApplication) getApplication();
        if (app.mBMapManager != null) {
            app.mBMapManager.destroy();
            app.mBMapManager = null;
        }

        super.onDestroy();
        
        System.exit(0);
    }

}
