#include <jni.h>
#include <string>
#include "cJSON.h"
#include "common.h"
#include "helper.h"
#include "fio-jni.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL native_ListEngines(JNIEnv *env, jobject instance)
{
    char *engineList[MAX_ENGINE_NUM];
    int index, engineNum;

    engineNum = fio_list_ioengines(engineList);

    cJSON *root = cJSON_CreateObject();
    cJSON *engines = cJSON_AddArrayToObject(root, "engines");

    if (engineNum > 0) {
        for (index = 0; index < engineNum; index++)
        {
            cJSON *engine = cJSON_CreateObject();
            char *engineName = engineList[index];
            bool engineAvailable = checkEngineAvailability(engineName);
            cJSON_AddStringToObject(engine, "name", engineName);
            cJSON_AddBoolToObject(engine, "available", engineAvailable);
            cJSON_AddItemToArray(engines, engine);

            free(engineList[index]);
        }
    } else {
        LOGE("Could not find IO engine.");
    }

    return env->NewStringUTF(cJSON_PrintUnformatted(root));
}

#ifdef __cplusplus
}
#endif

static const JNINativeMethod FIOMethods[] = {
        {"native_ListEngines",  "()Ljava/lang/String;",  (void *)native_ListEngines}
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
