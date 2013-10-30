#include "image_utils.h"
//#include "com_example_autofixtest_AutoFix.h"
#include <math.h>

#define IMAGE_SCALE 256

#ifndef IMAGE_MACRO

#define CLAMP(i) ((i) < 0 ? 0 : ((i) > 255 ? 255 : (i)))
#define RGB(r, g, b) (0xff000000 | ((r) << 16) | ((g) << 8) | (b))
#define R(rgb) (((rgb) & 0x00ff0000) >> 16)
#define G(rgb) (((rgb) & 0x0000ff00) >> 8)
#define B(rgb) ((rgb) & 0x000000ff)
#define MAX(a, b) ((a) > (b) ? (a) : (b))
#define MIN(a, b) ((a) < (b) ? (a) : (b))
#define MAX3(a, b, c) ((a) > (b) ? MAX((a), (c)) : MAX((b), (c)))
#define MIN3(a, b, c) ((a) < (b) ? MIN((a), (c)) : MIN((b), (c)))

#endif

JNIEXPORT void JNICALL Java_com_example_autofixtest_AutoFix_imageAdjust
(JNIEnv * env, jclass clazz, jintArray pixels, jintArray results, jint width, jint height, jfloat gamma) {
	int * rgb =	(int *) (*env)->GetPrimitiveArrayCritical(env, pixels, NULL);
	int * processed = (int *) (*env)->GetPrimitiveArrayCritical(env, results, NULL);

	image_adjust(rgb, processed, width, height, gamma);

	(*env)->ReleasePrimitiveArrayCritical(env, pixels, rgb, 0);
	(*env)->ReleasePrimitiveArrayCritical(env, results, processed, 0);
}

void imhist(int image[], int n, int hist_r[], int hist_g[], int hist_b[]) {
	int i = 0;
	for(i = 0; i < IMAGE_SCALE; i++) {
		hist_r[i] = 0;
		hist_g[i] = 0;
		hist_b[i] = 0;
	}

	for (i = 0; i < n; i++) {
		++hist_r[R(image[i])];
		++hist_g[G(image[i])];
		++hist_b[B(image[i])];
	}
}

void cdf(int hist[], float result[], int n) {
	if (n != IMAGE_SCALE) {
		return;
	}

	int cum = 0, i = 0;
	int temp = 0;
	for (i = 0; i < n; ++i) {
		temp = hist[i];
		hist[i] += cum;
		cum += temp;
	}

	//calculate cdf
	if (cum <= 0) {
		return;
	}

	float sum = (float) cum;
	for (i = 0; i < n; ++i) {
		result[i] = ((float) hist[i]) / sum;
	}
}

void stretchlim(float cdf[], float lim[], float tol_low, float tol_high, int n) {
	if (n != IMAGE_SCALE) {
		return;
	}

	if (tol_low >= tol_high) {
		lim[0] = 0.0f;
		lim[1] = 1.0f;
		return;
	}

	int iLow = 0;
	int iHigh = n;
	int i = 0;
	for (i = 0; i < n; i++) {
		if (cdf[i] > tol_low) {
			break;
		}
		iLow = i;
	}

	for (i = 0; i < n; i++) {
		if (cdf[i] >= tol_high) {
			iHigh = i;
			break;
		}
	}

	lim[0] = ((float)(iLow - 1)) / (n - 1);
	lim[1] = ((float)(iHigh - 1)) / (n - 1);
}

void create_adjust_table(int lookup[], int n, float lowIn, float highIn, float lowOut,
		float highOut, float gamma) {
	if (n != IMAGE_SCALE) {
		return;
	}

	int i = 0;
	float range_in = highIn - lowIn;
	float range_out = highOut - lowOut;
	float out;

 	for(i = 0; i < n; i++) {
		out = ((float) i) / n;
		out = MAX(lowIn, MIN(highIn, out));
		out = pow(((out - lowIn) / range_in), gamma);
		out = out * range_out + lowOut;
		lookup[i] = CLAMP((int)(IMAGE_SCALE * out));
	}
}

void image_adjust(int rgb[], int result[], int width, int height, float gamma) {
	int img_size = width * height;

	int hist_r[IMAGE_SCALE], hist_g[IMAGE_SCALE], hist_b[IMAGE_SCALE];
	float cdf_r[IMAGE_SCALE], cdf_g[IMAGE_SCALE], cdf_b[IMAGE_SCALE];

	float lim_r[2], lim_g[2], lim_b[2];

	imhist(rgb, img_size, hist_r, hist_g, hist_b);

	cdf(hist_r, cdf_r, IMAGE_SCALE);
	cdf(hist_g, cdf_g, IMAGE_SCALE);
	cdf(hist_b, cdf_b, IMAGE_SCALE);

	float tol_low = 0.01f;
	float tol_high = 0.99f;

	stretchlim(cdf_r, lim_r, tol_low, tol_high, IMAGE_SCALE);
	stretchlim(cdf_g, lim_g, tol_low, tol_high, IMAGE_SCALE);
	stretchlim(cdf_b, lim_b, tol_low, tol_high, IMAGE_SCALE);

	int lookup_r[IMAGE_SCALE], lookup_g[IMAGE_SCALE], lookup_b[IMAGE_SCALE];
	create_adjust_table(lookup_r, IMAGE_SCALE, lim_r[0], lim_r[1], 0, 1,
			gamma);
	create_adjust_table(lookup_g, IMAGE_SCALE, lim_g[0], lim_g[1], 0, 1,
			gamma);
	create_adjust_table(lookup_b, IMAGE_SCALE, lim_b[0], lim_b[1], 0, 1,
			gamma);

	//adjust
	int i = 0;
	for (i = 0; i < img_size; i++) {
		result[i] = RGB(lookup_r[R(rgb[i])],
				lookup_g[G(rgb[i])],
				lookup_b[B(rgb[i])]);
	}
}

