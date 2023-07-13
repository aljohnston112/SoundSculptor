package io.fourth_finger.sound_sculptor.view

import android.content.Context
import android.view.View
import io.fourth_finger.sound_sculptor.data_class.ModuleData

class Module(context: Context): View(context) {

    private val inConnections = mutableListOf<ModuleData>()
    private val outConnections = mutableListOf<ModuleData>()

    fun addIncomingConnection(moduleData: ModuleData){
        inConnections.add(moduleData)
    }

    fun addOutgoingConnection(moduleData: ModuleData){
        outConnections.add(moduleData)
    }

}