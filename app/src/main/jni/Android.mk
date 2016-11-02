LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)



# OpenCV
OPENCV_INSTALL_MODULES:=on
OPENCV_CAMERA_MODULES:=on

include D:\AndroidDev\OpenCV-3.0.0-android-sdk-1\OpenCV-android-sdk\sdk\native\jni\OpenCV.mk


LOCAL_MODULE    := mixed_sample
LOCAL_SRC_FILES := jni_part.cpp jni_blur.cpp jni_otsu.cpp jni_canny.cpp ImageProcessing.cpp jni_hough.cpp Decoding.cpp
LOCAL_LDLIBS +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)
