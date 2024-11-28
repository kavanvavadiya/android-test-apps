package com.project_ips.piv0

import com.project_ips.piv0.views.MapView

class PositionUpdate(v: MapView) {
    private val view: MapView = v

    fun onPositionUpdate(x: Int, y:Int){
        view.show_cli = true
        view.cli_x = ((x)/100.0).toFloat()
        view.cli_y = ((y)/100.0).toFloat()
        view.post { view.invalidate() }
    }
}