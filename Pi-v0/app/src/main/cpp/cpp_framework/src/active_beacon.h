#ifndef ACTIVE_BEACON_H
#define ACTIVE_BEACON_H

#include"configured_beacon.h"
#include<shared_mutex>

class ActiveBeacon{

private:
	const ConfiguredBeacon* cnfg_beacon_ptr;		//a pointer to the configuration of the beacon with the same id
	int rssi;
	int inactive_flag;

	void inline notifyFilters(){
		if((cnfg_beacon_ptr->getFilters()).empty()){
				//handle the case
			return;
			}

		for(Filter* filter : (cnfg_beacon_ptr->getFilters())){
			if(filter == nullptr){
				//handle the case
				return;
			}
			filter->update(getRssi());
		}
	}

public:
	/**
	 * Instead of inheritance, a pointer to the beacon configuration is kept
	 * for fast initialization of ActiveBeacons.
	 */
	ActiveBeacon(const ConfiguredBeacon* cnfg_beacon_ptr, int rssi): cnfg_beacon_ptr{cnfg_beacon_ptr}, rssi{rssi},
	inactive_flag{0}{
		setRssi(rssi);
	}

	~ActiveBeacon(){}

	ActiveBeacon(const ActiveBeacon&) = default;
	ActiveBeacon& operator=(const ActiveBeacon&) = default;

	ActiveBeacon(ActiveBeacon&&) = default;
	ActiveBeacon& operator=(ActiveBeacon&&) = default;

	void inline setRssi(int rssi_){
		this->rssi = rssi_;
		resetInactiveFlag();
		notifyFilters();
	}

	int inline getRssi() const{
		return rssi;
	}

	/**
	 * This function just returns  the estimated rssi of the first filter.
	 * Later on compound logic can be worked out.
	 */
	int inline getEstimatedRssi() const{
		if((cnfg_beacon_ptr->getFilters()).empty()){
			return rssi;	//if no filters return the measured value
		}

		Filter* firstFilter = (cnfg_beacon_ptr->getFilters()).front();

		if(firstFilter!=nullptr){
			return firstFilter -> getEstimatedRssi();
		}

		return rssi;	//if no filters return the measured value
	}

	string inline getUuid() const{
		return cnfg_beacon_ptr->getUuid();
	}

	int inline getMajor() const{
		return cnfg_beacon_ptr->getMajor();
	}

	int inline getMinor() const{
		return cnfg_beacon_ptr->getMinor();
	}

	Position inline getPosition() const{
		return cnfg_beacon_ptr->getPosition();
	}

	void inline updateInactiveFlag(){
		inactive_flag ++;
	}

	void inline resetInactiveFlag(){
		inactive_flag = 0;
	}

	bool inline isAlive() const{
		if(inactive_flag <= MAX_INACTIVE_FLAG){
			return true;
		}
		else{
			return false;
		}
	}

	string inline toString() const{
		string str = "{"+getPosition().toString()+","+to_string(getEstimatedRssi())+","+to_string(inactive_flag)+"}";
		return str;
	}
};

vector<ActiveBeacon> active_beacons;
shared_mutex activeBeaconMutex;

#endif
