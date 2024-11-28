package com.project_ips.piv0.jni_models;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import com.project_ips.piv0.MainActivity;

public class PositioningAlgorithm {
    public enum FILTER_FLAG{MeanFilter,KalmanFilter}
    public enum ALGO_FLAG{Trilateration,DeadReckoning,Fusion}

    private static PositioningAlgorithm instance;

    private ALGO_FLAG algo_flag;
    private static final String TAG = PositioningAlgorithm.class.getSimpleName();
    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private Sensor magnetometerSensor;
    private Sensor stepDetectorSensor;
    DRListener drListener;

    private static native void jni_initialize(String className, String methodName, String signature, PositioningAlgorithm pa);
    private static native void jni_begin(int algo, int start_x, int start_y);
    private static native void jni_stop();

    private PositioningAlgorithm(){}

    public static PositioningAlgorithm getInstance(){
        if(instance==null){
            instance = new PositioningAlgorithm();
        }
        return instance;
    }

    public void create(/*Activity activity*/){
        jni_initialize("com/project_ips/piv0/jni_models/PositioningAlgorithm","onPositionUpdate",
                "(II)V",this);
//        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
//        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
//        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
//        drListener = DRListener.initializeDR();
    }

    public void begin(ALGO_FLAG algo, int start_x, int start_y){
        algo_flag = algo;
        if(algo_flag == ALGO_FLAG.Trilateration){
            jni_begin(1, start_x, start_y);
        }
//        else if(algo_flag == ALGO_FLAG.DeadReckoning){
//            jni_begin(2, start_x, start_y);
//            sensorManager.registerListener(drListener,stepDetectorSensor,SensorManager.SENSOR_DELAY_NORMAL);
//            sensorManager.registerListener(drListener, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
//            sensorManager.registerListener(drListener,magnetometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
//        }
//        else{
//            jni_begin(0,start_x,start_y);
//            sensorManager.registerListener(drListener,stepDetectorSensor,SensorManager.SENSOR_DELAY_NORMAL);
//            sensorManager.registerListener(drListener, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
//            sensorManager.registerListener(drListener,magnetometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
//        }
    }

    public void stop(){
        //jni_stop is blocked so that thread recreation is not required on trilateration restart.
        //jni_stop();
        if(algo_flag == ALGO_FLAG.Trilateration){
            //Nothing required here.
        }
        else if(algo_flag == ALGO_FLAG.DeadReckoning){
            sensorManager.unregisterListener(drListener);
        }
        else{
            sensorManager.unregisterListener(drListener);
        }
    }

    public void onPositionUpdate(int x_coordinate, int y_coordinate){    //user position
        /*
        For some reason, the Logs present in this method are not shown. But this method
        does get called when it is supposed to. This can be tested via assertions as
        below.
         */
        //assert false;
        //Log.d("Auro : ", "Received Position Update x = "+x_coordinate+" y = "+y_coordinate);
        if(instance.algo_flag == ALGO_FLAG.Trilateration){
            //Position Updates Here.
            Log.d("Auro : ", "Received Position Update x = "+x_coordinate+" y = "+y_coordinate);
            //Run update on UI Thread.
            MainActivity.UPDATE.onPositionUpdate(x_coordinate,y_coordinate);
        }
        else if(instance.algo_flag == ALGO_FLAG.DeadReckoning){

        }
        else{

        }
    }
}

