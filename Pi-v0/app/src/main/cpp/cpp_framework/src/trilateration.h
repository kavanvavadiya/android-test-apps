#ifndef TRILATERATION_H
#define TRILATERATION_H

#include"update_cycle.h"
#include<cmath>

using namespace std;


ScopedThread readerThread;

double inline rssiToDistance(int rssi){
    double n = 2;
    double rssi_d0 = -72;
    double d0 = 100;   //in cm
    double d = d0*pow(10,((rssi_d0-rssi)/(10*n)));
    return d;
}

void inline trilaterate(UpdateCycle currCycle){
    if(currCycle.noOfEntries()<3){
        LOGI("Not Enough Entries to perform trilateration");
        return;
    }

    forward_list<Entry> entries = currCycle.getEntries();
    double x_coors[3], y_coors[3];
    double distances[3];
    int i=0;
    for (Entry entry:  entries) {
        if(i<3){    //We just need 3 entries. Drop others.
            x_coors[i] = entry.getBeaconX();
            y_coors[i] = entry.getBeaconY();
            distances[i] = rssiToDistance(entry.getRssi());
            LOGI("Entry processed x = %f y = %f d = %f",x_coors[i], y_coors[i], distances[i]);
            i++;
        }
    }

    double tmp_matrix_1[4], tmp_vec_1[2], tmp_matrix_2[4], tmp_vec_2[2];
    tmp_matrix_1[0] = 2*(x_coors[0]-x_coors[2]);
    tmp_matrix_1[1] = 2*(y_coors[0]-y_coors[2]);
    tmp_matrix_1[2] = 2*(x_coors[1]-x_coors[2]);
    tmp_matrix_1[3] = 2*(y_coors[1]-y_coors[2]);

    tmp_vec_1[0] = x_coors[0]*x_coors[0]-x_coors[2]*x_coors[2]
            + y_coors[0]*y_coors[0]-y_coors[2]*y_coors[2]
            + distances[2]*distances[2]-distances[0]*distances[0];

    tmp_vec_1[1] = x_coors[1]*x_coors[1]-x_coors[2]*x_coors[2]
                   + y_coors[1]*y_coors[1]-y_coors[2]*y_coors[2]
                   + distances[1]*distances[1]-distances[0]*distances[0];

    tmp_matrix_2[0] = tmp_matrix_1[0]*tmp_matrix_1[0] + tmp_matrix_1[2]*tmp_matrix_1[2];
    tmp_matrix_2[1] = tmp_matrix_1[0]*tmp_matrix_1[1] + tmp_matrix_1[2]*tmp_matrix_1[3];
    tmp_matrix_2[2] = tmp_matrix_1[1]*tmp_matrix_1[0] + tmp_matrix_1[3]*tmp_matrix_1[2];
    tmp_matrix_2[3] = tmp_matrix_1[1]*tmp_matrix_1[1] + tmp_matrix_1[3]*tmp_matrix_1[3];

    tmp_vec_2[0] = tmp_matrix_1[0]*tmp_vec_1[0]+tmp_matrix_1[2]*tmp_vec_1[1];
    tmp_vec_2[1] = tmp_matrix_1[1]*tmp_vec_1[0]+tmp_matrix_1[3]*tmp_vec_1[1];

    double tmp_x = tmp_matrix_2[3]*tmp_vec_2[0]-tmp_matrix_2[1]*tmp_vec_2[1];
    double tmp_y = tmp_matrix_2[0]*tmp_vec_2[1]-tmp_matrix_2[2]*tmp_vec_2[0];

    double tmp_factor = tmp_matrix_2[0]*tmp_matrix_2[3]-tmp_matrix_2[1]*tmp_matrix_2[2];
    double x = tmp_x/tmp_factor;
    double y = tmp_y/tmp_factor;

    LOGI("Position on Trilateration is (%lf,%lf)",x,y);
    Position *newPos = new Position(x,y,240);
    onPositionUpdated(1, newPos);
    delete newPos;
}


void inline asynchronous_reader(){
	while(true){
		LOGI("Asynchronous Reader called");

		unique_lock<mutex> lock(updateQueueMutex);
		newUpdateCycleReady.wait(lock,[](){
			shared_lock<shared_mutex> lock2(trilaterationMutex);
			if(!isTrilaterationActive){
				lock2.unlock();
				return true;
			}
			lock2.unlock();
			return !update_queue.empty();
		});

		shared_lock<shared_mutex> lock2(trilaterationMutex);
		if(!isTrilaterationActive){
			LOGI("Exiting Asynchronous reader");
			lock2.unlock();
			break;
		}
		lock2.unlock();

		if(update_queue.size()>MAX_UPDATE_CYCLES_IN_QUEUE){
			while(update_queue.size()>1){
				LOGI("Popping update cycle");
				update_queue.pop();
			}
		}

		UpdateCycle currentUpdateCycle = update_queue.front();
		update_queue.pop();
		lock.unlock();

		LOGI("Running Reader on current update cycle: %s",currentUpdateCycle.toString().c_str());

		//pass current update cycle to trilateration/multilateration function.
		trilaterate(currentUpdateCycle);
	}
}

void startTrilateration(){
	unique_lock<shared_mutex> lock(trilaterationMutex);
	if(!isTrilaterationActive){
		isTrilaterationActive = true;
		lock.unlock();

		updateCycleThread.post(infiniteUpdateLoop);
		readerThread.post(asynchronous_reader);
	}
}

void stopTrilateration(){
	unique_lock<shared_mutex> lock(trilaterationMutex);
	isTrilaterationActive = false;
	lock.unlock();
	updateCycleThread.join();
	newUpdateCycleReady.notify_one();
	readerThread.join();
}

#endif
