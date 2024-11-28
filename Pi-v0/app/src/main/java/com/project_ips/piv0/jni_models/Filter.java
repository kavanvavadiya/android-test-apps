package com.project_ips.piv0.jni_models;

public interface Filter {
   // long getPointer();

    long getPointer();

    void freeObject();;

    static void free(Filter filter) {
        filter.freeObject();
    }
}
