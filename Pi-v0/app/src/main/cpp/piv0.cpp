#include "./cpp_framework/src/active_beacon.h"
#include "./cpp_framework/src/beacon_callback.h"
#include "./cpp_framework/src/dead_reckoning.h"
#include "./cpp_framework/src/trilateration.h"
#include <jni.h>


extern "C"
JNIEXPORT void JNICALL
Java_com_project_1ips_piv0_jni_1models_BeaconCallback_jni_1resolve(JNIEnv *env, jclass clazz,
                                                              jstring uuid, jint major, jint minor,
                                                              jint rssi) {
    const char *str = env->GetStringUTFChars(uuid , nullptr);
    string id = str;
    pool.post([id,major,minor,rssi]{
        resolve(id,major,minor,rssi);
    });
    env->ReleaseStringUTFChars(uuid, str);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_project_1ips_piv0_jni_1models_ConfiguredBeacon_addToConfiguredBeacons(JNIEnv *env, jclass clazz,
                                                                          jlong beacon_ptr) {
    auto* configured_beacon_ptr = (ConfiguredBeacon*)beacon_ptr;
    return addToConfiguredBeacons(*configured_beacon_ptr);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_project_1ips_piv0_jni_1models_ConfiguredBeacon_00024Builder_jni_1allocate(JNIEnv *env,
                                                                              jclass clazz) {
    auto* builder_ptr = new ConfiguredBeacon::Builder();
    return (long)builder_ptr;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_project_1ips_piv0_jni_1models_ConfiguredBeacon_00024Builder_jni_1free(JNIEnv *env, jclass clazz,
                                                                          jlong pointer) {
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    delete builder_ptr;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_project_1ips_piv0_jni_1models_ConfiguredBeacon_00024Builder_jni_1create(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jlong pointer) {
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    return (long)(builder_ptr->create());
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_project_1ips_piv0_jni_1models_ConfiguredBeacon_00024Builder_jni_1setId(JNIEnv *env,
                                                                           jobject thiz,
                                                                           jlong pointer,
                                                                           jstring uuid, jint major,
                                                                           jint minor) {
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    const char* str = env->GetStringUTFChars(uuid,nullptr);
    string id = str;
    env->ReleaseStringUTFChars(uuid,str);
    return (long)(builder_ptr->setId(id,major,minor));
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_project_1ips_piv0_jni_1models_ConfiguredBeacon_00024Builder_jni_1setPos(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jlong pointer, jint x,
                                                                            jint y, jint z) {
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    return (long)(builder_ptr->setPos(x,y,z));
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_project_1ips_piv0_jni_1models_ConfiguredBeacon_00024Builder_jni_1setRssiD0(JNIEnv *env,
                                                                               jobject thiz,
                                                                               jlong pointer,
                                                                               jint rssi) {
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    return (long)(builder_ptr->setRssiD0(rssi));
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_project_1ips_piv0_jni_1models_ConfiguredBeacon_00024Builder_jni_1setD0(JNIEnv *env,
                                                                           jobject thiz,
                                                                           jlong pointer, jint d0) {
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    return (long)(builder_ptr->setD0(d0));
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_project_1ips_piv0_jni_1models_ConfiguredBeacon_00024Builder_jni_1setBeaconCoeff(JNIEnv *env,
                                                                                    jobject thiz,
                                                                                    jlong pointer,
                                                                                    jfloat coeff) {
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    return (long)(builder_ptr->setBeaconCoeff(coeff));
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_project_1ips_piv0_jni_1models_ConfiguredBeacon_00024Builder_jni_1setXSigma(JNIEnv *env,
                                                                               jobject thiz,
                                                                               jlong pointer,
                                                                               jfloat x_sig) {
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    return (long)(builder_ptr->setXSigma(x_sig));
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_project_1ips_piv0_jni_1models_ConfiguredBeacon_00024Builder_jni_1registerFilter(JNIEnv *env,
                                                                                    jobject thiz,
                                                                                    jlong pointer,
                                                                                    jlong filter_ptr) {
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    auto* filter_pointer = (Filter*)filter_ptr;
    return (long)(builder_ptr->registerFilter(filter_pointer));
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_project_1ips_piv0_jni_1models_ConfiguredBeacon_00024Builder_jni_1removeFilter(JNIEnv *env,
                                                                                  jobject thiz,
                                                                                  jlong pointer,
                                                                                  jlong filter_ptr) {
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    auto* filter_pointer = (Filter*)filter_ptr;
    return (long)(builder_ptr->removeFilter(filter_pointer));
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_project_1ips_piv0_jni_1models_ConfiguredBeacon_00024Builder_jni_1build(JNIEnv *env,
                                                                           jobject thiz,
                                                                           jlong pointer) {
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    //build() would return a reference to the dynamically allocated ConfigureBeacon object
    //and this function would return the memory address of that object.
    return (long)(&builder_ptr->build());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_project_1ips_piv0_jni_1models_DRListener_onStepDetected(JNIEnv *env, jobject thiz,
                                                            jfloatArray rot_array) {
    int len = env-> GetArrayLength(rot_array);
    float carray[len];
    env->GetFloatArrayRegion(rot_array,0,len,carray);
    onStepDetected(carray);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_project_1ips_piv0_jni_1models_MeanFilter_jni_1allocate(JNIEnv *env, jclass clazz,
                                                           jint window_size) {
    auto* mean_filter_ptr = new MeanFilter(window_size);
    return (long)mean_filter_ptr;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_project_1ips_piv0_jni_1models_MeanFilter_jni_1free(JNIEnv *env, jobject thiz, jlong pointer) {
    auto* mean_filter_ptr = (MeanFilter*)pointer;
    delete mean_filter_ptr;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_project_1ips_piv0_jni_1models_PositioningAlgorithm_jni_1begin(JNIEnv *env, jclass clazz,
                                                                  jint algo, jint start_x,
                                                                  jint start_y) {
    ALGO_FLAG = algo;
    LOGI("Algo Flag set to %d", ALGO_FLAG);
    unique_lock<shared_mutex> lock(positionMutex);
    user_position = Position(start_x,start_y,0);
    lock.unlock();
    if(ALGO_FLAG == 0||ALGO_FLAG==1){     //Fusion or trilateration
        readerThread.post([]{
            LOGI("Trying to get JniEnv for current thread");
            int attached = jvm->AttachCurrentThread(&jniENV,NULL);
            if(attached==JNI_OK)
            {
                LOGI("Success attaching JavaVM to current thread");
            }
            else{
                LOGI("Failure attaching JavaVM to current thread");
            }
        });
        startTrilateration();
    }
    //No need to do anything in DR. It automatically gets triggered from Step Detection.
}

extern "C"
JNIEXPORT void JNICALL
Java_com_project_1ips_piv0_jni_1models_PositioningAlgorithm_jni_1stop(JNIEnv *env, jclass clazz) {
    if (ALGO_FLAG == 0 || ALGO_FLAG == 1) {  //Fusion or Trilateration
        readerThread.post([]{
            LOGI("Trying to detach JniEnv for current thread");
            int detached = jvm->DetachCurrentThread();
            if(detached==JNI_OK)
            {
                LOGI("Success detaching JavaVM to current thread");
            }
            else{
                LOGI("Failure detaching JavaVM to current thread");
            }
        });
        stopTrilateration();
    }
}
