#include <jni.h>
#include <stdlib.h>
#include "mpclipboard-generic-client.h"
#include "jni_aliases.h"

#define MAYBE_UNUSED __attribute__((unused))

void mpclipboard_setup_rustls_on_jvm(JNIEnv *env, jobject context);

JNIEXPORT void JNICALL j_mpclipboardSetup(
        JNIEnv* env,
        MAYBE_UNUSED jclass klass,
        jobject context) {
    mpclipboard_init();
    mpclipboard_setup_rustls_on_jvm(env, context);
}

JNIEXPORT jlong JNICALL j_mpclipboardConfigNew(
        JNIEnv* env,
        MAYBE_UNUSED jclass klass,
        jstring j_uri,
        jstring j_token,
        jstring j_name) {
    const char* uri = (*env)->GetStringUTFChars(env, j_uri, 0);
    const char* token = (*env)->GetStringUTFChars(env, j_token, 0);
    const char* name = (*env)->GetStringUTFChars(env, j_name, 0);

    mpclipboard_config_t *config = mpclipboard_config_new(uri, token, name);

    (*env)->ReleaseStringUTFChars(env, j_uri, uri);
    (*env)->ReleaseStringUTFChars(env, j_token, token);
    (*env)->ReleaseStringUTFChars(env, j_name, name);

    return (jlong)(uintptr_t)config;
}

JNIEXPORT jlong JNICALL j_mpclipboardStartThread(MAYBE_UNUSED JNIEnv *env, MAYBE_UNUSED jobject klass, jlong j_config) {
    mpclipboard_config_t *config = (mpclipboard_config_t*)j_config;
    mpclipboard_handle_t *handle = mpclipboard_thread_start(config);
    return (jlong)(uintptr_t)handle;
}

JNIEXPORT jboolean JNICALL j_mpclipboardStopThread(MAYBE_UNUSED JNIEnv *env, MAYBE_UNUSED jobject klass, jlong j_handle) {
    mpclipboard_handle_t *handle = (void*)j_handle;
    return mpclipboard_handle_stop(handle);
}

JNIEXPORT jboolean JNICALL j_mpclipboardSend(JNIEnv *env, MAYBE_UNUSED jobject klass, jlong j_handle, jstring j_text) {
    mpclipboard_handle_t *handle = (void*)j_handle;
    const char* text = (*env)->GetStringUTFChars(env, j_text, 0);
    bool is_new = mpclipboard_handle_send(handle, text);
    (*env)->ReleaseStringUTFChars(env, j_text, text);
    return is_new;
}

JNIEXPORT jobject JNICALL j_mpclipboardPoll(JNIEnv *env, MAYBE_UNUSED jobject klass, jlong j_handle) {
    mpclipboard_handle_t *handle = (void*)j_handle;
    mpclipboard_output_t out = mpclipboard_handle_poll(handle);

    jstring j_text = NULL;
    if (out.text != NULL) {
        j_text = (*env)->NewStringUTF(env, (const char *)out.text);
        free(out.text);
    }

    jobject j_connectivity = NULL;
    if (out.connectivity != NULL) {
        jclass booleanClass = (*env)->FindClass(env, "java/lang/Boolean");
        jmethodID booleanCtor = (*env)->GetMethodID(env, booleanClass, "<init>", "(Z)V");
        j_connectivity = (*env)->NewObject(env, booleanClass, booleanCtor, *out.connectivity);
        free(out.connectivity);
    }

    jclass cls = (*env)->FindClass(env, "org/mpclipboard/mpclipboard/PollOutput");
    jmethodID ctor = (*env)->GetMethodID(env, cls, "<init>", "(Ljava/lang/String;Ljava/lang/Boolean;)V");

    jobject result = (*env)->NewObject(env, cls, ctor, j_text, j_connectivity);
    return result;
}

JNIEXPORT jlong JNICALL j_mpclipboardTakeFd(JNIEnv *env, MAYBE_UNUSED jobject klass, jlong j_handle) {
    mpclipboard_handle_t *handle = (void*)j_handle;
    int fd = mpclipboard_handle_take_fd(handle);
    return fd;
}