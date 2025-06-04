#include <jni.h>
#include <stdlib.h>
#include "shared-clipboard-generic-client.h"
#include "jni_aliases.h"

#define MAYBE_UNUSED __attribute__((unused))

void shared_clipboard_setup_rustls_on_jvm(JNIEnv *env, jobject context);

JNIEXPORT void JNICALL j_shared_clipboard_setup(
        JNIEnv* env,
        MAYBE_UNUSED jclass klass,
        jobject context) {
    shared_clipboard_setup();
    shared_clipboard_setup_rustls_on_jvm(env, context);
}

JNIEXPORT jlong JNICALL j_shared_clipboard_config_new(
        JNIEnv* env,
        MAYBE_UNUSED jclass klass,
        jstring j_url,
        jstring j_token,
        jstring j_name) {
    const char* url = (*env)->GetStringUTFChars(env, j_url, 0);
    const char* token = (*env)->GetStringUTFChars(env, j_token, 0);
    const char* name = (*env)->GetStringUTFChars(env, j_name, 0);

    shared_clipboard_config_t *config = shared_clipboard_config_new(
            (const uint8_t*)url,
            (const uint8_t*)token,
            (const uint8_t*)name);

    (*env)->ReleaseStringUTFChars(env, j_url, url);
    (*env)->ReleaseStringUTFChars(env, j_token, token);
    (*env)->ReleaseStringUTFChars(env, j_name, name);

    return (jlong)(uintptr_t)config;
}

JNIEXPORT void JNICALL j_shared_clipboard_start_thread(MAYBE_UNUSED JNIEnv *env, MAYBE_UNUSED jobject klass, jlong j_config) {
    shared_clipboard_config_t *config = (shared_clipboard_config_t*)j_config;
    shared_clipboard_start_thread(config);
}

JNIEXPORT jboolean JNICALL j_shared_clipboard_stop_thread(MAYBE_UNUSED JNIEnv *env, MAYBE_UNUSED jobject klass) {
    return shared_clipboard_stop_thread();
}

JNIEXPORT void JNICALL j_shared_clipboard_send(JNIEnv *env, MAYBE_UNUSED jobject klass, jstring j_text) {
    const char* text = (*env)->GetStringUTFChars(env, j_text, 0);
    shared_clipboard_send((const uint8_t *)text);
    (*env)->ReleaseStringUTFChars(env, j_text, text);
}

JNIEXPORT jobject JNICALL j_shared_clipboard_poll(JNIEnv *env, MAYBE_UNUSED jobject klass) {
    shared_clipboard_output_t out = shared_clipboard_poll();

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

    jclass cls = (*env)->FindClass(env, "com/example/shared_clipboard_lib/PollOutput");
    jmethodID ctor = (*env)->GetMethodID(env, cls, "<init>", "(Ljava/lang/String;Ljava/lang/Boolean;)V");

    jobject result = (*env)->NewObject(env, cls, ctor, j_text, j_connectivity);
    return result;
}
