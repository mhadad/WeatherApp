package com.mf.weatherapp

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext

//@Module
@HiltAndroidApp
class App  : Application() {
//    @Provides
//    @ApplicationContext
//    fun provideApplicationContext(application: Application): Context {
//        return application.applicationContext
//    }

}