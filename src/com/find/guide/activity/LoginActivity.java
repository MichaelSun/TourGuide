package com.find.guide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.find.guide.R;
import com.find.guide.app.TourGuideApplication;
import com.find.guide.model.helper.UserHelper;
import com.find.guide.model.helper.UserHelper.OnLoginFinishListener;
import com.find.guide.setting.SettingManager;
import com.find.guide.view.TipsDialog;

public class LoginActivity extends BaseActivity implements OnClickListener {

    private static final int REQUEST_CODE_REGISTER = 1;

    private EditText mPhoneEt;
    private EditText mPasswordEt;
    private Button mLoginBtn;
    private Button mRegisterBtn;

    private UserHelper mLoginHelper = null;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        initUI();

        mLoginHelper = new UserHelper(getApplicationContext());
    }

    private void initUI() {
        mPhoneEt = (EditText) findViewById(R.id.login_phone_et);
        mPasswordEt = (EditText) findViewById(R.id.login_password_et);
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mRegisterBtn = (Button) findViewById(R.id.register_btn);

        mLoginBtn.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);

        String phoneNum = SettingManager.getInstance().getUserPhoneNum();
        if (!TextUtils.isEmpty(phoneNum)) {
            mPhoneEt.setText(phoneNum);
            mPhoneEt.clearFocus();
            mPasswordEt.requestFocus();
        }
    }

    @Override
    protected void onDestroy() {
        TipsDialog.getInstance().dismiss();
        if (mLoginHelper != null) {
            mLoginHelper.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.login_btn:
            login();
            break;
        case R.id.register_btn:
            register();
            break;
        }
    }

    private void login() {
        String phone = mPhoneEt.getText().toString();
        String password = mPasswordEt.getText().toString();
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)) {
            TipsDialog.getInstance().show(this, R.drawable.tips_loading, R.string.logining, true, false);
            mLoginHelper.login(phone, password, mOnLoginFinishListener);
        }
    }

    private OnLoginFinishListener mOnLoginFinishListener = new OnLoginFinishListener() {

        @Override
        public void onLoginFinish(final int result) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TipsDialog.getInstance().dismiss();
                    if (result == UserHelper.SUCCESS) {
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    private void register() {
        Intent intent = new Intent(TourGuideApplication.getInstance(), RegisterActivity.class);
        startActivityForResult(intent, REQUEST_CODE_REGISTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
            finish();
        }
    }

}
