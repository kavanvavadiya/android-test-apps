//
// Created by rjskg on 26-08-2022.
//
#include "libs/sensors_event.h"
#include "fstream"
#include "jni.h"

#ifndef SENSOR_EVENT_MANAGEGER_SENSOR_REC_H
#define SENSOR_EVENT_MANAGEGER_SENSOR_REC_H

AccelerationEventQueue accelerationEventQueue;
GyroscopeEventQueue gyroscopeEventQueue;
MagneticEventQueue magneticEventQueue;
VectorEventQueue vectorEventQueue;
TemperatureEventQueue temperatureEventQueue;
StepCounterEventQueue stepCounterEventQueue;


// object to write in file
std::fstream csv_file;

// Both are initialized in sensoreventmanager.cpp file in startRecordingCheckedSensors() method
jobject sensorList;
JNIEnv *envToEnableSensors;

void enableRequiredSensors() {
    // Get the class and method IDs for ArrayList class and the get method
    jclass arrayListClass = envToEnableSensors->FindClass("java/util/ArrayList");
    jmethodID getMethodID = envToEnableSensors->GetMethodID(arrayListClass, "get", "(I)Ljava/lang/Object;");

    jmethodID sizeMethodID = envToEnableSensors->GetMethodID(arrayListClass, "size", "()I");
    jint listSize = envToEnableSensors->CallIntMethod(sensorList, sizeMethodID);

    for (int i = 0; i < listSize; i++) {
        // Call the get method to retrieve the element at the given index
        jobject elementObject = envToEnableSensors->CallObjectMethod(sensorList, getMethodID, i);

        // Convert the jobject to jint
        jint elementValue = envToEnableSensors->CallIntMethod(elementObject, envToEnableSensors->GetMethodID(envToEnableSensors->GetObjectClass(elementObject), "intValue", "()I"));

        // Use the elementValue as needed
        if(elementValue == ASENSOR_TYPE_ACCELEROMETER)
            accelerationEventQueue.enableSensor(ASENSOR_TYPE_ACCELEROMETER);

        if(elementValue == ASENSOR_TYPE_MAGNETIC_FIELD)
            magneticEventQueue.enableSensor(ASENSOR_TYPE_MAGNETIC_FIELD);

        if(elementValue == ASENSOR_TYPE_GYROSCOPE)
            gyroscopeEventQueue.enableSensor(ASENSOR_TYPE_GYROSCOPE);

        if(elementValue == ASENSOR_TYPE_ROTATION_VECTOR)
            vectorEventQueue.enableSensor(ASENSOR_TYPE_ROTATION_VECTOR);

        if(elementValue == ASENSOR_TYPE_AMBIENT_TEMPERATURE)
            temperatureEventQueue.enableSensor(ASENSOR_TYPE_AMBIENT_TEMPERATURE);

        if(elementValue == ASENSOR_TYPE_STEP_COUNTER)
            stepCounterEventQueue.enableSensor(ASENSOR_TYPE_STEP_COUNTER);

    }
}

void disableAll(){
    if(accelerationEventQueue.isEnable) accelerationEventQueue.disableSensor();
    if(gyroscopeEventQueue.isEnable) gyroscopeEventQueue.disableSensor();
    if(magneticEventQueue.isEnable) magneticEventQueue.disableSensor();
    if(vectorEventQueue.isEnable) vectorEventQueue.disableSensor();
    if(temperatureEventQueue.isEnable) temperatureEventQueue.disableSensor();
    if(stepCounterEventQueue.isEnable) stepCounterEventQueue.disableSensor();
}


// handled in start_rec() and stop_rec() functions in same file
bool isRecording = false;


// path where data is to be recorded
const char* file_path_sensor_rec;

void rec_data(){
    enableRequiredSensors();
    // Initializing the file
    csv_file.open(file_path_sensor_rec, std::ios::in|std::ios::out|std::ios::app);
    LOGI("%s", file_path_sensor_rec);
    if(csv_file.is_open()) LOGI("file is opened");
    else LOGI("File is not opened");

    csv_file << "Acceleration_x,Acceleration_y,Acceleration_z,Gyro_uncalib_x,Gyro_uncalib_y,Gyro_uncalib_z,Gyro_bias_x,Gyro_bias_y,Gyro_bias_z,Magnetic_x,Magnetic_y,Magnetic_z,Vector_x,Vector_y,Vector_z,temperature,StepCounter\n";

    while (isRecording) {

        if(accelerationEventQueue.isEnable){
            csv_file << accelerationEventQueue.val[0] << ","
                     << accelerationEventQueue.val[1] << ","
                     << accelerationEventQueue.val[2] << ",";
        }
        else{
            csv_file<<".,.,.,";
        }
        if(gyroscopeEventQueue.isEnable){
            csv_file << gyroscopeEventQueue.val[0] << ","
                     << gyroscopeEventQueue.val[1] << ","
                     << gyroscopeEventQueue.val[2] << ","
                     << gyroscopeEventQueue.val[3] << ","
                     << gyroscopeEventQueue.val[4] << ","
                     << gyroscopeEventQueue.val[5] << ",";
        } else csv_file << ".,.,.,.,.,.,";

        if(magneticEventQueue.isEnable){
            csv_file << magneticEventQueue.val[0] << ","
                     << magneticEventQueue.val[1] << ","
                     << magneticEventQueue.val[2] << ",";
        } else csv_file << ".,.,.,";

        if(vectorEventQueue.isEnable){
            csv_file << vectorEventQueue.val[0] << ","
                     << vectorEventQueue.val[1] << ","
                     << vectorEventQueue.val[2] << ",";
        } else csv_file << ".,.,.,";

        if(temperatureEventQueue.isEnable) csv_file << temperatureEventQueue.val << ",";
        else csv_file << ".,";

        if(stepCounterEventQueue.isEnable) csv_file << stepCounterEventQueue.val << "\n";

        this_thread::sleep_for(5ms);
    }

}

ScopedThread recThread;

void start_rec(){
    isRecording = true;
    recThread.post(rec_data);
}
void stop_rec(){
    disableAll();
    isRecording = false;
    recThread.join();
}

#endif //SENSOR_EVENT_MANAGEGER_SENSOR_REC_H