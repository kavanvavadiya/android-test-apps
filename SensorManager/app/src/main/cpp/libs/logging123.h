//
// Created by yashp on 12/29/2022.
//

#ifndef SENSOR_EVENT_MANAGEGER_LOGGING123_H
#define SENSOR_EVENT_MANAGEGER_LOGGING123_H

#include "spdlog/sinks/android_sink.h"
//#include "logging123.h"
#include "spdlog/spdlog.h"

static const char* path_for_logger123;
class logger123{
public:
    // static std::string tag = "spdlog-android1";
    //static std::shared_ptr<spdlog::logger> mylogger1;
    //  static void android_example(const std::shared_ptr<spdlog::logger>& mylogger);
    static void print_log(std::shared_ptr<spdlog::logger> &printer, const std::string& a);


    static std::shared_ptr<spdlog::logger> mylogger1;
};


#endif //SENSOR_EVENT_MANAGEGER_LOGGING123_H
