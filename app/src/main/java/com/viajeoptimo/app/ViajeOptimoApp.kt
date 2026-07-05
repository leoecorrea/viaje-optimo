package com.viajeoptimo.app

import android.app.Application
import com.viajeoptimo.app.data.AppDataStore

class ViajeOptimoApp : Application() {
    val dataStore by lazy { AppDataStore(this) }
}
