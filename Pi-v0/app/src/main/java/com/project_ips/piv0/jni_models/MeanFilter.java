package com.project_ips.piv0.jni_models;

public class MeanFilter implements Filter{

    private final long mean_filter_ptr;

    private static native long jni_allocate(int windowSize);

    private native void jni_free(long pointer);

    private MeanFilter(int windowSize){
        mean_filter_ptr = jni_allocate(windowSize);
    }

    public static MeanFilter allocate(int windowSize){
        return new MeanFilter(windowSize);
    }

    @Override
    public void freeObject(){
        jni_free(mean_filter_ptr);
    }

    @Override
    public long getPointer() {
        return mean_filter_ptr;
    }
}
