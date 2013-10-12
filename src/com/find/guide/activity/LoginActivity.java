package com.find.guide.activity;

import com.find.guide.R;
import com.find.guide.app.TourGuideApplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends BaseActivity implements OnClickListener {

    private EditText mPhoneEt;
    private EditText mPasswordEt;
    private Button mLoginBtn;
    private Button mRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initUI();
    }

    private void initUI() {
        mPhoneEt = (EditText) findViewById(R.id.login_phone_et);
        mPasswordEt = (EditText) findViewById(R.id.login_password_et);
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mRegisterBtn = (Button) findViewById(R.id.register_btn);

        mLoginBtn.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);
        
        mActionbar.setTitle(R.string.login);
    }

    @Override
    protected void onDestroy() {
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
        // TODO
    }

    private void register() {
        Intent intent = new Intent(TourGuideApplication.getInstance(), RegisterActivity.class);
        startActivity(intent);
    }

}
