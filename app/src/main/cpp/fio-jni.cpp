#include <jni.h>
#include <string>
#include "fio-jni.h"

#ifdef __cplusplus
extern "C" {
#endif

#ifdef __cplusplus
}
#endif

static const JNINativeMethod FIOMethods[] = {
};

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    jclass c = env->FindClass(CLASS_NAME_RAPTOR_MARK);
    if (c == nullptr) return JNI_ERR;

    jint rc = env->RegisterNatives(c, FIOMethods, sizeof(FIOMethods) / sizeof(JNINativeMethod));
    if (rc != JNI_OK) return rc;

    return JNI_VERSION_1_6;
}

JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return;
    }

    jclass c = env->FindClass(CLASS_NAME_RAPTOR_MARK);
    if (c == nullptr) return;

    env->UnregisterNatives(c);
}
