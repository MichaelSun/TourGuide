package com.find.guide.activity;

import com.find.guide.R;
import com.find.guide.user.UserHelper;
import com.find.guide.user.UserHelper.OnChangePasswordListener;
import com.find.guide.view.TipsDialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChangePasswordActivity extends BaseActivity {

	private EditText mOldPwdEt;
	private EditText mNewPwdEt;
	private EditText mNewPwdConfirmEt;
	private Button mChangePwdBtn;

	private UserHelper mUserHelper = null;
	private Handler mHandler = new Handler(Looper.getMainLooper());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		mOldPwdEt = (EditText) findViewById(R.id.old_pwd_et);
		mOldPwdEt.addTextChangedListener(mOnEditorActionListener);
		mNewPwdEt = (EditText) findViewById(R.id.new_pwd_et);
		mNewPwdEt.addTextChangedListener(mOnEditorActionListener);
		mNewPwdConfirmEt = (EditText) findViewById(R.id.new_pwd_confirm_et);
		mNewPwdConfirmEt.addTextChangedListener(mOnEditorActionListener);

		mChangePwdBtn = (Button) findViewById(R.id.change_pwd_btn);
		mChangePwdBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				changePassword();
			}
		});

		mUserHelper = new UserHelper(this);
	}

	private TextWatcher mOnEditorActionListener = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			String oldPwd = mOldPwdEt.getText().toString();
			String newPwd = mNewPwdEt.getText().toString();
			String newPwdConfirm = mNewPwdConfirmEt.getText().toString();
			if (!TextUtils.isEmpty(oldPwd) && !TextUtils.isEmpty(newPwd)
					&& !TextUtils.isEmpty(newPwdConfirm)) {
				mChangePwdBtn.setEnabled(true);
			} else {
				mChangePwdBtn.setEnabled(false);
			}
		}

	};

	private void changePassword() {
		String oldPwd = mOldPwdEt.getText().toString();
		String newPwd = mNewPwdEt.getText().toString();
		String newPwdConfirm = mNewPwdConfirmEt.getText().toString();

		if (newPwd == null || newPwd.length() < 6 || newPwd.length() > 30) {
			TipsDialog.getInstance().show(this, R.drawable.tips_fail,
					"密码必须是6-30位数字或字符", true);
		} else if (!newPwd.equals(newPwdConfirm)) {
			TipsDialog.getInstance().show(this, R.drawable.tips_fail,
					"两次输入的密码不一致", true);
		} else {
			TipsDialog.getInstance().show(this, R.drawable.tips_loading, "",
					true, false);
			mUserHelper.changePassword(oldPwd, newPwd,
					mOnChangePasswordListener);
		}
	}

	private OnChangePasswordListener mOnChangePasswordListener = new OnChangePasswordListener() {

		@Override
		public void onChangedPassword(final int result) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					TipsDialog.getInstance().dismiss();
					if (result == UserHelper.SUCCESS) {
						mOldPwdEt.setText("");
						mNewPwdEt.setText("");
						mNewPwdConfirmEt.setText("");
						TipsDialog.getInstance().show(
								ChangePasswordActivity.this,
								R.drawable.tips_saved, "修改成功", true);
					} else if (result == UserHelper.FAILED) {
						TipsDialog.getInstance().show(
								ChangePasswordActivity.this,
								R.drawable.tips_fail, "修改失败", true);
					}
				}
			});
		}
	};

	@Override
	protected void onDestroy() {
		TipsDialog.getInstance().dismiss();
		if (mUserHelper != null) {
			mUserHelper.destroy();
			mUserHelper = null;
		}
		super.onDestroy();
	}

}
