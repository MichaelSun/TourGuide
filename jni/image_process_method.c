#include "image_process_method.h"
#include <string.h>
#include <math.h>
#include <stdio.h>
#include <stdint.h>
#include <unistd.h>
#include <stdarg.h>
#include <android/log.h>
#include <libpng/png.h>
#include <libjpeg-turbo-1/jpeglib.h>
#include <time.h>
#include <image_utils.h>

#define RGB(a,b,c) ((a)+(b)*3+(c))

struct my_error_mgr {
	struct jpeg_error_mgr pub; /* "public" fields */

	jmp_buf setjmp_buffer; /* for return to caller */
};

typedef struct my_error_mgr * my_error_ptr;

typedef struct my_image_compress_struct * global_flag_ptr;

struct my_error_mgr jerr;

void my_error_exit(j_common_ptr cinfo) {
	my_error_ptr myerr = (my_error_ptr) cinfo->err;

	(*cinfo->err->output_message)(cinfo);

	longjmp(myerr->setjmp_buffer, 1);
}

char* itostr(char *str, int i) //将i转化位字符串存入str
{
	sprintf(str, "%d", i);
	return str;
}

void printjnilog(int i) {
//	char buffer[20];
//	char * str = &buffer[0];
//	str = itostr(str, i);
//	__android_log_print(ANDROID_LOG_INFO, "JNI", str);
}

int ready_for_read_jpg(global_flag_ptr myicss, char *infilename) {

	if ((myicss->in_file_name = fopen(infilename, "rb")) == NULL) {
		return FILE_CANNOT_OPEN;
	}
	myicss->cinfo.err = jpeg_std_error(&jerr.pub);
	jerr.pub.error_exit = my_error_exit;
	if (setjmp(jerr.setjmp_buffer)) {
		jpeg_destroy_decompress(&myicss->cinfo);
		fclose(myicss->in_file_name);
		myicss->in_file_name = NULL;
		return NOT_JPG_FILE;
	}
	jpeg_create_decompress(&myicss->cinfo);
	jpeg_stdio_src(&myicss->cinfo, myicss->in_file_name);
	jpeg_read_header(&myicss->cinfo, TRUE);
	jpeg_start_decompress(&myicss->cinfo);

	myicss->src_width = myicss->cinfo.output_width;
	myicss->src_height = myicss->cinfo.output_height;
	if (myicss->cinfo.output_components == 1) { //灰度图
		fclose(myicss->in_file_name);
		myicss->in_file_name = NULL;
		return NOT_JPG_FILE;
	}
	return READ_SUCCESS;
}

