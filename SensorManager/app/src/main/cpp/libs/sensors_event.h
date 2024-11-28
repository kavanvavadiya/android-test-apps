//
// Created by yashp on 12/29/2022.
//

#include "scoped_thread.h"
#include <android/sensor.h>
#include <android/log.h>
#include <dlfcn.h>
#include "spdlog/sinks/android_sink.h"
#include <string>
#include "spdlog/spdlog.h"
#include "logging123.h"

#ifndef SENSOR_EVENT_MANAGEGER_SENSORS_EVENT_H
#define SENSOR_EVENT_MANAGEGER_SENSORS_EVENT_H


#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,     "TAG", __VA_ARGS__)

class getpackage{
public:
    const char* pakagename;

    const char* getpc(){
        return pakagename;
    }


};

getpackage gp;

const char* kPackageName = gp.getpc();
//const char* kPackageName = "com.ihrsachin.sensoreventmanageger";

/**
 * {@link ASensorManager} is an opaque type to manage sensors and
 * events queues.
 * @return SensorManager, to manage all the sensors event
 */
ASensorManager* AcquireASensorManagerInstance(void) {
    typedef ASensorManager *(*PF_GETINSTANCEFORPACKAGE)(const char *name);
    void* androidHandle = dlopen("libandroid.so", RTLD_NOW);
    PF_GETINSTANCEFORPACKAGE getInstanceForPackageFunc = (PF_GETINSTANCEFORPACKAGE)
            dlsym(androidHandle, "ASensorManager_getInstanceForPackage");
    if (getInstanceForPackageFunc) {
        return getInstanceForPackageFunc(kPackageName);
    }

    typedef ASensorManager *(*PF_GETINSTANCE)();
    PF_GETINSTANCE getInstanceFunc = (PF_GETINSTANCE)
            dlsym(androidHandle, "ASensorManager_getInstance");
    // by all means at this point, ASensorManager_getInstance should be available
    assert(getInstanceFunc);
    return getInstanceFunc();
}

ASensorManager *aSensorManager;

/**
 * A super class to handle all the logic of
 * getting sensor,
 * enabling sensor,
 * creating event queue,
 * associate it with a looper in new thread,
 *
 * It is an abstract class, only for extension,
 * Update data at a constant frequency
 */
class SensorEventQueue{
protected:
    ScopedThread eventQueueThread;
    const int kNumEvents = 1;
    const int kTimeoutMilliSecs = 10;
    const int kLooperId = 3;
    ASensorEventQueue *eventQueue;
    logger123 lg;

    /**
     * Creates a new sensor event queue and associate it with a looper.
     * update data with the help of abstract function
     * Extended to all type of Sensors
     *
     * @param sensor_type : int -> type of sensor you want to get data from
     * @return 0 on success or a negative error code on failure.
     * @return
     * -1 -> Failed to get a sensor manager
     * -2 -> Failed to create a sensor event queue
     * -3 -> Specified sensor is not found
     * -4 -> Failed to enable sensor
     * -5 -> looper wasn't identified
     */
    int createEventQueue(int sensor_type){
        ALooper *looper;
        const ASensor *aSensor;   //TYPE_ANY
        aSensorManager = AcquireASensorManagerInstance();
        if (!aSensorManager) {
            return -1;
            LOGI("Failed to get a sensor manager");
        }

        /**
         * Prepares a looper associated with the calling thread, and returns it.
         * If the thread already has a looper, it is returned.  Otherwise, a new
         * one is created, associated with the thread, and returned.
         */
        looper = ALooper_prepare(ALOOPER_PREPARE_ALLOW_NON_CALLBACKS);


        /**
         * Creates a new sensor event queue and associate it with a looper.
         */
        eventQueue = ASensorManager_createEventQueue(
                aSensorManager,
                looper,
                kLooperId, nullptr
                /* no callback */, nullptr /* no data */);

        if (!eventQueue) {
            LOGI("Failed to create a sensor event queue");
            return -2;
        } else{
            LOGI("Created a sensor event queue");
        }

        aSensor = ASensorManager_getDefaultSensor(aSensorManager, sensor_type);
        if(aSensor == nullptr){
            LOGI("specified sensor is not found");
            return -3;
        }
        else
            LOGI("found required sensor");

        auto status = ASensorEventQueue_enableSensor(eventQueue, aSensor);
        if(status >=0) LOGI("Sensor Enabled");
        else{
            LOGI("Failed to enable sensor");
            return -4;
        }


        ASensorEventQueue_setEventRate(eventQueue, aSensor, 10000);

        while (isEnable) {
            int ident = ALooper_pollOnce(kTimeoutMilliSecs,
                                         nullptr /* no output file descriptor */,
                                         nullptr /* no output event */,
                                         nullptr /* no output data */);


            /**
             * "ident" is a identifier for the events that will be returned when
             * calling ALooper_pollOnce(). The identifier must be >= 0, or
             * ALOOPER_POLL_CALLBACK if providing a non-NULL callback.
             */
            if (ident == kLooperId) {
//            LOGI("ident == kLooperId");
                ASensorEvent pEvent;
                if (ASensorEventQueue_getEvents(eventQueue, &pEvent,kNumEvents)) {
                    updateData(sensor_type, &pEvent,lg);
                } else {
                  //  LOGI("Failed to get sensors' data");
                }
            } else{
                LOGI("looper wasn't identified");
                return -5;
            }
        }
        return 0;
    }
    virtual void updateData(int sensor_type, ASensorEvent *pEvent,logger123 lg) = 0;

public:
    /**
     * Public methods to enable sensors
     * @param sensor_type
     * @return 0 on success or a negative error code on failure.
     */
    bool enableSensor(int sensor_type){
        isEnable = true;
        eventQueueThread.post(&SensorEventQueue::createEventQueue, this, sensor_type);
        return true;
    };

