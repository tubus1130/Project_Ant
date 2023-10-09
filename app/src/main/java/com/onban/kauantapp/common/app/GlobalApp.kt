package com.onban.kauantapp.common.app

import android.app.Application
import com.onban.kauantapp.di.AppComponent
import com.onban.kauantapp.di.DaggerAppComponent

class GlobalApp : Application() {

    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    private fun initializeComponent(): AppComponent {
        return DaggerAppComponent.factory().create(applicationContext)
    }
}