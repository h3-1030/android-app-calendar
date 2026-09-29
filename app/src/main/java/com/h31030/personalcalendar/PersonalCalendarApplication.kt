package com.h31030.personalcalendar

import android.app.Application

class PersonalCalendarApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
