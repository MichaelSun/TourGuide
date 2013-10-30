LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_SRC_FILES:=libpng/png.c libpng/pngerror.c libpng/pnggccrd.c libpng/pngget.c libpng/pngmem.c \
libpng/pngpread.c libpng/pngread.c libpng/pngrio.c libpng/pngrtran.c libpng/pngrutil.c libpng/pngset.c \
libpng/pngtrans.c libpng/pngvcrd.c libpng/pngwio.c libpng/pngwrite.c libpng/pngwtran.c libpng/pngwutil.c
LOCAL_LDLIBS := -lz 
LOCAL_MODULE := png
include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_SRC_FILES:= jpng.c image_process_method.c
LOCAL_MODULE := jpng

LOCAL_STATIC_LIBRARIES := libjpeg
LOCAL_STATIC_LIBRARIES += libpng

LOCAL_C_INCLUDES += $(LOCAL_PATH)/libjpeg-turbo-1
LOCAL_C_INCLUDES += $(LOCAL_PATH)/libpng

APP_ABI := armeabi armeabi-v7a 

LOCAL_LDLIBS :=  -L$(SYSROOT)/usr/lib -llog
LOCAL_LDLIBS += -lz

include $(BUILD_SHARED_LIBRARY)

include $(LOCAL_PATH)/libjpeg-turbo-1/Android.mk


#arm-v7a
#ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
#	LOCAL_CFLAGS := -DHAVE_NEON=1
#endif # TARGET_ARCH_ABI == armeabi-v7a


