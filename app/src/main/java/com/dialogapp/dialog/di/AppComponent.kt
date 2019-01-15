package com.dialogapp.dialog.di

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkerFactory
import com.dialogapp.dialog.auth.SessionManager
import com.dialogapp.dialog.di.modules.AppModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun sessionManager(): SessionManager
    fun viewModelFactory(): ViewModelProvider.Factory
    fun workerFactory(): WorkerFactory
}