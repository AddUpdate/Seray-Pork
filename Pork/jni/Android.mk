
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := ImageProc
LOCAL_SRC_FILES := ImageProc.c
LOCAL_LDLIBS    := -llog -ljnigraphics
LOCAL_CERTIFICATE := platform

include $(BUILD_SHARED_LIBRARY)
