package com.find.guide.activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.find.guide.R;
import com.find.guide.fragment.MapFragment;

public class MainActivity extends BaseActivity {

    private static final String TAG_CHAT_LIST_FRAGMENT = "location_fragment";
    private static final String TAG_CONTACT_FRAGMENT = "tuangou_fragment";
    private static final String TAG_PROFILE_FRAGMENT = "profile_fragment";
    private static final String TAG_SETTING_FRAGMENT = "more_fragment";

    private static final String KEY_CURR_SELECTED_TAB_ID = "key_curr_selected_tab_id";

    private FragmentManager mFragmentManager;
    private int mCurrSelectedTabId;
    private Fragment mLocationFragment;
    private Fragment mTuangouFragment;
    private Fragment mProfileFragment;
    private Fragment mMoreFragment;

    private View mLocationBtn;
    private View mTuangouBtn;
    private View mProfileBtn;
    private View mMoreBtn;

    private ImageView mLocationImage;
    private ImageView mTuangouImage;
    private ImageView mProfileImage;
    private ImageView mMoreImage;

    private TextView mLocationTV;
    private TextView mTuangouTV;
    private TextView mProfileTV;
    private TextView mMoreTV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData(savedInstanceState);
        initUI();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.ju_gei_li, menu);
//        return true;
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_CURR_SELECTED_TAB_ID, mCurrSelectedTabId);
    }

    private void initUI() {
        mLocationBtn = findViewById(R.id.location);
        mTuangouBtn = findViewById(R.id.tuangou);
        mProfileBtn = findViewById(R.id.profile);
        mMoreBtn = findViewById(R.id.more);

        View.OnClickListener tabClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToTab(v.getId());
            }
        };
        mLocationBtn.setOnClickListener(tabClickListener);
        mTuangouBtn.setOnClickListener(tabClickListener);
        mProfileBtn.setOnClickListener(tabClickListener);
        mMoreBtn.setOnClickListener(tabClickListener);

        mLocationImage = (ImageView) findViewById(R.id.location_img);
        mLocationTV = (TextView) findViewById(R.id.location_text);
        mTuangouImage = (ImageView) findViewById(R.id.tuangou_img);
        mTuangouTV = (TextView) findViewById(R.id.tuangou_text);
        mProfileImage = (ImageView) findViewById(R.id.profile_img);
        mProfileTV = (TextView) findViewById(R.id.profile_text);
        mMoreImage = (ImageView) findViewById(R.id.more_img);
        mMoreTV = (TextView) findViewById(R.id.more_text);

        switchToTab(mCurrSelectedTabId);
    }

    private void updateBottomSelected(int index) {
        mLocationImage.setImageResource(R.drawable.location);
        mTuangouImage.setImageResource(R.drawable.tuangou);
        mProfileImage.setImageResource(R.drawable.profile);
        mMoreImage.setImageResource(R.drawable.more);

        mLocationTV.setTextColor(getResources().getColor(R.color.bottom_normal_color));
        mTuangouTV.setTextColor(getResources().getColor(R.color.bottom_normal_color));
        mProfileTV.setTextColor(getResources().getColor(R.color.bottom_normal_color));
        mMoreTV.setTextColor(getResources().getColor(R.color.bottom_normal_color));

        ImageView imageSet = null;
        TextView textSet = null;
        switch (index) {
            case R.id.location:
                imageSet = mLocationImage;
                textSet = mLocationTV;
                break;
            case R.id.tuangou:
                imageSet = mTuangouImage;
                textSet = mTuangouTV;
                break;
            case R.id.profile:
                imageSet = mProfileImage;
                textSet = mProfileTV;
                break;
            case R.id.more:
                imageSet = mMoreImage;
                textSet = mMoreTV;
                break;
        }
        if (textSet != null && imageSet != null) {
            imageSet.setImageResource(R.drawable.location_press);
            textSet.setTextColor(getResources().getColor(R.color.bottom_pressed_color));
        }
    }

    private void initData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCurrSelectedTabId = savedInstanceState.getInt(KEY_CURR_SELECTED_TAB_ID);
        } else {
            mCurrSelectedTabId = R.id.location;
        }

        mFragmentManager = getFragmentManager();

        mLocationFragment = mFragmentManager.findFragmentByTag(TAG_CHAT_LIST_FRAGMENT);
        if (mLocationFragment == null) {
            mLocationFragment = new MapFragment();

            FragmentTransaction trans = mFragmentManager.beginTransaction();
            trans.add(R.id.fragment_container, mLocationFragment, TAG_CHAT_LIST_FRAGMENT);
            trans.commit();
        }

        mTuangouFragment = mFragmentManager.findFragmentByTag(TAG_CONTACT_FRAGMENT);
        if (mTuangouFragment == null) {
            //mTuangouFragment = new GroupOnFragment();
            mTuangouFragment = new MapFragment();

            FragmentTransaction trans = mFragmentManager.beginTransaction();
            trans.add(R.id.fragment_container, mTuangouFragment, TAG_CONTACT_FRAGMENT);
            trans.commit();
        }

        mProfileFragment = mFragmentManager.findFragmentByTag(TAG_PROFILE_FRAGMENT);
        if (mProfileFragment == null) {
            mProfileFragment = new MapFragment();

            FragmentTransaction trans = mFragmentManager.beginTransaction();
            trans.add(R.id.fragment_container, mProfileFragment, TAG_PROFILE_FRAGMENT);
            trans.commit();
        }

        mMoreFragment = mFragmentManager.findFragmentByTag(TAG_SETTING_FRAGMENT);
        if (mMoreFragment == null) {
            mMoreFragment = new MapFragment();

            FragmentTransaction trans = mFragmentManager.beginTransaction();
            trans.add(R.id.fragment_container, mMoreFragment, TAG_SETTING_FRAGMENT);
            trans.commit();
        }

    }

    private void switchToTab(int tabId) {
        FragmentTransaction tans = mFragmentManager.beginTransaction();

        switch (tabId) {
            case R.id.location:
                mCurrSelectedTabId = R.id.location;
                mActionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
                mActionbar.setTitle(R.string.location_title);

                mLocationBtn.setSelected(true);
                mTuangouBtn.setSelected(false);
                mProfileBtn.setSelected(false);
                mMoreBtn.setSelected(false);

                tans.show(mLocationFragment);
                tans.hide(mTuangouFragment);
                tans.hide(mProfileFragment);
                tans.hide(mMoreFragment);
                break;

            case R.id.tuangou:
                mCurrSelectedTabId = R.id.tuangou;
                mActionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
                mActionbar.setTitle(R.string.tuangou_title);

                mLocationBtn.setSelected(false);
                mTuangouBtn.setSelected(true);
                mProfileBtn.setSelected(false);
                mMoreBtn.setSelected(false);

                tans.hide(mLocationFragment);
                tans.show(mTuangouFragment);
                tans.hide(mProfileFragment);
                tans.hide(mMoreFragment);
                break;

            case R.id.profile:
                mCurrSelectedTabId = R.id.profile;
                mActionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
                mActionbar.setTitle(R.string.profile_title);

                mLocationBtn.setSelected(false);
                mTuangouBtn.setSelected(false);
                mProfileBtn.setSelected(true);
                mMoreBtn.setSelected(false);

                tans.hide(mLocationFragment);
                tans.hide(mTuangouFragment);
                tans.show(mProfileFragment);
                tans.hide(mMoreFragment);
                
//                Intent intent = new Intent();
//                intent.setClass(JuGeiLiActivity.this, LoginActivity.class);
//                startActivity(intent);
                
                break;

            case R.id.more:
                mCurrSelectedTabId = R.id.more;
                mActionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME);
                mActionbar.setIcon(R.drawable.actionbar_main_page_icon);

                mLocationBtn.setSelected(false);
                mTuangouBtn.setSelected(false);
                mProfileBtn.setSelected(false);
                mMoreBtn.setSelected(true);

                tans.hide(mLocationFragment);
                tans.hide(mTuangouFragment);
                tans.hide(mProfileFragment);
                tans.show(mMoreFragment);
                break;
        }
        updateBottomSelected(tabId);

        tans.commitAllowingStateLoss();
    }

}
