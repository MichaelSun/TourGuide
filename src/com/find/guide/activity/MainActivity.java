package com.find.guide.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushSettings;
import com.find.guide.R;
import com.find.guide.app.TourGuideApplication;
import com.find.guide.config.AppConfig;
import com.find.guide.config.AppRuntime;
import com.find.guide.fragment.NearByFragment;
import com.find.guide.fragment.RecordFragment;
import com.find.guide.fragment.SearchFragment;
import com.find.guide.fragment.SettingFragment;
import com.find.guide.push.PushUtils;
import com.find.guide.setting.SettingManager;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends BaseActivity {

    public static final String ACTION_PUSH_CLICK = "com.find.guide.push.click";

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

    private boolean mPressedBackKey = false;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mCurrSelectedTabId = savedInstanceState.getInt(KEY_CURR_SELECTED_TAB_ID);
        } else {
            mCurrSelectedTabId = R.id.nearby_guide;
            if (getIntent() != null && ACTION_PUSH_CLICK.equals(getIntent().getAction())) {
                mCurrSelectedTabId = R.id.record;
            }
        }

        initUI();

        mActionbar.setDisplayHomeAsUpEnabled(false);
        mActionbar.setDisplayShowHomeEnabled(false);

        switchToTab(mCurrSelectedTabId);

        UmengUpdateAgent.setUpdateAutoPopup(true);
        UmengUpdateAgent.update(this);

        PushSettings.enableDebugMode(getApplicationContext(), AppConfig.DEBUG);
        if (SettingManager.getInstance().getUserId() > 0) {
            PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                    PushUtils.getMetaValue(MainActivity.this, "BAIDU_PUSH_APIKEY"));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_CURR_SELECTED_TAB_ID, mCurrSelectedTabId);
    }

    private void initUI() {
        mNearbyGuideBtn = findViewById(R.id.nearby_guide);
        mProfileRecordBtn = findViewById(R.id.record);
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

        case R.id.record:
            mCurrSelectedTabId = R.id.record;

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

        if (mCurrSelectedTabId == R.id.record && mProfileRecordFragment != null
                && mProfileRecordFragment instanceof RecordFragment) {
            ((RecordFragment) mProfileRecordFragment).forceRefresh();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!mPressedBackKey) {
                mPressedBackKey = true;
                Toast.makeText(this, "再按一次退出找导游", Toast.LENGTH_SHORT).show();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPressedBackKey = false;
                    }
                }, 2000);
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null) {
            if (TourGuideApplication.ACTION_KICKOUT.equals(intent.getAction())) {
                if (AppRuntime.gInLogoutProcess.get()) {
                    return;
                }
                AppRuntime.gInLogoutProcess.set(true);
                SettingManager.getInstance().clearAll();
                PushManager.stopWork(getApplicationContext());
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                AppRuntime.gInLogoutProcess.set(false);
            } else if (ACTION_PUSH_CLICK.equals(intent.getAction())) {
                mCurrSelectedTabId = R.id.record;
                switchToTab(mCurrSelectedTabId);
            }
        }
    }

    public void onDestory() {
        TourGuideApplication app = (TourGuideApplication) getApplication();
        if (app.mBMapManager != null) {
            app.mBMapManager.destroy();
            app.mBMapManager = null;
        }
        AppRuntime.gInLogoutProcess.set(false);

        super.onDestroy();

        System.exit(0);
    }

}