    /**
     * methods to disable sensor,
     * data update will be stopped,
     * however you'll be able to get data, which will basically be a constant garbage value
     */
    void disableSensor(){
        isEnable = false;
        eventQueueThread.join();
    };

    bool isEnable;
};


/**
 * Accelerometer Events
 */
class AccelerationEventQueue : public SensorEventQueue{
    virtual void updateData(int sensor_type, ASensorEvent *pEvent,logger123 lg) {
        val[0] = pEvent->acceleration.x;
        val[1] = pEvent->acceleration.y;
        val[2] = pEvent->acceleration.z;
        LOGI("acc x: %f, y: %f, z: %f", val[0], val[1], val[2]);

    }
public:
    /**
     * val[0] : acceleration in x direction
     * val[1] : acceleration in y direction
     * val[2] : acceleration in z direction
     */
    float val[3];
};


/**
 * Gyroscope Events
 */
class GyroscopeEventQueue : public SensorEventQueue{
    /**
     * check latency
     * @param sensor_type
     * @param pEvent
     */
    virtual void updateData(int sensor_type, ASensorEvent *pEvent,logger123 lg) {
        val[0] = pEvent->uncalibrated_gyro.x_uncalib;
        val[1] = pEvent->uncalibrated_gyro.y_uncalib;
        val[2] = pEvent->uncalibrated_gyro.z_uncalib;
        val[3] = pEvent->uncalibrated_gyro.x_bias;
        val[4] = pEvent->uncalibrated_gyro.y_bias;
        val[5] = pEvent->uncalibrated_gyro.z_bias;
        LOGI("gyro x: %f, y: %f, z: %f", val[0], val[1], val[2]);
    }
public:

    /**
    * val[0] : gyroscope in x direction
    * val[1] : gyroscope in y direction
    * val[2] : gyroscope in z direction
    * val[3] : gyroscope bias in x direction
    * val[4] : gyroscope bias in y direction
    * val[5] : gyroscope bias in z direction
    */
    float val[6];
};

/**
 * Earth's Magnetic Field Events
 */
class MagneticEventQueue : public SensorEventQueue{
    virtual void updateData(int sensor_type, ASensorEvent *pEvent, logger123 lg) {
        val[0] = pEvent->magnetic.x;
        val[1] = pEvent->magnetic.y;
        val[2] = pEvent->magnetic.z;
        LOGI("mag x: %f, y: %f, z: %f", val[0], val[1], val[2]);
    }
public:
    /**
     * val[0] : Magnetic Field in x direction
     * val[1] : Magnetic Field in y direction
     * val[2] : Magnetic Field in z direction
     */
    float val[3];
};


/**
 * Device Rotation Vector Events
 */
class VectorEventQueue : public SensorEventQueue{
    virtual void updateData(int sensor_type, ASensorEvent *pEvent,logger123 lg) {
        val[0] = pEvent->vector.x;
        val[1] = pEvent->vector.y;
        val[2] = pEvent->vector.z;
        LOGI("vector x: %f, y: %f, z: %f", val[0], val[1], val[2]);
    }
public:
    /**
     * val[0] : Rotation Vector in x direction
     * val[1] : Rotation Vector in y direction
     * val[2] : Rotation Vector in z direction
     */
    float val[3];
};


/**
 * Step Counter Events
 */
class StepCounterEventQueue : public SensorEventQueue {
    virtual void updateData(int sensor_type, ASensorEvent *pEvent,logger123 lg) {
        val = pEvent->u64.step_counter;
        LOGI("step_counter x: %f", val);
    }

public:
    /**
     * step counter
     */
    float val;
};


/**
 * Step Counter Events
 */
class TemperatureEventQueue : public SensorEventQueue {
    virtual void updateData(int sensor_type, ASensorEvent *pEvent,logger123 lg) {
        val = pEvent->temperature;
        LOGI("temperature: %f", val);
    }

public:
    /**
     * temperature
     */
    float val;
};


#endif //SENSOR_EVENT_MANAGEGER_SENSORS_EVENT_H
