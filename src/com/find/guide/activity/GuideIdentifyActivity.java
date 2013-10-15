package com.find.guide.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.find.guide.R;
import com.find.guide.app.TourGuideApplication;
import com.find.guide.config.AppRuntime;
import com.find.guide.model.help.UserHelper;
import com.find.guide.model.help.UserHelper.OnApplyForGuideFinishListener;
import com.find.guide.utils.Toasts;
import com.find.guide.view.TipsDialog;
import com.plugin.common.utils.files.DiskManager;
import com.plugin.common.utils.files.DiskManager.DiskCacheType;
import com.plugin.common.utils.image.ImageUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

public class GuideIdentifyActivity extends BaseActivity implements OnClickListener {

    public static final int REQUEST_GALLERY = 1;
    public static final int REQUEST_CAMERA = 2;

    private ImageView mGuideCardIv;
    private EditText mGuideCardIdEt;
    private EditText mNameEt;
    private EditText mBirthdayEt;
    private EditText mBeGuideYearEt;
    private RadioButton mMaleRadio;
    private RadioButton mFemaleRadio;
    private Button mSubmitCheckBtn;

    private String mGuideCardPath = null;
    private Bitmap mGuideCardBitmap = null;

    private UserHelper mUserHelper = null;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guide_identify);
        initUI();

        mUserHelper = new UserHelper(getApplicationContext());
    }

    private void initUI() {
        mGuideCardIv = (ImageView) findViewById(R.id.guide_identify_guide_card_image);
        mGuideCardIdEt = (EditText) findViewById(R.id.guide_card_id_et);
        mNameEt = (EditText) findViewById(R.id.guide_identify_name_et);
        mBirthdayEt = (EditText) findViewById(R.id.guide_identify_birthday_et);
        mBeGuideYearEt = (EditText) findViewById(R.id.guide_identify_be_guide_year_et);
        mMaleRadio = (RadioButton) findViewById(R.id.guide_identify_male_radio);
        mFemaleRadio = (RadioButton) findViewById(R.id.guide_identify_female_radio);
        mSubmitCheckBtn = (Button) findViewById(R.id.guide_identify_submit_check_btn);

        mGuideCardIv.setOnClickListener(this);
        mSubmitCheckBtn.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUserHelper != null) {
            mUserHelper.destroy();
            mUserHelper = null;
        }
        if (mGuideCardBitmap != null && !mGuideCardBitmap.isRecycled()) {
            mGuideCardBitmap.recycle();
            mGuideCardBitmap = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.guide_identify_guide_card_image:
            selectPhoto();
            break;
        case R.id.guide_identify_submit_check_btn:
            submitCheck();
            break;
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
                mGuideCardPath = cursor.getString(columnIndex);
                cursor.close();

                if (!TextUtils.isEmpty(mGuideCardPath)) {
                    mGuideCardBitmap = ImageUtils.loadBitmapWithMemSizeOrientation(mGuideCardPath, 400 * 400);
                    mGuideCardIv.setImageBitmap(mGuideCardBitmap);
                }
            } else if (requestCode == REQUEST_CAMERA) {
                if (!TextUtils.isEmpty(mGuideCardPath)) {
                    mGuideCardBitmap = ImageUtils.loadBitmapWithMemSizeOrientation(mGuideCardPath, 400 * 400);
                    mGuideCardIv.setImageBitmap(mGuideCardBitmap);
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
        mGuideCardPath = createImageFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mGuideCardPath)));
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private String createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        String path = DiskManager.tryToFetchCachePathByType(DiskCacheType.PICTURE);
        return path + "/" + imageFileName;
    }

    private void submitCheck() {
        String sBirthday = mBirthdayEt.getText().toString();
        String sBeGuideYear = mBeGuideYearEt.getText().toString();
        String guideCardId = mGuideCardIdEt.getText().toString();

        if (TextUtils.isEmpty(sBirthday) || TextUtils.isEmpty(sBeGuideYear) || TextUtils.isEmpty(guideCardId)) {
            return;
        }

        long birthday = Long.parseLong(sBirthday);
        int beGuideYear = Integer.parseInt(sBeGuideYear);

        TipsDialog.getInstance().show(this, R.drawable.tips_loading, "认证中...", false);
        mUserHelper.applyForGuide("", birthday, beGuideYear, mGuideCardPath, guideCardId, AppRuntime.gLocation, 1,
                mApplyForGuideFinishListener);
    }

    private OnApplyForGuideFinishListener mApplyForGuideFinishListener = new OnApplyForGuideFinishListener() {

        @Override
        public void onApplyForGuideFinish(final int result) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TipsDialog.getInstance().dismiss();
                    if (result == UserHelper.APPLY_FOR_GUIDE_SUCCESS) {
                        finish();
                    } else {
                        Toasts.getInstance(TourGuideApplication.getInstance()).show("认证失败", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    };

}
