#ifndef FILTER_H
#define FILTER_H

#include<queue>
#include<math.h>

using namespace std;

class Filter{

protected:
	int estimatedRssi;
	Filter(){}

public:
	virtual ~Filter(){}

	Filter(const Filter&) = default;
	Filter& operator=(const Filter&) = default;

    /**
     * Update the state of this filter observer
     * @param rssi new rssi
     */
    virtual void update(int rssi) = 0;

    virtual bool operator==(const Filter&)=0;
	virtual bool operator!=(const Filter& filter){
		return !(this->operator==(filter));
	}

	int getEstimatedRssi() const{
    	return estimatedRssi;
    }
};

class MeanFilter : public Filter{

private:
	int windowSize;
	int sampleAccumulator;
	queue<int> dataset;

public:
	MeanFilter(int window): windowSize{window}, sampleAccumulator{0}{
		if(window<1){
			windowSize = 1;
		}
	}
	~MeanFilter(){}

	virtual bool operator==(const Filter& filter) override{
		if(const MeanFilter* f = dynamic_cast<const MeanFilter*>(&filter); f!=nullptr){
			//Executes if the Filter was indeed of type MeanFilter.
			if(this->windowSize == f->windowSize){
				return true;
			}
			return false;
		}
		return false;
	}

	void inline update(int rssi) override{

		if((int)dataset.size()==windowSize){
			sampleAccumulator -= dataset.front();
			dataset.pop();
		}

		dataset.push(rssi);
		sampleAccumulator += rssi;
		estimatedRssi = round(sampleAccumulator*1.0/(dataset.size()));
	}
};

#endif
