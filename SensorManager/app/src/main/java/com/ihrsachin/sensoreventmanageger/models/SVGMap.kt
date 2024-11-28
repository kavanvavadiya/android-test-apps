package com.ihrsachin.sensoreventmanageger.models

class SVGMap() {
    private lateinit var jsonFileName : String
    private lateinit var drawableName : String

    constructor(jsonFileName : String) : this() {
        this.jsonFileName = jsonFileName
        drawableName = jsonFileName.substringBefore('.')
    }

    fun getJsonFileName() : String{
        return jsonFileName
    }

    fun getDrawableName() : String{
        return drawableName
    }
}