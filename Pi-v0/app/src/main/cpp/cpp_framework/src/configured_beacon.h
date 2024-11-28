#ifndef CONFIGURED_BEACON_H
#define CONFIGURED_BEACON_H

#include<vector>
#include<utility>
#include<algorithm>		//for std::find
#include"positioning_algorithm.h"
#include"filter.h"
#include "position.h"

using namespace std;

class ConfiguredBeacon{

protected:
	int rssi_d0;
	int d0;
	string uuid;
	int major;
	int minor;
	Position pos;
	[[maybe_unused]] float beacon_coeff;
	float x_sigma;
	vector<Filter *> filters; 	// filter observers
	ConfiguredBeacon* next = nullptr;		//for chaining - maybe.
								//Not required if HashTable is a separate class.

	ConfiguredBeacon():rssi_d0{DEFAULT_RSSI_D0}, d0{DEFAULT_D0}, uuid{""}, major{-1}, minor{-1}, pos{DEFAULT_POS}, beacon_coeff{DEFAULT_BEACON_COEFF},
			x_sigma{DEFAULT_X_SIGMA}, next{nullptr}{}

	//Moving a ConfiguredBeacon would delete the vector of filters in the original object.
	ConfiguredBeacon(ConfiguredBeacon&&) = delete;
	ConfiguredBeacon& operator=(ConfiguredBeacon&&) = delete;

public:
	virtual ~ConfiguredBeacon(){}

	ConfiguredBeacon(const ConfiguredBeacon&) = default;
	ConfiguredBeacon& operator=(ConfiguredBeacon&) = default;

	bool operator==(const ConfiguredBeacon& beacon) const{
		if((this->getUuid() == beacon.getUuid())&&(this->getMajor() == beacon.getMajor())&&(this->getMinor() == beacon.getMinor())){
			return true;
		}
		else{
			return false;
		}
	}
	bool operator!=(const ConfiguredBeacon& beacon) const{
		return !(this->operator==(beacon));
	}

	class Builder{
	private:
		ConfiguredBeacon* cnfg_beacon_ptr;

	public:
		Builder* create(){
			cnfg_beacon_ptr = new ConfiguredBeacon();
			return this;
		}

		Builder* setId(string uuid_, int major_, int minor_){
			cnfg_beacon_ptr -> uuid = std::move(uuid_);
			cnfg_beacon_ptr -> major = major_;
			cnfg_beacon_ptr -> minor = minor_;
			return this;
		}

		Builder* setPos(int x, int y, int z){
			cnfg_beacon_ptr -> pos = Position(x,y,z);
			return this;
		}

		Builder* setRssiD0(int rssi){
			cnfg_beacon_ptr -> rssi_d0 = rssi;
			return this;
		}

		Builder* setD0(int d){
			cnfg_beacon_ptr -> d0 = d;
			return this;
		}

		Builder* setBeaconCoeff(float coeff){
			cnfg_beacon_ptr -> beacon_coeff = coeff;
			return this;
		}

		Builder* setXSigma(float x_sig){
			cnfg_beacon_ptr -> x_sigma = x_sig;
			return this;
		}

		Builder* registerFilter(const Filter* filter){
			//A copy of the filter should be made here because the filter object is deleted
			//dynamically from java.
			if(const auto* f = dynamic_cast<const MeanFilter*>(filter); f!=nullptr) {
				//Executes if the Filter was indeed of type MeanFilter.
				(cnfg_beacon_ptr->filters).push_back(new MeanFilter{*f});
			}
			return this;
		}

		[[maybe_unused]] Builder* removeFilter(Filter* filter){
			auto iterator = find_if((cnfg_beacon_ptr -> filters).begin(),(cnfg_beacon_ptr -> filters).end(),[filter](Filter * f){
				return (*filter) == (*f);
			});
			if(iterator!= (cnfg_beacon_ptr -> filters).end()){
				(cnfg_beacon_ptr -> filters).erase(iterator);
			}
			//what otherwise
			return this;
		}

		ConfiguredBeacon& build(){
			return *cnfg_beacon_ptr;
		}
	};

	string getUuid() const{
		return uuid;
	}

	int getMajor() const{
		return major;
	}

	int getMinor() const{
		return minor;
	}

	[[ maybe_unused ]] ConfiguredBeacon* getNext() const{
		return next;		//Inside the hash function this pointer can be checked as not null
	}

	vector<Filter*> getFilters() const{
		return filters;
	}

	Position getPosition() const{
		return pos;
	}

	string toString() const{
		return "ConfiguredBeacon{"+uuid+", "+to_string(major)+", "+to_string(minor)+"}";
	}
};

vector<ConfiguredBeacon> configured_beacons;

/**
 * Adding to configured_beacons is not thread-safe. Add Beacons
 * from a single thread only.
 * @param configured_beacon
 */
bool addToConfiguredBeacons(const ConfiguredBeacon& configured_beacon){
	for(const ConfiguredBeacon& beacon : configured_beacons){
		if(beacon == configured_beacon){
			LOGI("There is already beacon with the same id");
			return false;
		}
	}
	//The Beacon object is dynamically deleted from Java so make
	//sure that a copy is made here.
	configured_beacons.push_back(configured_beacon);
	LOGI("Beacon Added : %s",configured_beacon.toString().c_str());
	return true;
}



#endif
