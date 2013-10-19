package com.find.guide.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.find.guide.R;
import com.find.guide.model.helper.UserHelper;
import com.find.guide.model.helper.UserHelper.OnChangeHeadListener;
import com.find.guide.setting.SettingManager;
import com.find.guide.view.TipsDialog;
import com.plugin.common.utils.files.DiskManager;
import com.plugin.common.utils.files.DiskManager.DiskCacheType;
import com.plugin.common.view.WebImageView;

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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ProfileActivity extends BaseActivity implements OnClickListener {

    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;

    private String mHeadPath;

    private WebImageView mHeadIv;
    private TextView mPhoneTv;
    private TextView mNameTv;
    private TextView mGenderTv;

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
        mNameTv = (TextView) findViewById(R.id.name_tv);
        mGenderTv = (TextView) findViewById(R.id.gender_tv);

        mHeadIv.setOnClickListener(this);
    }

    private void initData() {
        SettingManager settingManager = SettingManager.getInstance();
        mHeadIv.setImageURI(new Uri.Builder().path(settingManager.getUserHeader()).build());
        mPhoneTv.setText(settingManager.getUserPhoneNum());
        mNameTv.setText(settingManager.getUserName());
        if (settingManager.getUserGender() == 1) {
            mGenderTv.setText(R.string.male);
        } else if (settingManager.getUserGender() == 2) {
            mGenderTv.setText(R.string.female);
        } else {
            mGenderTv.setText(R.string.gender_unknown);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!TextUtils.isEmpty(mHeadPath)) {
                changeHead();
            } else {
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!TextUtils.isEmpty(mHeadPath)) {
                changeHead();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void changeHead() {
        dismissDialog();
        mDialog = new AlertDialog.Builder(this).setMessage(R.string.change_head_dialog_msg)
                .setPositiveButton(R.string.change_head_dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TipsDialog.getInstance().show(ProfileActivity.this, R.drawable.tips_loading, "提交中...", true,
                                false);
                        mUserHelper.changeHead(mHeadPath, mOnChangeHeadListener);
                    }
                }).setNegativeButton(R.string.change_head_dialog_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create();
        mDialog.show();
    }

    private OnChangeHeadListener mOnChangeHeadListener = new OnChangeHeadListener() {
        @Override
        public void onChangeHead(final int result) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TipsDialog.getInstance().dismiss();
                    if (result == UserHelper.SUCCESS) {
                        finish();
                    } else {
                        TipsDialog.getInstance().show(ProfileActivity.this, R.drawable.tips_fail, "提交失败", true);
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
