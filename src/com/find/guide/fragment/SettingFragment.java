package com.find.guide.fragment;

import com.find.guide.R;
import com.find.guide.activity.GuideIdentifyActivity;
import com.find.guide.activity.LoginActivity;
import com.find.guide.app.TourGuideApplication;
import com.find.guide.config.AppRuntime;
import com.find.guide.setting.SettingManager;
import com.find.guide.utils.Toasts;

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
import android.widget.Toast;

public class SettingFragment extends Fragment implements OnClickListener {

    private View mLoginRegisterView;
    private View mGuideAuthenticationView;
    private View mUpdateView;
    private View mFeedBackView;
    private View mLogoutView;

    private Dialog mDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, null);

        mLoginRegisterView = view.findViewById(R.id.setting_login);
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
    public void onDestroy() {
        dismissDialog();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.setting_login:
            login();
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
        if (SettingManager.getInstance().getUserId() > 0) {
            logout();
        } else {
            Intent intent = new Intent(TourGuideApplication.getInstance(), LoginActivity.class);
            startActivity(intent);
        }
    }

    private void guideIdentify() {
        if (SettingManager.getInstance().getUserId() > 0) {
            Intent intent = new Intent(TourGuideApplication.getInstance(), GuideIdentifyActivity.class);
            startActivity(intent);
        } else {
            Toasts.getInstance(TourGuideApplication.getInstance()).show(R.string.need_login, Toast.LENGTH_SHORT);
        }
    }

    private void checkUpdate() {

    }

    private void feedback() {

    }

    private void logout() {
        if (SettingManager.getInstance().getUserId() > 0) {
            dismissDialog();
            mDialog = new AlertDialog.Builder(getActivity()).setMessage(R.string.logout_dialog_message)
                    .setPositiveButton(R.string.logout_dialog_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AppRuntime.logout();
                        }
                    }).setNegativeButton(R.string.logout_dialog_negative, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
            mDialog.show();
        }
    }

    private void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

}
