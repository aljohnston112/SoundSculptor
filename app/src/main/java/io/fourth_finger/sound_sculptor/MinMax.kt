package io.fourth_finger.sound_sculptor

import java.lang.ref.WeakReference

data class MinMax(val min: Float, val max: Float)

class MinMaxSubject(private var minMax: MinMax) {

    private val observers = mutableSetOf<WeakReference<MinMaxObserver>>()

    fun setMinMax(minMax: MinMax){
        if (minMax.min == minMax.max) {
            // Add room around constant
            this.minMax = MinMax(
                minOf(this.minMax.min, minMax.min - 1),
                maxOf(this.minMax.max, minMax.max + 1)
            )
        } else {
            this.minMax = MinMax(
                minOf(this.minMax.min, minMax.min),
                maxOf(this.minMax.max, minMax.max)
            )
        }
        notifyObservers()
    }

    fun attach(minMaxObserver: MinMaxObserver){
        observers.add(WeakReference(minMaxObserver))
    }

    fun detach(minMaxObserver: MinMaxObserver){
        observers.remove(WeakReference(minMaxObserver))
    }

    private fun notifyObservers(){
        for(observer in observers){
            observer.get()?.update(minMax)
        }
    }

}

interface MinMaxObserver {
    fun update(minMax: MinMax)
}