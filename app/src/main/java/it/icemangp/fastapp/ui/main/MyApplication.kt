package it.icemangp.fastapp.ui.main

import android.app.Application
import it.icemangp.shakenetworklog.data.NetworkLogManager

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        NetworkLogManager.init(this)
    }

}