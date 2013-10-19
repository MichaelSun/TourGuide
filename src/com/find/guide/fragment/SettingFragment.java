package com.find.guide.fragment;

import com.find.guide.R;
import com.find.guide.activity.GuideIdentifyActivity;
import com.find.guide.activity.LoginActivity;
import com.find.guide.activity.ProfileActivity;
import com.find.guide.app.TourGuideApplication;
import com.find.guide.config.AppRuntime;
import com.find.guide.model.Tourist;
import com.find.guide.setting.SettingManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class SettingFragment extends Fragment implements OnClickListener {

    private View mLoginRegisterView;
    private View mProfileView;
    private View mGuideAuthenticationView;
    private View mUpdateView;
    private View mFeedBackView;
    private View mLogoutView;
    private View mGuideArrowView;
    private Switch mGuideSwitch;

    private Dialog mDialog = null;

    private SettingManager mSettingManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettingManager = SettingManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, null);

        mLoginRegisterView = view.findViewById(R.id.setting_login);
        mProfileView = view.findViewById(R.id.setting_profile);
        mGuideAuthenticationView = view.findViewById(R.id.setting_guide_authentication);
        mUpdateView = view.findViewById(R.id.setting_update);
        mFeedBackView = view.findViewById(R.id.setting_feedback);
        mLogoutView = view.findViewById(R.id.setting_logout);
        mGuideArrowView = view.findViewById(R.id.guide_arrow);
        mGuideSwitch = (Switch) view.findViewById(R.id.guide_switch);

        mLoginRegisterView.setOnClickListener(this);
        mProfileView.setOnClickListener(this);
        mGuideAuthenticationView.setOnClickListener(this);
        mUpdateView.setOnClickListener(this);
        mFeedBackView.setOnClickListener(this);
        mLogoutView.setOnClickListener(this);

        mGuideSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SettingManager.getInstance().setGuideMode(0);
                } else {
                    SettingManager.getInstance().setGuideMode(1);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        dismissDialog();
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            checkVisibility();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        checkVisibility();
    }

    private void checkVisibility() {
        if (mSettingManager.getUserId() < 0) {
            mLoginRegisterView.setVisibility(View.VISIBLE);
            mProfileView.setVisibility(View.GONE);
            mGuideAuthenticationView.setVisibility(View.GONE);
            mLogoutView.setVisibility(View.GONE);
            mFeedBackView.setBackgroundResource(R.drawable.bg_bottom);
        } else if (mSettingManager.getUserType() == Tourist.USER_TYPE_TOURIST) {
            mLoginRegisterView.setVisibility(View.GONE);
            mProfileView.setVisibility(View.VISIBLE);
            mGuideAuthenticationView.setVisibility(View.VISIBLE);
            mLogoutView.setVisibility(View.VISIBLE);
            mProfileView.setBackgroundResource(R.drawable.bg_top);
            mFeedBackView.setBackgroundResource(R.drawable.bg_middle);
            mGuideArrowView.setVisibility(View.VISIBLE);
            mGuideSwitch.setVisibility(View.GONE);
            mGuideAuthenticationView.setOnClickListener(this);
        } else {
            mLoginRegisterView.setVisibility(View.GONE);
            mProfileView.setVisibility(View.VISIBLE);
            mGuideAuthenticationView.setVisibility(View.VISIBLE);
            mLogoutView.setVisibility(View.VISIBLE);
            mProfileView.setBackgroundResource(R.drawable.bg_top);
            mFeedBackView.setBackgroundResource(R.drawable.bg_middle);
            mGuideArrowView.setVisibility(View.GONE);
            mGuideSwitch.setVisibility(View.VISIBLE);
            mGuideAuthenticationView.setOnClickListener(null);
            if (SettingManager.getInstance().getGuideMode() == 0) {
                mGuideSwitch.setChecked(true);
            } else {
                mGuideSwitch.setChecked(false);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.setting_login:
            login();
            break;
        case R.id.setting_profile:
            profile();
            break;
        case R.id.setting_guide_authentication:
            guideIdentify();
            break;
        case R.id.setting_update:
            checkUpdate();
            break;
        case R.id.setting_feedback:
            feedback();
            break;
        case R.id.setting_logout:
            logout();
            break;
        }
    }

    private void login() {
        Intent intent = new Intent(TourGuideApplication.getInstance(), LoginActivity.class);
        startActivity(intent);
    }

    private void guideIdentify() {
        Intent intent = new Intent(TourGuideApplication.getInstance(), GuideIdentifyActivity.class);
        startActivity(intent);
    }

    private void checkUpdate() {

    }

    private void feedback() {

    }

    private void profile() {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);
    }

    private void logout() {
        dismissDialog();
        mDialog = new AlertDialog.Builder(getActivity()).setMessage(R.string.logout_dialog_message)
                .setPositiveButton(R.string.logout_dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppRuntime.logout();
                        checkVisibility();
                    }
                }).setNegativeButton(R.string.logout_dialog_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        mDialog.show();
    }

    private void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

}
