package com.find.guide.fragment;

import com.find.guide.R;
import com.find.guide.activity.GuideIdentifyActivity;
import com.find.guide.activity.LoginActivity;
import com.find.guide.app.TourGuideApplication;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class SettingFragment extends Fragment implements OnClickListener {

    private View mLoginRegisterView;
    private View mGuideAuthenticationView;
    private View mUpdateView;
    private View mFeedBackView;
    private View mLogoutView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, null);

        mLoginRegisterView = view.findViewById(R.id.setting_login_register);
        mGuideAuthenticationView = view.findViewById(R.id.setting_guide_authentication);
        mUpdateView = view.findViewById(R.id.setting_update);
        mFeedBackView = view.findViewById(R.id.setting_feedback);
        mLogoutView = view.findViewById(R.id.setting_logout);

        mLoginRegisterView.setOnClickListener(this);
        mGuideAuthenticationView.setOnClickListener(this);
        mUpdateView.setOnClickListener(this);
        mFeedBackView.setOnClickListener(this);
        mLogoutView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.setting_login_register:
            loginOrRegister();
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

    private void loginOrRegister() {
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

    private void logout() {

    }

}
