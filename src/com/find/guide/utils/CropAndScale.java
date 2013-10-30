package com.find.guide.utils;

import com.plugin.common.utils.DebugLog;

import android.content.Context;

public class CropAndScale {

	static {
		System.loadLibrary("jpng");
	}

	private long mGlobalFlags = 0;
	private static final String TAG = "CropAndScale";

	public CropAndScale(Context context) {
		mGlobalFlags = createGlobalFlags(context);
		DebugLog.d("TAG","mGlobalFlags====================="+mGlobalFlags);
	}

	private native long createGlobalFlags(Object context);

	private native void close(long gfp);

	public native int nativeCropJpg(long gfp, String inFile, String outFile, int x, int y, int rectW, int rectH,
			int scaleW, int scaleH, int rotation);

	public int cropJpg(String inFile, String outFile, int x, int y, int rectW, int rectH, int scaleW, int scaleH,
			int rotation) {
		long start = System.currentTimeMillis();
		DebugLog.d(TAG, "start croping");
		try {
			return nativeCropJpg(mGlobalFlags, inFile, outFile, x, y, rectW, rectH, scaleW, scaleH, rotation);
		} finally {
			long end = System.currentTimeMillis();
			DebugLog.d(TAG, "cost=" + (end - start));
		}
	}

	public void dispose() {
		close(mGlobalFlags);
	}

}
