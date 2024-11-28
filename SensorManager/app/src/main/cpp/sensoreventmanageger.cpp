#include "senor_manager.h"
#include <iostream>
#include <fstream>
#include <cstring>
#include <ctime>
#include <string>
#include <sstream>
#include <iomanip>
using namespace std;
#include <android/log.h>

#define LOGI(...) __android_log_print(ANDROID_LOG_ERROR, "TRACKERS", "%s", __VA_ARGS__);

extern "C"
JNIEXPORT void JNICALL
Java_com_ihrsachin_sensoreventmanageger_jni_1methods_JNI_00024Companion_startRecordingCheckedSensors(
        JNIEnv *env,
        jobject thiz,
        jobject list) {

    envToEnableSensors = env;
    sensorList = list;


    // Get the current time
    std::time_t currentTime = std::time(nullptr);

    // Convert the current time to a struct tm
    std::tm* localTime = std::localtime(&currentTime);

    // Format the date and time
    std::stringstream ss;
    ss << std::put_time(localTime, "%Y_%m_%d_%H_%M_%S");
    std::string dateTimeString = ss.str();

    std::string filePath = std::string("/data/data/com.ihrsachin.sensoreventmanageger/files/csv_") + dateTimeString + ".csv";

    // Convert the filePath string to const char*
    file_path_sensor_rec = filePath.c_str();

    start_rec();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_ihrsachin_sensoreventmanageger_jni_1methods_JNI_00024Companion_stopRecording(JNIEnv *env,
                                                                                      jobject thiz) {
    stop_rec();
}