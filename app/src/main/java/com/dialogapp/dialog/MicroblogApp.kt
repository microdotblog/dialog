package com.dialogapp.dialog

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.dialogapp.dialog.di.AppComponent
import com.dialogapp.dialog.di.DaggerAppComponent
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber

open class MicroblogApp : Application() {

    lateinit var appComponent: AppComponent
        private set

    companion object {
        private var INSTANCE: MicroblogApp? = null

        @JvmStatic
        fun get(): MicroblogApp = INSTANCE!!
    }

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)

        Timber.plant(TimberImplementation())

        INSTANCE = this
        appComponent = DaggerAppComponent.builder().application(this).build()

        configureWorkManager()
    }

    private fun configureWorkManager() {
        val config = Configuration.Builder()
                .setWorkerFactory(appComponent.workerFactory())
                .build()

        WorkManager.initialize(this, config)
    }
}
