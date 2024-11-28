package com.project_ips.piv0.jni_models;

public abstract class BeaconCallback {

    private static native void jni_resolve(String uuid, int major, int minor, int rssi);

    public static void resolve(String uuid, int major, int minor, int rssi){
        jni_resolve(uuid, major, minor, rssi);
    }

    public abstract void onBeaconCallback(String uuid, int major, int minor, int rssi);
}
