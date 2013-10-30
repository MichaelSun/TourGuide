package com.find.guide.activity;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.find.guide.R;
import com.find.guide.app.TourGuideApplication;
import com.find.guide.city.CityItem;
import com.find.guide.config.AppRuntime;
import com.find.guide.user.UserHelper;
import com.find.guide.user.UserHelper.OnApplyForGuideFinishListener;
import com.find.guide.utils.Toasts;
import com.find.guide.view.TipsDialog;
import com.plugin.common.utils.image.ImageUtils;

public class GuideIdentifyActivity extends BaseActivity implements OnClickListener {

    private static final int REQUEST_CROP_IMAGE = 1;
    public static final int REQUEST_CODE_SELECT_CITY = 2;

    private ImageView mGuideCardIv;
    private EditText mGuideCardIdEt;
    private TextView mBirthdayTv;
    private View mBirthdayView;
    private EditText mBeGuideYearEt;
    private EditText mGoodAtScenicEt;
    private View mCityView;
    private TextView mCityTv;
    private Button mSubmitCheckBtn;

    private String mGuideCardPath = null;
    private Bitmap mGuideCardBitmap = null;

    private DatePickerDialog mDialog = null;

    private CityItem mCityItem = new CityItem("北京", "B", 20001);

    private long mBirthdayTimestamp = 0l;

    private UserHelper mUserHelper = null;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guide_identify);
        initUI();
        initData();

        mUserHelper = new UserHelper(getApplicationContext());
    }

    private void initUI() {
        mGuideCardIv = (ImageView) findViewById(R.id.guide_identify_guide_card_image);
        mGuideCardIdEt = (EditText) findViewById(R.id.guide_card_id_et);
        mBirthdayTv = (TextView) findViewById(R.id.guide_identify_birthday_tv);
        mBirthdayView = findViewById(R.id.guide_identify_birthday_layout);
        mBeGuideYearEt = (EditText) findViewById(R.id.guide_identify_be_guide_year_et);
        mGoodAtScenicEt = (EditText) findViewById(R.id.guide_identify_scenic_et);
        mCityView = findViewById(R.id.city_layout);
        mCityTv = (TextView) findViewById(R.id.city_tv);
        mSubmitCheckBtn = (Button) findViewById(R.id.guide_identify_submit_check_btn);

        mGuideCardIv.setOnClickListener(this);
        mSubmitCheckBtn.setOnClickListener(this);
        mBirthdayView.setOnClickListener(this);
        mCityView.setOnClickListener(this);
    }

    private void initData() {
        mBirthdayTimestamp = getTimestamp(1990, 0, 1);
        mBirthdayTv.setText("1990/01/01");
        mCityTv.setText(mCityItem.getCityName());
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
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
        TipsDialog.getInstance().dismiss();
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
        case R.id.city_layout:
            selectCity();
            break;
        case R.id.guide_identify_birthday_layout:
            selectBirthday();
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CROP_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                String path = data.getStringExtra(CropImageActivity.EXTRA_CROP_IMAGE_PATH);
                if (!TextUtils.isEmpty(path)) {
                    mGuideCardPath = path;
                    mGuideCardBitmap = ImageUtils.loadBitmapWithSizeOrientation(mGuideCardPath);
                    if (mGuideCardBitmap != null && !mGuideCardBitmap.isRecycled())
                        mGuideCardIv.setImageBitmap(mGuideCardBitmap);
                }
            }
        } else if (requestCode == REQUEST_CODE_SELECT_CITY) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    mCityItem = (CityItem) data.getSerializableExtra(SelectCityActivity.INTENT_EXTRA_CITY);
                    if (mCityItem != null) {
                        mCityTv.setText(mCityItem.getCityName());
                    }
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
        Intent i = new Intent(this, CropImageActivity.class);
        i.putExtra(CropImageActivity.EXTRA_SELECT_IMAGE_MODE, 2);
        startActivityForResult(i, REQUEST_CROP_IMAGE);
    }

    private void selectFromCamera() {
        Intent i = new Intent(this, CropImageActivity.class);
        i.putExtra(CropImageActivity.EXTRA_SELECT_IMAGE_MODE, 1);
        startActivityForResult(i, REQUEST_CROP_IMAGE);
    }

    private void selectCity() {
        Intent intent = new Intent(this, SelectCityActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_CITY);
    }

    private void selectBirthday() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        Calendar calendar = Calendar.getInstance();
        mDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year + "/" + (monthOfYear + 1 < 10 ? "0" + (monthOfYear + 1) : monthOfYear + 1) + "/"
                        + (dayOfMonth < 10 ? "0" + (dayOfMonth) : dayOfMonth);
                mBirthdayTv.setText(date);
                mBirthdayTimestamp = getTimestamp(year, monthOfYear, dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        mDialog.show();
    }

    private long getTimestamp(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    private void submitCheck() {
        String sBeGuideYear = mBeGuideYearEt.getText().toString();
        String guideCardId = mGuideCardIdEt.getText().toString();
        String goodAtScenic = mGoodAtScenicEt.getText().toString();

        if (TextUtils.isEmpty(mGuideCardPath) || TextUtils.isEmpty(sBeGuideYear) || TextUtils.isEmpty(guideCardId)
                || TextUtils.isEmpty(goodAtScenic)) {
            return;
        }

        int beGuideYear = 0;
        try {
            beGuideYear = Integer.parseInt(sBeGuideYear);
        } catch (NumberFormatException e) {
            TipsDialog.getInstance().show(this, R.drawable.tips_fail, "领证年份格式不对", true, false);
            return;
        }

        TipsDialog.getInstance().show(this, R.drawable.tips_loading, "导游认证中...", true, false);
        mUserHelper
                .applyForGuide(goodAtScenic, mBirthdayTimestamp, beGuideYear, mGuideCardPath, guideCardId,
                        AppRuntime.gLocation, mCityItem != null ? mCityItem.getCityCode() : 20001,
                        mApplyForGuideFinishListener);
    }

    private OnApplyForGuideFinishListener mApplyForGuideFinishListener = new OnApplyForGuideFinishListener() {

        @Override
        public void onApplyForGuideFinish(final int result) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TipsDialog.getInstance().dismiss();
                    if (result == UserHelper.SUCCESS) {
                        finish();
                    } else if (result == UserHelper.FAILED) {
                        Toasts.getInstance(TourGuideApplication.getInstance()).show("认证失败", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    };

}
