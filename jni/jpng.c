#include <string.h>
#include <jni.h>
#include <math.h>
#include <stdint.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdarg.h>
#include "com_find_guide_utils_CropAndScale.h"
#include "image_process_method.h"

JNIEXPORT jlong JNICALL Java_com_find_guide_utils_CropAndScale_createGlobalFlags(
		JNIEnv * env, jobject obj, jobject context) {

	global_flag_ptr gfp = (global_flag_ptr) malloc(1024);
	if (NULL == gfp) {
		return 0L;
	}
	return (jlong) gfp;
}

JNIEXPORT void JNICALL Java_com_find_guide_utils_CropAndScale_close
(JNIEnv * env, jobject obj, jlong gfp) {
	if(NULL != (global_flag_ptr)gfp) {
		printjnilog(gfp);
		free((global_flag_ptr)gfp);
	}
}

JNIEXPORT jint JNICALL Java_com_find_guide_utils_CropAndScale_nativeCropJpg(
		JNIEnv * env, jclass thiz, jlong gfp, jstring infilename,
		jstring outfilename, jint x, jint y, jint rectW, jint rectH, jint destW,
		jint destH, jint rotation) {

	char* infile = (char *) (*env)->GetStringUTFChars(env, infilename, 0);
	char* outfile = (char *) (*env)->GetStringUTFChars(env, outfilename, 0);
	global_flag_ptr myics = (global_flag_ptr) gfp;

	myics->origin_x = x;
	myics->origin_y = y;
	myics->dest_width = destW <= destH ? destW : destH;
	myics->dest_height = destW <= destH ? destW : destH;
	myics->rect_width = rectW <= rectH ? rectW : rectH;
	myics->rect_height = rectW <= rectH ? rectW : rectH;
	myics->src_rotation = rotation;

	int jniResult = FAILED_PROCESS_RESULT;

	myics->read_data_line = (BYTE *) malloc(
			sizeof(BYTE) * myics->rect_width * 3);
	myics->write_data_line = (BYTE *) malloc(
			sizeof(BYTE) * myics->dest_width * 3);
	if (ready_for_read_jpg(myics, infile) == NOT_JPG_FILE) {
		if (ready_for_read_png(myics, infile) == READ_SUCCESS) { //read png file success
			if (ready_for_write_jpg(myics, outfile) == WRITE_SUCCESS) {
				//ready for write
				jniResult = crop_and_scale(myics, IS_PNG_FILE);
				if (myics->in_file_name != NULL) {
					fclose(myics->in_file_name);
					myics->in_file_name = NULL;
				}
				if (myics->out_file_name != NULL) {
					fclose(myics->out_file_name);
					myics->out_file_name = NULL;
				}
			}
		}
	} else if (ready_for_read_jpg(myics, infile) == READ_SUCCESS) { //read jpg file success
		if (ready_for_write_jpg(myics, outfile) == WRITE_SUCCESS) {
			//ready for write
			jniResult = crop_and_scale(myics, IS_JPG_FILE);
			if (myics->in_file_name != NULL) {
				fclose(myics->in_file_name);
				myics->in_file_name = NULL;
			}
			if (myics->out_file_name != NULL) {
				fclose(myics->out_file_name);
				myics->out_file_name = NULL;
			}
		}
	}

	if (myics->read_data_line != NULL) {
		free(myics->read_data_line);
		myics->read_data_line = NULL;
	}
	if (myics->write_data_line != NULL) {
		free(myics->write_data_line);
		myics->write_data_line = NULL;
	}

	(*env)->ReleaseStringUTFChars(env, infilename, infile);
	(*env)->ReleaseStringUTFChars(env, outfilename, outfile);

	return jniResult;
}

