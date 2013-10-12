package com.find.guide.activity;

import com.find.guide.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends BaseActivity implements OnClickListener {

    private EditText mPhoneEt;
    private EditText mPasswordEt;
    private EditText mPasswordConfirmEt;
    private EditText mVerifyCodeEt;
    private TextView mVerifyCodeTimeTv;
    private Button mRegisterBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        initUI();
    }

    private void initUI() {
        mPhoneEt = (EditText) findViewById(R.id.register_phone_et);
        mPasswordEt = (EditText) findViewById(R.id.register_password_et);
        mPasswordConfirmEt = (EditText) findViewById(R.id.register_password_confirm_et);
        mVerifyCodeEt = (EditText) findViewById(R.id.register_verification_code_et);
        mVerifyCodeTimeTv = (TextView) findViewById(R.id.register_verification_code_time_tv);
        mRegisterBtn = (Button) findViewById(R.id.register_btn);

        mVerifyCodeTimeTv.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);
        
        mActionbar.setTitle(R.string.register);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.register_btn:
            register();
            break;
        case R.id.register_verification_code_time_tv:
            resendCode();
            break;
        }
    }

    private void register() {
        // TODO
    }

    private void resendCode() {
        // TODO
    }

}
