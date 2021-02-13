#ifndef RAPTORMARK_COMMON_H
#define RAPTORMARK_COMMON_H

#include <android/log.h>

#define native_init    __attribute__((constructor))
#define native_exit    __attribute__((destructor))

#define DEBUG_TAG "RaptorMark:Native"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, __VA_ARGS__))
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, DEBUG_TAG, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, DEBUG_TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, DEBUG_TAG, __VA_ARGS__))

#endif //RAPTORMARK_COMMON_H
