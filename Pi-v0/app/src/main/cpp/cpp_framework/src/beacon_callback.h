//
// Created by aurok on 23-01-2022.
//

#ifndef BEACON_CALLBACK_H
#define BEACON_CALLBACK_H

#include "active_beacon.h"

void inline resolve(string uuid, int major, int minor, int rssi){
    LOGI("Resolving %s %d %d %d",uuid.c_str(),major,minor,rssi);

    shared_lock<shared_mutex> lock(trilaterationMutex);
    if(!isTrilaterationActive){		//lock on isTrilaterationActive doesn't seem critical here.
        LOGI("trilateration is not active");
        lock.unlock();
        return;
    }
    lock.unlock();

    if(rssi<MIN_ACCEPTABLE_RSSI){	//Ignore very weak signals
        return;
    }

//	int index = hash(id);
//	configured_beacons[index] == id;
//	configured_beacons[index].getNext() == id;

    unique_lock<shared_mutex> lock2(activeBeaconMutex);
    for(ActiveBeacon& active_beacon : active_beacons){
        if((active_beacon.getUuid()==uuid)&&(active_beacon.getMajor()==major)&&(active_beacon.getMinor()==minor)){
            LOGI("Found in Active Beacons");
            active_beacon.setRssi(rssi);
            lock2.unlock();
            return;
        }
    }
    lock2.unlock();

    for(const ConfiguredBeacon& cnf_beacon : configured_beacons){
        if((cnf_beacon.getUuid() == uuid)&&(cnf_beacon.getMajor()==major)&&(cnf_beacon.getMinor()==minor)){
            LOGI("Initialized from configured beacons");
            ActiveBeacon new_active_beacon{&cnf_beacon, rssi};
            unique_lock<shared_mutex> lock3(activeBeaconMutex);
            active_beacons.push_back(new_active_beacon);
            lock3.unlock();
            break;
        }
    }
    LOGI("Configured Beacon not found");
}

#endif //BEACON_CALLBACK_H
