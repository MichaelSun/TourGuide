package com.find.guide.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.find.guide.R;
import com.find.guide.config.AppConfig;
import com.find.guide.utils.CropAndScale;
import com.plugin.common.utils.files.DiskManager;
import com.plugin.common.utils.files.DiskManager.DiskCacheType;
import com.plugin.common.utils.image.ExifHelper;
import com.plugin.common.utils.image.ImageUtils;
import com.polites.android.GestureImageView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class CropImageActivity extends Activity implements OnClickListener {

	private static final int REQUEST_GALLERY = 1;
	private static final int REQUEST_CAMERA = 2;

	public static final String EXTRA_SELECT_IMAGE_MODE = "extra_select_image_mode";
	/**
	 * 1-camera 2-gallery
	 */
	private int mSelectImageMode = 1;

	public static final String EXTRA_CROP_IMAGE_PATH = "extra_crop_image_path";

	private GestureImageView mImageView;

	private Bitmap mSourceBitmap = null;
	private String mSourceImagePath = null;
	private String mCropImagePath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop_image);

		if (getIntent() != null) {
			mSelectImageMode = getIntent().getIntExtra(EXTRA_SELECT_IMAGE_MODE,
					1);
		}

		initUI();

		if (mSelectImageMode == 1) {
			selectFromCamera();
		} else {
			selectFromGallery();
		}
	}

	private void initUI() {
		mImageView = (GestureImageView) findViewById(R.id.image);
		mImageView.setIsCrop(true);

		findViewById(R.id.crop_cancel).setOnClickListener(this);
		findViewById(R.id.crop_save).setOnClickListener(this);

		View topView = findViewById(R.id.top_rect);
		View bottomView = findViewById(R.id.bottom_rect);
		View leftView = findViewById(R.id.left_rect);
		View rightView = findViewById(R.id.right_rect);

		DisplayMetrics dm = getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		int marginTop = (screenHeight - screenWidth) >> 1;

		RelativeLayout.LayoutParams topLp = (LayoutParams) topView
				.getLayoutParams();
		topLp.topMargin = marginTop;
		RelativeLayout.LayoutParams bottomLp = (LayoutParams) bottomView
				.getLayoutParams();
		bottomLp.topMargin = marginTop + screenWidth;
		RelativeLayout.LayoutParams leftLp = (LayoutParams) leftView
				.getLayoutParams();
		leftLp.topMargin = marginTop;
		leftLp.height = screenWidth;
		RelativeLayout.LayoutParams rightLp = (LayoutParams) rightView
				.getLayoutParams();
		rightLp.topMargin = marginTop;
		rightLp.height = screenWidth;
	}

	private void selectFromGallery() {
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, REQUEST_GALLERY);
	}

	private void selectFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		mSourceImagePath = createImageFile();
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(mSourceImagePath)));
		startActivityForResult(intent, REQUEST_CAMERA);
	}

	@SuppressLint("SimpleDateFormat")
	private String createImageFile() {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "IMG_" + timeStamp + "_";
		String path = DiskManager
				.tryToFetchCachePathByType(DiskCacheType.PICTURE);
		return path + "/" + imageFileName;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_GALLERY) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				mSourceImagePath = cursor.getString(columnIndex);
				cursor.close();
				if (!TextUtils.isEmpty(mSourceImagePath)) {
					setImageBitmap(mSourceImagePath);
				} else {
					finish();
				}
			} else if (requestCode == REQUEST_CAMERA) {
				setImageBitmap(mSourceImagePath);
			}
		} else {
			finish();
		}
	}

	private void setImageBitmap(String path) {
		if (!TextUtils.isEmpty(path)) {
			mSourceBitmap = ImageUtils.loadBitmapWithSizeOrientation(path);
			mImageView.setImageBitmap(mSourceBitmap);
		}
	}

	@Override
	protected void onDestroy() {
		if (mSourceBitmap != null && !mSourceBitmap.isRecycled()) {
			mSourceBitmap.recycle();
			mSourceBitmap = null;
		}

		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.crop_cancel:
			back();
			break;
		case R.id.crop_save:
			save();
			break;
		}
	}

	private void back() {
		finish();
	}

	private void save() {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mSourceImagePath, opt);
		int sourceWidth = opt.outWidth;
		int sourceHeight = opt.outHeight;
		int rotation = ExifHelper.getRotationFromExif(mSourceImagePath);

		LOGD("sourceWidth+++++++++++++++" + sourceWidth);
		LOGD("sourceHeight+++++++++++++++" + sourceHeight);
		LOGD("sourceRotation++++++++++++++" + rotation);

		final Rect rect = new Rect();
		rect.left = (int) mImageView.getRectLeft();
		rect.top = (int) mImageView.getRectTop();
		rect.right = (int) mImageView.getRectRight();
		rect.bottom = (int) mImageView.getRectBottom();
		final Rect newRect = new Rect();
		if (mSourceImagePath != null && mSourceBitmap != null
				&& mSourceBitmap.getWidth() != 0
				&& mSourceBitmap.getHeight() != 0) {
			float leftScale = (float) rect.left / mSourceBitmap.getWidth();
			float rightScale = (float) rect.right / mSourceBitmap.getWidth();
			float topScale = (float) rect.top / mSourceBitmap.getHeight();
			float bottomScale = (float) rect.bottom / mSourceBitmap.getHeight();

			LOGD("leftScale+++++++++++++++" + leftScale);
			LOGD("rightScale+++++++++++++++" + rightScale);

			switch (rotation) {
			case ExifHelper.ROTATION_0:
				newRect.left = (int) (leftScale * sourceWidth);
				newRect.right = (int) (rightScale * sourceWidth);
				newRect.top = (int) (topScale * sourceHeight);
				newRect.bottom = (int) (bottomScale * sourceHeight);
				break;
			case ExifHelper.ROTATION_90:
				newRect.left = (int) (topScale * sourceWidth);
				newRect.right = (int) (bottomScale * sourceWidth);
				newRect.top = (int) ((1.0 - rightScale) * sourceHeight);
				newRect.bottom = (int) ((1.0 - leftScale) * sourceHeight);
				break;
			case ExifHelper.ROTATION_180:
				newRect.left = (int) ((1.0 - rightScale) * sourceWidth);
				newRect.right = (int) ((1.0 - leftScale) * sourceWidth);
				newRect.top = (int) ((1.0 - bottomScale) * sourceHeight);
				newRect.bottom = (int) ((1.0 - topScale) * sourceHeight);
				break;
			case ExifHelper.ROTATION_270:
				newRect.left = (int) ((1.0 - bottomScale) * sourceWidth);
				newRect.right = (int) ((1.0 - topScale) * sourceWidth);
				newRect.top = (int) (leftScale * sourceHeight);
				newRect.bottom = (int) (rightScale * sourceHeight);
				break;
			default:
				newRect.left = (int) (leftScale * sourceWidth);
				newRect.right = (int) (rightScale * sourceWidth);
				newRect.top = (int) (topScale * sourceHeight);
				newRect.bottom = (int) (bottomScale * sourceHeight);
				break;
			}
		}
		mCropImagePath = cropBitmapWithlib(mSourceImagePath, newRect,
				getApplicationContext());

		Intent intent = new Intent();
		intent.putExtra(EXTRA_CROP_IMAGE_PATH, mCropImagePath);
		setResult(RESULT_OK, intent);
		finish();
	}

	public String cropBitmapWithlib(String srcPath, Rect cropRect,
			Context context) {
		if (srcPath == null || cropRect == null || cropRect.width() <= 0
				|| cropRect.height() <= 0) {
			return null;
		}
		CropAndScale cropAndScale = new CropAndScale(context);
		String dir = DiskManager.tryToFetchCachePathByType(DiskCacheType.BASE)
				+ "header_crop";
		File file = new File(dir);
		file.mkdirs();
		String retPath = new File(file, System.currentTimeMillis()
				+ "upload.jpg").getAbsolutePath();
		int targetWidth = 720;
		int targetHeight = 720;
		if (cropRect.width() < targetWidth || cropRect.height() < targetHeight) {
			targetWidth = cropRect.width();
			targetHeight = cropRect.height();
		}
		int rotation = ExifHelper.getRotationFromExif(srcPath);

		int jResult = cropAndScale.cropJpg(srcPath, retPath, cropRect.left,
				cropRect.top, cropRect.width(), cropRect.height(), targetWidth,
				targetHeight, rotation);
		cropAndScale.dispose();
		if (jResult == 0) {
			return null;
		}
		return retPath;
	}

	private void LOGD(String msg) {
		if (!TextUtils.isEmpty(msg)) {
			AppConfig.LOGD(CropImageActivity.class.getSimpleName() + " " + msg);
		}
	}

}