int ready_for_write_jpg(global_flag_ptr myicss, char* outfilename) {
	if ((myicss->out_file_name = fopen(outfilename, "wb")) == NULL) {
		fclose(myicss->in_file_name);
		myicss->in_file_name = NULL;
		return FILE_CANNOT_OPEN;
	}

	myicss->winfo.err = jpeg_std_error(&jerr.pub);
	jerr.pub.error_exit = my_error_exit;
	if (setjmp(jerr.setjmp_buffer)) {
		jpeg_destroy_compress(&myicss->winfo);
		fclose(myicss->in_file_name);
		fclose(myicss->out_file_name);
		myicss->in_file_name = NULL;
		myicss->out_file_name = NULL;
		return NOT_JPG_FILE;
	}
	jpeg_create_compress(&myicss->winfo);
	jpeg_stdio_dest(&myicss->winfo, myicss->out_file_name);
	myicss->winfo.image_width = myicss->dest_width;
	myicss->winfo.image_height = myicss->dest_height;
	myicss->winfo.in_color_space = JCS_RGB;
	myicss->winfo.input_components = 3;
	jpeg_set_defaults(&myicss->winfo);
	jpeg_start_compress(&myicss->winfo, TRUE);
	return WRITE_SUCCESS;
}
int ready_for_read_png(global_flag_ptr myicss, char* infile) {
	char header[8];    // 8 is the maximum size that can be checked

	/* open file and test for it being a png */
	myicss->in_file_name = fopen(infile, "rb");
	if (!myicss->in_file_name) {
		//		abort_("[read_png_file_singleline] File %s could not be opened for reading", file_name);
		return FILE_CANNOT_OPEN;
	}
	fread(header, 1, 8, myicss->in_file_name);

	if (png_sig_cmp(header, 0, 8)) {
		//		abort_("[read_png_file_singleline] File %s is not recognized as a PNG file", file_name);
		fclose(myicss->in_file_name);
		myicss->in_file_name = NULL;
		return NOT_PNG_FILE;
	}

	/* initialize stuff */
	myicss->png_ptr = png_create_read_struct(PNG_LIBPNG_VER_STRING, NULL, NULL,
			NULL);

	if (!myicss->png_ptr) {
		//		abort_("[read_png_file_singleline] png_create_read_struct failed");
		fclose(myicss->in_file_name);
		myicss->in_file_name = NULL;
		return OTHER_ERROR;
	}

	myicss->info_ptr = png_create_info_struct(myicss->png_ptr);
	if (!myicss->info_ptr) {
		//		abort_("[read_png_file_singleline] png_create_info_struct failed");
		fclose(myicss->in_file_name);
		myicss->in_file_name = NULL;
		return OTHER_ERROR;
	}
	if (setjmp(png_jmpbuf(myicss->png_ptr))) {
		//		abort_("[read_png_file_singleline] Error during init_io");
		png_destroy_read_struct(&myicss->png_ptr, &myicss->info_ptr,
				png_infopp_NULL);
		fclose(myicss->in_file_name);
		myicss->in_file_name = NULL;
		return OTHER_ERROR;
	}

	png_init_io(myicss->png_ptr, myicss->in_file_name);
	png_set_sig_bytes(myicss->png_ptr, 8);
	png_read_info(myicss->png_ptr, myicss->info_ptr);

	myicss->src_width = png_get_image_width(myicss->png_ptr, myicss->info_ptr);
	myicss->src_height = png_get_image_height(myicss->png_ptr,
			myicss->info_ptr);
	myicss->color_type = png_get_color_type(myicss->png_ptr, myicss->info_ptr);
	myicss->bit_depth = png_get_bit_depth(myicss->png_ptr, myicss->info_ptr);
	myicss->row_bytes = png_get_rowbytes(myicss->png_ptr, myicss->info_ptr);
	myicss->channel_num = png_get_channels(myicss->png_ptr, myicss->info_ptr);
	if (myicss->channel_num == 1) {    //gray png
		png_destroy_read_struct(&myicss->png_ptr, &myicss->info_ptr,
				png_infopp_NULL);
		fclose(myicss->in_file_name);
		myicss->in_file_name = NULL;
		return NOT_PNG_FILE;
	}
	myicss->number_passes = png_set_interlace_handling(myicss->png_ptr);
	if (myicss->number_passes != 1) {  //隔行扫描，暂不在此处理
		png_destroy_read_struct(&myicss->png_ptr, &myicss->info_ptr,
				png_infopp_NULL);
		fclose(myicss->in_file_name);
		myicss->in_file_name = NULL;
		return NOT_PNG_FILE;
	}

	if (myicss->bit_depth == 16)
		png_set_strip_16(myicss->png_ptr);
	if (myicss->color_type == PNG_COLOR_TYPE_PALETTE)
		png_set_expand(myicss->png_ptr);
	if (myicss->bit_depth < 8)
		png_set_expand(myicss->png_ptr);
	if (png_get_valid(myicss->png_ptr, myicss->info_ptr, PNG_INFO_tRNS))
		png_set_expand(myicss->png_ptr);
	if (myicss->color_type == PNG_COLOR_TYPE_GRAY
			|| myicss->color_type == PNG_COLOR_TYPE_GRAY_ALPHA)
		png_set_gray_to_rgb(myicss->png_ptr);

	png_read_update_info(myicss->png_ptr, myicss->info_ptr);

	/* read file */
	if (setjmp(png_jmpbuf(myicss->png_ptr))) {
		//		abort_("[read_png_file_singleline] Error during read_image");
		png_destroy_read_struct(&myicss->png_ptr, &myicss->info_ptr,
				png_infopp_NULL);
		fclose(myicss->in_file_name);
		myicss->in_file_name = NULL;
		return OTHER_ERROR;
	}
	return READ_SUCCESS;
}

