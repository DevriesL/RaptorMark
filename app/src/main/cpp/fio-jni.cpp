#include <jni.h>
#include <string>
#include "cJSON.h"
#include "common.h"
#include "helper.h"
#include "fio-jni.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL native_FIOTest(JNIEnv *env, jobject instance, jstring jsonCommand)
{
    const char *jsonStr = env->GetStringUTFChars(jsonCommand, NULL);
    int ret, argc;
    char **argv;

    json2Options(jsonStr, &argc, &argv);
    ret = fio(argc, argv, NULL);
    freeOptions(&argc, &argv);

    return ret;
}

JNIEXPORT jint JNICALL native_LatencyTest(JNIEnv *env, jobject instance, jstring jsonCommand)
{
    const char *jsonStr = env->GetStringUTFChars(jsonCommand, NULL);
    int ret, argc;
    char **argv;

    json2Options(jsonStr, &argc, &argv);
    ret = read_to_pipe_async(argc, argv);
    freeOptions(&argc, &argv);

    return ret;
}

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
        {"native_FIOTest",      "(Ljava/lang/String;)I", (void *)native_FIOTest},
        {"native_LatencyTest",  "(Ljava/lang/String;)I", (void *)native_LatencyTest},
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
