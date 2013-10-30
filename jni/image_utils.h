#ifndef __IMAGE_UTIL_H__
#define __IMAGE_UTIL_H__


/**
*/
void imhist(int image[], int n, int hist_r[], int hist_g[], int hist_b[]);

void cdf(int hist[], float cdf[], int n);

void stretchlim(float cdf[], float lim[], float tol_low, float tol_high, int n);

void image_adjust(int rgb[], int result[], int width, int height, float gamma);

void create_adjust_table(int lookup[], int n, float lowIn, float highIn, float lowOut,
		float highOut, float gamma);
#endif