BYTE* read_file_singleline(global_flag_ptr myicss, int image_style,
		int channel_num) {
	BYTE * temp_pointer = (BYTE *) malloc(
			sizeof(BYTE) * myicss->src_width * channel_num);
	if (image_style == 0) {
printjnilog(100000);
		(void) jpeg_read_scanlines(&myicss->cinfo, &temp_pointer, 1);
		printjnilog(1234567);
	} else {
		png_read_rows(myicss->png_ptr, &temp_pointer, png_bytepp_NULL, 1);
	}

	int i = 0;
	if (channel_num == 3) {
		memcpy(myicss->read_data_line, temp_pointer + myicss->origin_x * 3,
				3 * myicss->rect_width);
	} else if (channel_num == 4) {
		for (i = 0; i < myicss->rect_width; i++) {
			float alpha = temp_pointer[(i + myicss->origin_x) * channel_num + 3]
					/ 255;
			myicss->read_data_line[i * 3 + 0] = temp_pointer[(i
					+ myicss->origin_x) * channel_num + 0] * alpha;
			myicss->read_data_line[i * 3 + 1] = temp_pointer[(i
					+ myicss->origin_x) * channel_num + 1] * alpha;
			myicss->read_data_line[i * 3 + 2] = temp_pointer[(i
					+ myicss->origin_x) * channel_num + 2] * alpha;
		}
	}
	free(temp_pointer);
	return myicss->read_data_line;
}

int write_jpg_with_rotate(global_flag_ptr myicss, JSAMPROW *row_pointers_rotate) {
	BYTE* temp_pointer_rotate = (BYTE *) malloc(
			sizeof(BYTE) * myicss->dest_width * 3);

	//做增强处理
//	image_adjust((int *)row_pointers_rotate, (int *)row_pointers_rotate, myicss->dest_width, myicss->dest_height, 0.99f);

	int x = 0, y = 0;
	switch (myicss->src_rotation) {
	case ROTATE_DEGREE_0: {
		printjnilog(3333);
		for (y = 0; y < myicss->dest_height; y++) {
			memset(temp_pointer_rotate, 0, sizeof(temp_pointer_rotate));
			for (x = 0; x < myicss->dest_width; x++) {
				temp_pointer_rotate[x * 3 + 0] = row_pointers_rotate[y][x * 3
						+ 0];
				temp_pointer_rotate[x * 3 + 1] = row_pointers_rotate[y][x * 3
						+ 1];
				temp_pointer_rotate[x * 3 + 2] = row_pointers_rotate[y][x * 3
						+ 2];
			}
			jpeg_write_scanlines(&myicss->winfo, &temp_pointer_rotate, 1);
		}
	}
	break;
	case ROTATE_DEGREE_90: {
		printjnilog(4444);
		for (y = 0; y < myicss->dest_height; y++) {
			memset(temp_pointer_rotate, 0, sizeof(temp_pointer_rotate));
			for (x = 0; x < myicss->dest_width; x++) {
				temp_pointer_rotate[x * 3 + 0] =
						row_pointers_rotate[myicss->dest_height - x - 1][y * 3
								+ 0];
				temp_pointer_rotate[x * 3 + 1] =
						row_pointers_rotate[myicss->dest_height - x - 1][y * 3
								+ 1];
				temp_pointer_rotate[x * 3 + 2] =
						row_pointers_rotate[myicss->dest_height - x - 1][y * 3
								+ 2];
			}
			jpeg_write_scanlines(&myicss->winfo, &temp_pointer_rotate, 1);
		}

	}
		break;
	case ROTATE_DEGREE_180: {
		printjnilog(5555);
		for (y = 0; y < myicss->dest_height; y++) {
			for (x = 0; x < myicss->dest_width; x++) {
				temp_pointer_rotate[x * 3 + 0] =
						row_pointers_rotate[myicss->dest_height - y - 1][(myicss->dest_width
								- x - 1) * 3 + 0];
				temp_pointer_rotate[x * 3 + 1] =
						row_pointers_rotate[myicss->dest_height - y - 1][(myicss->dest_width
								- x - 1) * 3 + 1];
				temp_pointer_rotate[x * 3 + 2] =
						row_pointers_rotate[myicss->dest_height - y - 1][(myicss->dest_width
								- x - 1) * 3 + 2];
			}
			jpeg_write_scanlines(&myicss->winfo, &temp_pointer_rotate, 1);
		}
	}
		break;
	case ROTATE_DEGREE_270: {
		printjnilog(6666);
		for (y = 0; y < myicss->dest_height; y++) {
			memset(temp_pointer_rotate, 0, sizeof(temp_pointer_rotate));
			for (x = 0; x < myicss->dest_width; x++) {
				temp_pointer_rotate[x * 3 + 0] =
						row_pointers_rotate[x][(myicss->dest_width - y - 1) * 3
								+ 0];
				temp_pointer_rotate[x * 3 + 1] =
						row_pointers_rotate[x][(myicss->dest_width - y - 1) * 3
								+ 1];
				temp_pointer_rotate[x * 3 + 2] =
						row_pointers_rotate[x][(myicss->dest_width - y - 1) * 3
								+ 2];
			}
			jpeg_write_scanlines(&myicss->winfo, &temp_pointer_rotate, 1);
		}
	}
		break;
	}
	printjnilog(1234567);
	free(temp_pointer_rotate);
	temp_pointer_rotate = NULL;
	return 1;
}

