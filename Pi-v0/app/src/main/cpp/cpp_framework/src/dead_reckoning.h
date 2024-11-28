//
// Created by aurok on 23-01-2022.
//

#ifndef DEAD_RECKONING_H
#define DEAD_RECKONING_H

#include "positioning_algorithm.h"

const int step_length = 74;   //in cm.

void inline onStepDetected(float rotMatrix[]){
    int inc_x = rotMatrix[1]*step_length;
    int inc_y = rotMatrix[4]*step_length;
    onPositionUpdated(2,Position(inc_x,inc_y,0));
}

#endif //DEAD_RECKONING_H
