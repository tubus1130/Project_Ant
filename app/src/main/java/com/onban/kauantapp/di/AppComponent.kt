package com.onban.kauantapp.di

import android.content.Context
import com.onban.kauantapp.view.AnalysisActivity
import com.onban.kauantapp.view.HomeActivity
import com.onban.kauantapp.view.MainActivity
import com.onban.network.di.NetworkModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, ViewModelModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(mainActivity: MainActivity)
    fun inject(homeActivity: HomeActivity)
    fun inject(analysisActivity: AnalysisActivity)
}