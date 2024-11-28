package com.project_ips.piv0.jni_models;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Arrays;

public class DRListener implements SensorEventListener {

    private final float[] gravityReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];

    private static final String TAG = DRListener.class.getSimpleName();

    private native void onStepDetected(float[] rotArray);

    public static DRListener initializeDR(){
        return new DRListener();
    }

    private DRListener(){
        super();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            SensorManager.getRotationMatrix(rotationMatrix, null, gravityReading, magnetometerReading);
            Log.d(TAG, "Step Detected: "+Arrays.toString(rotationMatrix));
            onStepDetected(rotationMatrix);
        }
        else if(sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY){
            System.arraycopy(sensorEvent.values, 0, gravityReading, 0, gravityReading.length);
            Log.d(TAG, "Gravity update: "+ Arrays.toString(gravityReading));
        }
        else{
            //sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD;
            System.arraycopy(sensorEvent.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
            Log.d(TAG, "Magnetometer Update: "+Arrays.toString(magnetometerReading));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        if(sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            switch(i){
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    //Good data
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    //okay data.
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    //Needs Calibration
                case SensorManager.SENSOR_STATUS_UNRELIABLE:
                    //Needs Calibration
                    //TODO: Sensor calibration prompt
            }
        }
    }
}
