package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView.ScaleType;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.handmark.pulltorefresh.library.R;

public class CharmLoadingLayout extends LoadingLayout {

	static final int ROTATION_ANIMATION_DURATION = 1200;

	private final Animation mRotateAnimation;
	private final Matrix mHeaderImageMatrix;
	
	private CharmLoadingDrawable mCharmDrawable;

	private float mRotationPivotX, mRotationPivotY;

	public CharmLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);
		mHeaderImage.setScaleType(ScaleType.MATRIX);
		mHeaderImageMatrix = new Matrix();
		mHeaderImage.setImageMatrix(mHeaderImageMatrix);

		mRotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		Interpolator interpolator = new Interpolator() {
			
			@Override
			public float getInterpolation(float input) {
				// TODO Auto-generated method stub
				return input;
			}
		};
		mRotateAnimation.setInterpolator(interpolator);
		mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
		mRotateAnimation.setRepeatCount(Animation.INFINITE);
		mRotateAnimation.setRepeatMode(Animation.RESTART);
	}

	@Override
	protected Drawable getDefaultDrawable() {
		Drawable mSpinner = getContext().getResources().getDrawable(R.drawable.charm_pull_to_refresh_spinner);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.charm_pull_to_refresh_spinner);
		mCharmDrawable = new CharmLoadingDrawable(getResources(), bitmap);
		mCharmDrawable.setBounds(0, 0, mSpinner.getIntrinsicWidth(), mSpinner.getIntrinsicHeight());
		return mCharmDrawable;
	}

	@Override
	protected void onLoadingDrawableSet(Drawable imageDrawable) {
		if (null != imageDrawable) {
			mRotationPivotX = Math.round(imageDrawable.getIntrinsicWidth() / 2f);
			mRotationPivotY = Math.round(imageDrawable.getIntrinsicHeight() / 2f);
		}
	}

	@Override
	protected void onPullImpl(float scaleOfLayout) {
		float angle;
		angle = (float) ((scaleOfLayout - 1.5) * 90f);
		float scale = 0;
		if (scaleOfLayout > 1.5) {
			scale = 1;
			mCharmDrawable.switchToSpinner();
			mHeaderImageMatrix.setRotate(angle, mRotationPivotX, mRotationPivotY);
			mHeaderImage.setImageMatrix(mHeaderImageMatrix);
		} else {
			scale = scaleOfLayout / 1.5f;
			mCharmDrawable.switchToCircle();
			mCharmDrawable.updateScale(scale);
		}
	}

	@Override
	protected void pullToRefreshImpl() {
	}

	@Override
	protected void refreshingImpl() {
		mCharmDrawable.switchToSpinner();
		mHeaderImage.startAnimation(mRotateAnimation);
	}

	@Override
	protected void releaseToRefreshImpl() {
	}

	@Override
	protected void resetImpl() {
		mHeaderImage.clearAnimation();
		resetImageRotation();
	}

	private void resetImageRotation() {
		if (null != mHeaderImageMatrix) {
			mHeaderImageMatrix.reset();
			mHeaderImage.setImageMatrix(mHeaderImageMatrix);
		}
	}

}
