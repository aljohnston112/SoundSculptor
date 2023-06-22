package io.fourth_finger.sound_sculptor.data_class

import java.lang.ref.WeakReference

/**
 * A min and a max.
 */
data class MinMax(val min: Float, val max: Float)

/**
 * An observer of minmax values.
 */
interface MinMaxObserver {

    /**
     * Called when there is an update to the minmax.
     */
    fun update(minMax: MinMax)
}

/**
 * An observable of a min and max wrapped in a class.
 */
class MinMaxSubject(private var minMax: MinMax) {

    private val observers = mutableSetOf<WeakReference<MinMaxObserver>>()

    /**
     * Update the minmax and notify observers of the update.
     *
     * @param minMax The new minmax
     */
    fun updateMinMax(minMax: MinMax){
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

    /**
     * Attaches an observer to this subject, so they get notified of minmax updates.
     *
     * @param minMaxObserver The observer to attach.
     */
    fun attach(minMaxObserver: MinMaxObserver){
        observers.add(WeakReference(minMaxObserver))
    }

    /**
     * Detaches an observer from this subject, so they will no longer get notified of minmax updates.
     *
     * @param minMaxObserver The observer to detach.
     */
    fun detach(minMaxObserver: MinMaxObserver){
        observers.remove(WeakReference(minMaxObserver))
    }

    /**
     * Notifies the attached observers of the current minmax.
     */
    private fun notifyObservers(){
        for(observer in observers){
            observer.get()?.update(minMax)
        }
    }

}