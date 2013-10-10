package com.handmark.pulltorefresh.library.internal;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

public class CharmLoadingDrawable extends BitmapDrawable {

	private Paint mPaint;

	private RectF mRect;

	private float mScale;
	
	private boolean mIsSpinner = false;

	public CharmLoadingDrawable(Resources res) {
		super(res);
		init();
	}

	public CharmLoadingDrawable(Resources res, Bitmap bitmap) {
		super(res, bitmap);
		init();
	}

//	public CharmLoadingDrawable(Resources res, String filepath) {
//		super(res, filepath);
//		init();
//	}
//
//	public CharmLoadingDrawable(Resources res, InputStream is) {
//		super(res, is);
//		init();
//	}

	private void init() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}

	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		if (mRect == null) {
			mRect = new RectF(left, top, right, bottom);
			super.setBounds(left, top, right, bottom);
		}
	}

	public void updateScale(float scale) {
		mScale = scale;
		invalidateSelf();
	}
	
	public void switchToSpinner() {
		if (!mIsSpinner) {
			mIsSpinner = true;
			invalidateSelf();
		}
	}
	
	public void switchToCircle() {
		if (mIsSpinner) {
			mIsSpinner = false;
			invalidateSelf();
		}
	}

	@Override
	public void draw(Canvas canvas) {

		if (mIsSpinner) {
//			canvas.drawBitmap(getBitmap(), 0, 0, mPaint);
			super.draw(canvas);
		} else {
			//c8c8c8
			int startAlhpa = 100;
			int endAlhpa = 255;
			int alpha = (int) (startAlhpa + (endAlhpa - startAlhpa) * mScale);
			//c6c6c6
			mPaint.setColor(0x00C8C8C8 | (alpha << 24));
			float scale = 0.5f + mScale / 2;
			float w = mRect.right * scale;
			float l = (mRect.right - w) / 2;
			RectF rect = new RectF(0, 0, w, w);
			canvas.translate(l, l);
			canvas.drawArc(rect, 0, 360, true, mPaint);
			canvas.translate(-l, -l);
			
			//e2e4e5
			scale = mScale > 0.3 ? (mScale - 0.3f) / 0.7f : 0f;
			w = mRect.right * scale * 0.85f;
			RectF innerRect = new RectF(0, 0, w, w);
			mPaint.setColor(0xFFE2E4E5);
			canvas.translate((mRect.right - w) / 2, (mRect.right - w) / 2);
			canvas.drawArc(innerRect, 0, 360, true, mPaint);
		}
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mPaint.setColorFilter(cf);
	}

}
