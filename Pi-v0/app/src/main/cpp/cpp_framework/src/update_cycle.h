#ifndef UPDATE_CYCLE_H
#define UPDATE_CYCLE_H

#include<forward_list>
#include"../libs/scoped_thread.h"
#include"active_beacon.h"

using namespace std;

void infiniteUpdateLoop();

/**
 * It should contain values that are strictly necessary for trilateration.
 */
class Entry{

private:
	int estimatedRssi;
	Position pos;

public:
	Entry(int estimatedRssi, Position pos): estimatedRssi{estimatedRssi}, pos{pos}{}
	~Entry(){}

	Entry(const Entry&) = default;
	Entry& operator=(const Entry&) = default;

	//Move semantics is basically the same as copy semantics so no need to add it.

	int getRssi() const{
		return estimatedRssi;
	}

	int getBeaconX(){
		return pos.getX();
	}

	int getBeaconY(){
		return pos.getY();
	}

	string toString() const{
		string str = "{"+pos.toString()+" : "+to_string(estimatedRssi)+"}";
		return str;
	}
};

ScopedThread updateCycleThread;
condition_variable newUpdateCycleReady;

class UpdateCycle{

private:
	/**
	 * Contains entries in descending order of rssi
	 */
	forward_list<Entry> update_cycle;
	unsigned short no_of_entries;

	/**
	 * Contains logic for insertion into the update_cycle
	 */
	void insert(const Entry& entry){
		if(no_of_entries<3){
			addEntry(entry);
			return;
		}
		else{
			if((update_cycle.front().getRssi())<entry.getRssi()){
				update_cycle.pop_front();
				no_of_entries--;
				addEntry(entry);
			}
		}
	}

	/**
	 * For insertion sort into the list.
	 */
	void addEntry(const Entry& entry){
		if(no_of_entries == 0){
			update_cycle.push_front(entry);
			no_of_entries++;
			return;
		}

		if(entry.getRssi()<=update_cycle.front().getRssi()){
			update_cycle.push_front(entry);
			no_of_entries++;
			return;
		}

		auto it = update_cycle.begin();
		forward_list<Entry>::iterator temp;
		while(true){
			temp=it;
			it++;
			if(it==update_cycle.end()){
				update_cycle.insert_after(temp,entry);
				no_of_entries++;
				return;
			}
			else if(entry.getRssi()<(it->getRssi())){
				update_cycle.insert_after(temp, entry);
				no_of_entries++;
				return;
			}
		}
	}

	UpdateCycle():no_of_entries{0}{}

public:
	~UpdateCycle(){}

	UpdateCycle(const UpdateCycle&) = default;
	UpdateCycle& operator=(const UpdateCycle&) = default;

	UpdateCycle(UpdateCycle&&) = default;
	UpdateCycle& operator=(UpdateCycle&&) = default;

	static void generateUpdateCycle(vector<ActiveBeacon>& beacons);

	string toString(){
		string str = "[";
		if(!update_cycle.empty()){
			for(const Entry& entry : update_cycle){
				str += entry.toString()+",";
			}
		}
		str+="]";
		return str;
	}

	int noOfEntries(){
	    return no_of_entries;
	}

	forward_list<Entry> getEntries(){
	    return update_cycle;
	}
};

queue<UpdateCycle> update_queue;
mutex updateQueueMutex;

void UpdateCycle::generateUpdateCycle(vector<ActiveBeacon> &beacons) {
	string str = "[";
	for(ActiveBeacon& beac: beacons){
		str+=beac.toString()+",";
	}
	str+="]";
	LOGI("%s",str.c_str());

	UpdateCycle update_cycle;
	for(auto it = beacons.begin(); it!=beacons.end();){
		if(!(it->isAlive())){
			it = beacons.erase(it);
			continue;
		}
		LOGI("Update Cycle' : ");
		LOGI("%s",update_cycle.toString().c_str());
		it->updateInactiveFlag();
		Entry new_entry{(it->getEstimatedRssi()),(it->getPosition())};
		update_cycle.insert(new_entry);
		it++;
	}
	LOGI("Update Cycle : ");
	LOGI("%s",update_cycle.toString().c_str());
	//put update cycle into queue.
	unique_lock<mutex> lock(updateQueueMutex);
	update_queue.push(update_cycle);
	lock.unlock();
	newUpdateCycleReady.notify_one();		//notifies the asynchronous reader
}

void infiniteUpdateLoop(){
	while(true){
		shared_lock<shared_mutex> lock(trilaterationMutex);
		if(!isTrilaterationActive){
			break;
		}
		lock.unlock();

		unique_lock<shared_mutex> lock2(activeBeaconMutex);
		UpdateCycle::generateUpdateCycle(active_beacons);	//move should not occur here.
		lock2.unlock();

		this_thread::sleep_for(chrono::milliseconds(DEFAULT_UPDATE_CYCLE_FREQUENCY));
	}
}

#endif
