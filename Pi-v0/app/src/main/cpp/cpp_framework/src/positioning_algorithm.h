//
// Created by aurok on 23-01-2022.
//

#ifndef POSITIONING_ALGORITHM_H
#define POSITIONING_ALGORITHM_H

#include "../libs/thread_pool.h"
#include "position.h"
#include <shared_mutex>
#include <android/log.h>
#include <jni.h>

#ifdef LOGI
#undef LOGI
#endif
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "native-lib::", __VA_ARGS__))

ThreadPool pool{5};

//Some Defaults:
const float DEFAULT_N = 1.63;
const int DEFAULT_D0 = 100;
const int DEFAULT_RSSI_D0 = -81;
const Position DEFAULT_POS{0,1,0};		//invalid position
const float DEFAULT_X_SIGMA = 0;
const float DEFAULT_BEACON_COEFF = 1;
const int MAX_INACTIVE_FLAG = 3;
const int DEFAULT_UPDATE_CYCLE_FREQUENCY = 350;	//in ms
const int MIN_ACCEPTABLE_RSSI = -97;
int ALGO_FLAG = 0;      //ALGO_FLAG(s) = 0(FUSION), 1(Only Trilateration), 2(Only Dead Reckoning)

static JNIEnv* jniENV;
static JavaVM* jvm;
jclass callback_class;
jmethodID callback_method;
const char* mName;
const char* sig;
jobject pa;

const unsigned int MAX_UPDATE_CYCLES_IN_QUEUE = 3;

shared_mutex trilaterationMutex;
bool isTrilaterationActive = false;

Position user_position{0,0,0};
shared_mutex positionMutex;

extern "C"
JNIEXPORT void JNICALL
Java_com_project_1ips_piv0_jni_1models_PositioningAlgorithm_jni_1initialize(JNIEnv *env, jclass clazz,
                                                                       jstring class_name,
                                                                       jstring method_name,
                                                                       jstring signature, jobject pa_) {
    jniENV = env;
    const char* cName = env->GetStringUTFChars(class_name,nullptr);
    LOGI("%s",cName);
    mName = env->GetStringUTFChars(method_name, nullptr);
    LOGI("%s",mName);
    sig = env->GetStringUTFChars(signature, nullptr);
    LOGI("%s",sig);
    pa = reinterpret_cast<jobject>(env->NewGlobalRef(pa_));
//    jclass localClass = env->GetObjectClass(pa);
//    callback_class = reinterpret_cast<jclass>(env->NewGlobalRef(localClass));
    env->ReleaseStringUTFChars(class_name,cName);
    env->ReleaseStringUTFChars(method_name,mName);
    env->ReleaseStringUTFChars(signature,sig);

    LOGI("Trying to Get JavaVM");
    int gotVM = jniENV->GetJavaVM(&jvm);
    if(gotVM == 0){
        LOGI("Got Java VM");
    }
    else{
        LOGI("Java VM Error : %d",gotVM);
    }
}

void inline onPositionUpdated(int flag, Position *pos){
    if (flag == 1) {     //Trilateration update
        if (ALGO_FLAG == 1) {
            //ALGO_FLAG = 1
            //callback_class = jniENV->FindClass("com/location/app/jni_models/PositioningAlgorithm");
            callback_class = jniENV->GetObjectClass(pa);
            LOGI("Inside native c++ method onPositionUpdate() pt2");
            callback_method = jniENV->GetMethodID(callback_class, "onPositionUpdate", "(II)V");
            if (callback_method == NULL) {
                LOGI("No method found");
            }
            LOGI("Inside native c++ method onPositionUpdate() pt Pos: x = %d y = %d", pos->getX(),
                 pos->getY());
            jniENV->CallVoidMethod(pa, callback_method, pos->getX(), pos->getY());
        }
    }

}

/**
 * Flag refers to which algorithm is updating its position estimate.
 * @param flag
 * @param pos
 */
void inline onPositionUpdated(int flag, Position pos) {

    static int tr_pos[] = {0, 0};
    static shared_mutex trilaterationPositionMutex;

    static int n = 0;
    static  Position position_estimate{0, 0, 0};

    static double process_covariance = 0.1;
    static double process_covariance_estimate = 0;
    static const double process_noise = 0.005;
    static const double measurement_noise = 1.25;

    if (flag == 1) {     //Trilateration update
        if (ALGO_FLAG == 0) {
            n++;
            tr_pos[0] += round(pos.getX() / (n * 1.0));
            tr_pos[0] += round(pos.getY() / (n * 1.0));
        } else {           //ALGO_FLAG = 1
            //callback_class = jniENV->FindClass("com/location/app/jni_models/PositioningAlgorithm");
            callback_class = jniENV->GetObjectClass(pa);
            LOGI("Inside native c++ method onPositionUpdate() pt2");
            callback_method = jniENV->GetMethodID(callback_class,"onPositionUpdate","(II)V");
            if(callback_method == NULL){
                LOGI("No method found");
            }
            LOGI("Inside native c++ method onPositionUpdate() pt Pos: x = %d y = %d",pos.getX(),pos.getY());
            jniENV->CallVoidMethod(pa, callback_method, pos.getX(),pos.getY());
        }
    } else {              // flag = 2 => DR update
        if (ALGO_FLAG == 0) {
            shared_lock<shared_mutex> lock(positionMutex);
            position_estimate = user_position + pos;    //DR gives increments.
            lock.unlock();
            process_covariance_estimate = process_covariance + process_noise;
            double kalman_gain =
                    process_covariance_estimate / (process_covariance_estimate + measurement_noise);
            Position newPos{static_cast<int>(position_estimate.getX() +
                                             kalman_gain * (tr_pos[0] - position_estimate.getX())),
                            static_cast<int>(position_estimate.getY() +
                                             kalman_gain * (tr_pos[1] - position_estimate.getY())),
                            0};
            unique_lock<shared_mutex> lock2(positionMutex);
            user_position.setX(newPos.getX());
            user_position.setY(newPos.getY());
            lock2.unlock();
            process_covariance = (1 - kalman_gain) * process_covariance_estimate;
            jniENV->CallVoidMethod(pa, callback_method, newPos.getX(),
                                      newPos.getY());
        } else {           //ALGO_FLAG = 1
            jniENV->CallVoidMethod(pa, callback_method, pos.getX(), pos.getY());
        }
    }
}

#endif //POSITIONING_ALGORITHM_H