int binary_interpolation_crop_and_scale(global_flag_ptr myicss,
		BYTE *line_data_one, BYTE *line_data_two, int widthPix, BYTE *outBuf,
		double scale, int row) {
	int count;
	int stand_count = 0, stand_row = 0;    //dest图中的坐标，count表示横向第几个点，row表示第几行
	int pixel_R, pixel_G, pixel_B;    //dest图中对应RGB像素值
	int left_count = 0, right_count = 1, top_count = 0, bottom_count = 1; //近邻四个点的默认横向位置
	int left_row = 0, right_row = 1, top_row = 0, bottom_row = 1; //近邻四个点的默认纵向位置
	int pixel_11, pixel_12, pixel_21, pixel_22;    //近邻四个点的像素值 int型
	double power_11, power_12, power_21, power_22;    //近邻四个点的权重
	struct RGBstyle {
		int pixel_11;
		int pixel_12;
		int pixel_21;
		int pixel_22;
		int pixel;

	};
	struct RGBstyle R, G, B;
	stand_row = row * scale;

	for (count = 0; count < widthPix; count++) {
		stand_count = count * scale;
		if (stand_count == myicss->rect_width - 1) {
			right_count = stand_count;
		} else {
			left_count = stand_count;
			right_count = stand_count + 1;
		}

		power_11 = (1 + stand_count * 1.0 - count * scale)
				* (1 + stand_row * 1.0 - row * scale);
		power_12 = (count * scale - stand_count * 1.0)
				* (1 + stand_row * 1.0 - row * scale);
		power_21 = (1 + stand_count * 1.0 - count * scale)
				* (row * scale - stand_row * 1.0);
		power_22 = (count * scale - stand_count * 1.0)
				* (row * scale - stand_row * 1.0);
		//		power_11 = 0.25;
		//		power_12 = 0.25;
		//		power_21 = 0.25;
		//		power_22 = 0.25;

		R.pixel_11 = (int) (*(RGB(line_data_one,left_count,0)));
		R.pixel_12 = (int) (*(RGB(line_data_one,right_count,0)));
		R.pixel_21 = (int) (*(RGB(line_data_two,left_count,0)));
		R.pixel_22 = (int) (*(RGB(line_data_two,right_count,0)));
		R.pixel = (int) (R.pixel_11 * power_11 + R.pixel_12 * power_12
				+ R.pixel_21 * power_21 + R.pixel_22 * power_22);

		G.pixel_11 = (int) (*(RGB(line_data_one,left_count,1)));
		G.pixel_12 = (int) (*(RGB(line_data_one,right_count,1)));
		G.pixel_21 = (int) (*(RGB(line_data_two,left_count,1)));
		G.pixel_22 = (int) (*(RGB(line_data_two,right_count,1)));
		G.pixel = (int) (G.pixel_11 * power_11 + G.pixel_12 * power_12
				+ G.pixel_21 * power_21 + G.pixel_22 * power_22);

		B.pixel_11 = (int) (*(RGB(line_data_one,left_count,2)));
		B.pixel_12 = (int) (*(RGB(line_data_one,right_count,2)));
		B.pixel_21 = (int) (*(RGB(line_data_two,left_count,2)));
		B.pixel_22 = (int) (*(RGB(line_data_two,right_count,2)));
		B.pixel = (int) (B.pixel_11 * power_11 + B.pixel_12 * power_12
				+ B.pixel_21 * power_21 + B.pixel_22 * power_22);
		if (R.pixel < 0)
			R.pixel = 0;
		if (R.pixel > 255)
			R.pixel = 255;
		if (G.pixel < 0)
			G.pixel = 0;
		if (G.pixel > 255)
			G.pixel = 255;
		if (B.pixel < 0)
			B.pixel = 0;
		if (B.pixel > 255)
			B.pixel = 255;
		*(RGB(outBuf,count,0)) = (BYTE) (R.pixel);

		*(RGB(outBuf,count,1)) = (BYTE) (G.pixel);

		*(RGB(outBuf,count,2)) = (BYTE) (B.pixel);

	}
	return pixel_11;
}

