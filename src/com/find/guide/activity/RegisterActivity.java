package com.find.guide.activity;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.find.guide.R;
import com.find.guide.model.UserHelper;
import com.find.guide.model.UserHelper.OnGetVerifyCodeFinishListener;
import com.find.guide.model.UserHelper.OnRegisterFinishListener;
import com.find.guide.view.TipsDialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends BaseActivity implements OnClickListener {

    private EditText mPhoneEt;
    private EditText mPasswordEt;
    private EditText mPasswordConfirmEt;
    private EditText mNameEt;
    private EditText mVerifyCodeEt;
    private TextView mSendVerifyCodeTv;
    private Button mRegisterBtn;

    private UserHelper mLoginHelper = null;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Timer mTimer = null;
    private int mResendSeconds = 60;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        initUI();

        mLoginHelper = new UserHelper(getApplicationContext());
    }

    private void initUI() {
        mPhoneEt = (EditText) findViewById(R.id.register_phone_et);
        mPasswordEt = (EditText) findViewById(R.id.register_password_et);
        mPasswordConfirmEt = (EditText) findViewById(R.id.register_password_confirm_et);
        mNameEt = (EditText) findViewById(R.id.register_name_et);
        mVerifyCodeEt = (EditText) findViewById(R.id.register_verification_code_et);
        mSendVerifyCodeTv = (TextView) findViewById(R.id.register_get_verify_code_tv);
        mRegisterBtn = (Button) findViewById(R.id.register_btn);

        mSendVerifyCodeTv.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);

    }

    @Override
    public void onDestroy() {
        TipsDialog.getInstance().dismiss();
        if (mLoginHelper != null) {
            mLoginHelper.destroy();
            mLoginHelper = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.register_btn:
            register();
            break;
        case R.id.register_get_verify_code_tv:
            getVerifyCode();
            break;
        }
    }

    private void register() {
        String phone = mPhoneEt.getText().toString();
        String password = mPasswordEt.getText().toString();
        String password_confirm = mPasswordConfirmEt.getText().toString();
        String name = mNameEt.getText().toString();
        String verifyCode = mVerifyCodeEt.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            showError(getString(R.string.register_phone_empty));
        } else if (TextUtils.isEmpty(password)) {
            showError(getString(R.string.register_password_empty));
        } else if (!password.equals(password_confirm)) {
            showError(getString(R.string.register_password_diff));
        } else if (TextUtils.isEmpty(name)) {
            showError(getString(R.string.register_name_empty));
        } else if (TextUtils.isEmpty(verifyCode)) {
            showError(getString(R.string.register_verify_code_empty));
        } else {
            TipsDialog.getInstance().show(this, R.drawable.tips_loading, R.string.registering, false);
            mLoginHelper.register(phone, password, name, verifyCode, 1, mOnRegisterFinishListener);
        }

    }

    OnRegisterFinishListener mOnRegisterFinishListener = new OnRegisterFinishListener() {

        @Override
        public void onRegisterFinish(final int result) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (result == UserHelper.REGISTER_SUCCESS) {
                        finish();
                    } else {
                        showError(getString(R.string.register_failed));
                    }
                }
            });
        }
    };

    private void getVerifyCode() {
        String phone = mPhoneEt.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showError(getString(R.string.register_phone_empty));
        } else {
            mLoginHelper.getVerifyCode(phone, mOnGetVerifyCodeFinishListener);
            mResendSeconds = 60;
            if (mTimer == null) {
                mTimer = new Timer();
            }
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mResendSeconds == 0) {
                                mTimer.cancel();
                                mTimer = null;
                                
                                mSendVerifyCodeTv.setText(R.string.register_send_verify_code);
                                mSendVerifyCodeTv.setClickable(true);
                            } else {
                                mSendVerifyCodeTv.setText(mResendSeconds + "s");
                            }
                        }
                    });
                    mResendSeconds--;
                }
            }, new Date(), 1000);
            mSendVerifyCodeTv.setClickable(false);
        }
    }

    OnGetVerifyCodeFinishListener mOnGetVerifyCodeFinishListener = new OnGetVerifyCodeFinishListener() {

        @Override
        public void onGetVerifyCodeFinish(int result) {

        }
    };

    private void showError(String errorMsg) {
        TipsDialog.getInstance().show(this, R.drawable.tips_fail, errorMsg, true);
    }

}
