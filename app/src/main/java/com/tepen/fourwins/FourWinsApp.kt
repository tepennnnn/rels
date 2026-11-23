package com.tepen.fourwins

import android.app.Application
import com.tepen.fourwins.data.AppRepository
import com.tepen.fourwins.notify.ReminderScheduler

class FourWinsApp : Application() {

    lateinit var repository: AppRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = AppRepository(this)
        ReminderScheduler.syncReminders(this, repository.profile.value)
    }
}
