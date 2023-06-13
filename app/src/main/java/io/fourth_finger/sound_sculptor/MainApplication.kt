package io.fourth_finger.sound_sculptor

import android.app.Application

class MainApplication: Application() {

    val envelopeRepository = EnvelopeRepository(EnvelopeDataSource())

}