int crop_and_scale(global_flag_ptr myicss, int image_style) {

	BYTE* temp_pointer1;
	BYTE* temp_pointer2;
	int i = 0, row = 0, x = 0, y = 0, pass = 0, max_y = (myicss->rect_height
			+ myicss->origin_y);
	int destBytesPerLine = sizeof(BYTE) * myicss->dest_width * 3;
	printjnilog(destBytesPerLine);
	int need_interpolation = TRUE, need_rotate = FALSE;
	int img_style = image_style, channel_num;
	double scale_w = myicss->rect_width * 1.0 / (myicss->dest_width * 1.0);
	double scale_h = myicss->rect_height * 1.0 / (myicss->dest_height * 1.0);
	double scale = scale_w > scale_h ? scale_h : scale_w;
	int row_array[myicss->dest_height], k;
	for (k = 0; k < myicss->dest_height; k++) {
		//映射到原图上的y方向坐标
		row_array[k] = (int) (k * scale) + myicss->origin_y;
	}
	k = 0;
	JSAMPROW row_pointers[myicss->dest_height];
	for (row = 0; row < myicss->dest_height; row++)
		row_pointers[row] = NULL;
	row = 0;
	clock_t start_time, end_time;
	if ((myicss->dest_height == myicss->rect_height)
			&& (myicss->dest_width == myicss->rect_width))
		need_interpolation = FALSE;
	if (myicss->src_rotation != ROTATE_DEGREE_0)
		need_rotate = TRUE;
	if (img_style == IS_JPG_FILE) {
		channel_num = myicss->cinfo.output_components;
	} else if (img_style == IS_PNG_FILE) {
		channel_num = myicss->channel_num;
	}
	start_time = clock();
	printjnilog(99999);
	if (need_interpolation == FALSE && need_rotate == FALSE) {    //不需要插值也不需要旋转
		for (i = 0; i < max_y; i++) {
			temp_pointer2 = read_file_singleline(myicss, img_style,
					channel_num);
			if (i >= myicss->origin_y && i < max_y
					&& row < myicss->dest_height) {
//				jpeg_write_scanlines(&myicss->winfo, &temp_pointer2, 1);
				row_pointers[row] = (BYTE *) malloc(destBytesPerLine);
				memcpy(row_pointers[row], temp_pointer2, destBytesPerLine);
				row++;
			}
		}
		write_jpg_with_rotate(myicss, row_pointers);
	}
	printjnilog(123);

	if (need_interpolation == FALSE && need_rotate == TRUE) {    //不需要插值，需要旋转
		for (i = 0; i < max_y; i++) {
			temp_pointer2 = read_file_singleline(myicss, img_style,
					channel_num);
			if (i >= myicss->origin_y && i < max_y
					&& row < myicss->dest_height) {
				row_pointers[row] = (BYTE *) malloc(destBytesPerLine);
				memcpy(row_pointers[row], temp_pointer2, destBytesPerLine);
				row++;
			}
		}
		write_jpg_with_rotate(myicss, row_pointers);
	}

	if (need_interpolation == TRUE && need_rotate == FALSE) {    //需要插值，不用旋转
		for (i = 0; i < max_y; i++) {
			temp_pointer1 = temp_pointer2;
			temp_pointer2 = read_file_singleline(myicss, img_style,
					channel_num);
			if (i == myicss->origin_y
					|| row_array[k] == max_y - 1 && row < myicss->dest_height) {
				binary_interpolation_crop_and_scale(myicss, temp_pointer2,
						temp_pointer2, myicss->dest_width,
						myicss->write_data_line, scale, k);
//				jpeg_write_scanlines(&myicss->winfo, &myicss->write_data_line,
//						1);
				row_pointers[row] = (BYTE *) malloc(destBytesPerLine);
				memcpy(row_pointers[row], myicss->write_data_line,
						destBytesPerLine);
				row++;
			} else if (i == row_array[k] + 1 && k < myicss->dest_height
					&& row < myicss->dest_height) {
				binary_interpolation_crop_and_scale(myicss, temp_pointer1,
						temp_pointer2, myicss->dest_width,
						myicss->write_data_line, scale, k);
//				jpeg_write_scanlines(&myicss->winfo, &myicss->write_data_line,
//						1);
				row_pointers[row] = (BYTE *) malloc(destBytesPerLine);
				memcpy(row_pointers[row], myicss->write_data_line,
						destBytesPerLine);
				row++;
				k++;
			}

		}
		write_jpg_with_rotate(myicss, row_pointers);
	}
	if (need_interpolation == TRUE && need_rotate == TRUE) {    //需要插值，需要旋转
		for (i = 0; i < max_y; i++) {
			temp_pointer1 = temp_pointer2;
			temp_pointer2 = read_file_singleline(myicss, img_style,
					channel_num);
			if (i == myicss->origin_y
					|| row_array[k] == max_y - 1 && row < myicss->dest_height) {
				binary_interpolation_crop_and_scale(myicss, temp_pointer2,
						temp_pointer2, myicss->dest_width,
						myicss->write_data_line, scale, k);
				row_pointers[row] = (BYTE *) malloc(destBytesPerLine);
				memcpy(row_pointers[row], myicss->write_data_line,
						destBytesPerLine);
				row++;
			} else if (i == row_array[k] + 1 && k < myicss->dest_height
					&& row < myicss->dest_height) {
				binary_interpolation_crop_and_scale(myicss, temp_pointer1,
						temp_pointer2, myicss->dest_width,
						myicss->write_data_line, scale, k);
				row_pointers[row] = (BYTE *) malloc(destBytesPerLine);
				memcpy(row_pointers[row], myicss->write_data_line,
						destBytesPerLine);
				row++;
				k++;
			}
		}
		write_jpg_with_rotate(myicss, row_pointers);
	}
	end_time = clock();
	int time = (end_time - start_time) * 1000 / CLOCKS_PER_SEC;
	printjnilog(time);
	for (row = 0; row < myicss->dest_height; row++) {
		free(row_pointers[row]);
	}

	jpeg_finish_compress(&myicss->winfo);
	jpeg_destroy_compress(&myicss->winfo);
	if (img_style == IS_JPG_FILE) {
		myicss->cinfo.output_scanline = myicss->src_height;
		jpeg_finish_decompress(&myicss->cinfo);
		jpeg_destroy_decompress(&myicss->cinfo);
	} else if (img_style == IS_PNG_FILE) {
		png_destroy_read_struct(&myicss->png_ptr, &myicss->info_ptr,
				png_infopp_NULL);
	}

	return SUCCESS_PROCESS_RESULT;
}
