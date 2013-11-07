package com.find.guide.activity;

import com.find.guide.R;
import com.plugin.common.view.WebGestureImageView;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

public class ViewLargeActivity extends Activity {

	public static final String INTENT_EXTRA_URL = "intent_extra_url";

	private String mImageUrl;

	private WebGestureImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_large);

		if (getIntent() != null)
			mImageUrl = getIntent().getStringExtra(INTENT_EXTRA_URL);

		mImageView = (WebGestureImageView) findViewById(R.id.image);
		mImageView.setImageURI(new Uri.Builder().path(mImageUrl).build());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
