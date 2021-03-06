#ifndef _Included_com_sound_dubbler_utils_cropimage_CropAndScale_method
#define _Included_com_sound_dubbler_utils_cropimage_CropAndScale_method
/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_example_seeyou_generateJPG */
#include <string.h>
#include <math.h>
#include <stdio.h>
#include <stdint.h>
#include <unistd.h>
#include <stdarg.h>
#include <android/log.h>
#include <libpng/png.h>
#include <libjpeg-turbo-1/jpeglib.h>


#undef READ_SUCCESS
#define READ_SUCCESS 10
#undef WRITE_SUCCESS
#define WRITE_SUCCESS 20
#undef FILE_CANNOT_OPEN
#define FILE_CANNOT_OPEN 11
#undef NOT_JPG_FILE
#define NOT_JPG_FILE 12
#undef NOT_PNG_FILE
#define NOT_PNG_FILE 13
#undef FILE_CANNOT_WRITE
#define FILE_CANNOT_WRITE 21
#undef OTHER_ERROR
#define OTHER_ERROR 14
#undef FAILED_PROCESS_RESULT
#define FAILED_PROCESS_RESULT 0
#undef SUCCESS_PROCESS_RESULT
#define SUCCESS_PROCESS_RESULT 1
#undef ROTATE_DEGREE_0
#define ROTATE_DEGREE_0 0
#undef ROTATE_DEGREE_90
#define ROTATE_DEGREE_90 90
#undef ROTATE_DEGREE_180
#define ROTATE_DEGREE_180 180
#undef ROTATE_DEGREE_270
#define ROTATE_DEGREE_270 270
#undef IS_PNG_FILE
#define IS_PNG_FILE 1
#undef IS_JPG_FILE
#define IS_JPG_FILE 0


#ifdef __cplusplus
extern "C" {
#endif

typedef uint8_t BYTE;


struct my_image_compress_struct{
	FILE * in_file_name;
	FILE * out_file_name;

	int origin_x;
	int origin_y;
	int dest_width;
	int dest_height;
	int rect_width;
	int rect_height;
	int src_width;
	int src_height;
	int src_rotation;
	struct jpeg_decompress_struct cinfo;
	struct jpeg_compress_struct winfo;
	BYTE* write_data_line;
	BYTE* read_data_line;
	int row_bytes;
	int number_passes;
	png_structp png_ptr;
	png_infop info_ptr;
	png_byte color_type;
	png_byte bit_depth;
	png_byte channel_num;

};

typedef struct my_image_compress_struct * global_flag_ptr;

int ready_for_read_jpg(global_flag_ptr myicss, char * infilename);


int ready_for_write_jpg(global_flag_ptr myicss, char* outfilename);


int ready_for_read_png(global_flag_ptr myicss, char* infilename);


BYTE* read_jpg_file_singleline(global_flag_ptr myicss,int image_style,int channel_num) ;


int write_jpg_with_rotate(global_flag_ptr myicss,JSAMPROW * row_pointers);


int binary_interpolation_crop_and_scale(global_flag_ptr myicss, BYTE *line_data_one, BYTE *line_data_two,
		int widthPix, BYTE *outBuf, double scale, int row) ;

int crop_and_scale(global_flag_ptr myicss,int image_style);

#ifdef __cplusplus
}
#endif
#endif



