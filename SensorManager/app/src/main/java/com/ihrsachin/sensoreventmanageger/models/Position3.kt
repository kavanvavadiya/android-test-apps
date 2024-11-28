package com.ihrsachin.sensoreventmanageger.models

import com.google.gson.annotations.SerializedName

data class Position3 constructor(
    @SerializedName("x") var x : Double,
    @SerializedName("y") var y : Double,
    @SerializedName("z") var z : Double){
    operator fun plusAssign(pos: Position3) {
        x+=pos.x
        y+=pos.y
        z+=pos.z
    }

    operator fun div(i: Int): Position3 {
        return Position3(x/i,y/i,z/i)
    }
}