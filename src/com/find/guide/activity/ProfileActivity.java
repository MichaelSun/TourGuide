package com.find.guide.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.find.guide.R;
import com.find.guide.model.helper.UserHelper;
import com.find.guide.model.helper.UserHelper.OnChangeUserInfoListener;
import com.find.guide.setting.SettingManager;
import com.find.guide.view.TipsDialog;
import com.plugin.common.utils.files.DiskManager;
import com.plugin.common.utils.files.DiskManager.DiskCacheType;
import com.plugin.common.view.WebImageView;

public class ProfileActivity extends BaseActivity implements OnClickListener {

    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;

    private String mHeadPath;

    private WebImageView mHeadIv;
    private TextView mPhoneTv;
    private EditText mNameEt;
    private RadioGroup mGenderRadio;
    private RadioButton mMaleRadio;
    private RadioButton mFemaleRadio;
    private RadioButton mUnknownRadio;

    private Dialog mDialog;

    private UserHelper mUserHelper;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mUserHelper = new UserHelper(getApplicationContext());

        initUI();
        initData();
    }

    private void initUI() {
        mHeadIv = (WebImageView) findViewById(R.id.header_image);
        mPhoneTv = (TextView) findViewById(R.id.phone_tv);
        mNameEt = (EditText) findViewById(R.id.name_et);
        mGenderRadio = (RadioGroup) findViewById(R.id.gender_radio);
        mMaleRadio = (RadioButton) findViewById(R.id.male_radio);
        mFemaleRadio = (RadioButton) findViewById(R.id.female_radio);
        mUnknownRadio = (RadioButton) findViewById(R.id.unknown_radio);

        mHeadIv.setOnClickListener(this);
    }

    private void initData() {
        SettingManager settingManager = SettingManager.getInstance();
        mHeadIv.setImageURI(new Uri.Builder().path(settingManager.getUserHeader()).build());
        mPhoneTv.setText(settingManager.getUserPhoneNum());
        mNameEt.setText(settingManager.getUserName());
        if (settingManager.getUserGender() == 1) {
            mMaleRadio.setChecked(true);
        } else if (settingManager.getUserGender() == 2) {
            mFemaleRadio.setChecked(true);
        } else {
            mUnknownRadio.setChecked(true);
        }
    }

    @Override
    protected void onDestroy() {
        if (mUserHelper != null) {
            mUserHelper.destroy();
            mUserHelper = null;
        }
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
        TipsDialog.getInstance().dismiss();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.header_image) {
            selectPhoto();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mHeadPath = cursor.getString(columnIndex);
                cursor.close();

                if (!TextUtils.isEmpty(mHeadPath)) {
                    mHeadIv.setImageURI(new Uri.Builder().path("file://" + mHeadPath).build());
                }
            } else if (requestCode == REQUEST_CAMERA) {
                if (!TextUtils.isEmpty(mHeadPath)) {
                    mHeadIv.setImageURI(new Uri.Builder().path("file://" + mHeadPath).build());
                }
            }
        }
    }

    private void selectPhoto() {
        String[] items = new String[] { "图库", "拍照" };
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("选择图片")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            selectFromGallery();
                        } else {
                            selectFromCamera();
                        }
                    }
                }).create();
        dialog.show();
    }

    private void selectFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_GALLERY);
    }

    private void selectFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mHeadPath = createImageFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mHeadPath)));
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @SuppressLint("SimpleDateFormat")
    private String createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        String path = DiskManager.tryToFetchCachePathByType(DiskCacheType.PICTURE);
        return path + "/" + imageFileName;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            beforeBackCheck();
            return true;
        } else if (item.getItemId() == R.id.menu_save) {
            changeUserInfo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            beforeBackCheck();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void beforeBackCheck() {
        if (hasChanged()) {
            dismissDialog();
            mDialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.change_user_info_dialog_msg)
                    .setPositiveButton(R.string.change_user_info_dialog_positive,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                    .setNegativeButton(R.string.change_user_info_dialog_negative,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create();
            mDialog.show();
        } else {
            finish();
        }
    }

    private boolean hasChanged() {
        String name = mNameEt.getText().toString();
        int gender;
        if (mGenderRadio.getCheckedRadioButtonId() == R.id.male_radio) {
            gender = 1;
        } else if (mGenderRadio.getCheckedRadioButtonId() == R.id.female_radio) {
            gender = 2;
        } else {
            gender = 0;
        }
        if (!TextUtils.isEmpty(mHeadPath) || name == null || !name.equals(SettingManager.getInstance().getUserName())
                || gender != SettingManager.getInstance().getUserGender()) {
            return true;
        }
        return false;
    }

    private void changeUserInfo() {
        if (hasChanged()) {
            String name = mNameEt.getText().toString();
            int gender;
            if (mGenderRadio.getCheckedRadioButtonId() == R.id.male_radio) {
                gender = 1;
            } else if (mGenderRadio.getCheckedRadioButtonId() == R.id.female_radio) {
                gender = 2;
            } else {
                gender = 0;
            }

            TipsDialog.getInstance().show(ProfileActivity.this, R.drawable.tips_loading, "修改个人信息中...", true, false);
            mUserHelper.changeUserInfo(name, gender, mHeadPath, mOnChangeUserInfoListener);
        }

    }

    private OnChangeUserInfoListener mOnChangeUserInfoListener = new OnChangeUserInfoListener() {
        @Override
        public void onChangeUserInfo(final int result) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TipsDialog.getInstance().dismiss();
                    if (result == UserHelper.SUCCESS) {
                        mHeadPath = null;
                        TipsDialog.getInstance().show(ProfileActivity.this, R.drawable.tips_saved, "修改成功", true);
                    } else if (result == UserHelper.FAILED) {
                        TipsDialog.getInstance().show(ProfileActivity.this, R.drawable.tips_fail, "修改失败", true);
                    }
                }
            });
        }
    };

    private void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

}
