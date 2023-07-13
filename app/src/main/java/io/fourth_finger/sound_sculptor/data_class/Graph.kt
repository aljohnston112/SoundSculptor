package io.fourth_finger.sound_sculptor.data_class

import io.fourth_finger.sound_sculptor.view.Module

class Graph {

    private val modules = mutableListOf<Module>()

    fun addModule(module: Module){
        modules.add(module)
    }

    fun getWidth(): Int {
        TODO()
    }

    fun getHeight(): Int {
        TODO()
    }

}
