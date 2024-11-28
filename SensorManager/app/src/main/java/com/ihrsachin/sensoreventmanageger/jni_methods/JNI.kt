package com.ihrsachin.sensoreventmanageger.jni_methods

class JNI {
    companion object {
        external fun startRecordingCheckedSensors(list: ArrayList<Int>)
        external fun stopRecording()
        init {
            System.loadLibrary("your_native_library_name")
        }
    }
}