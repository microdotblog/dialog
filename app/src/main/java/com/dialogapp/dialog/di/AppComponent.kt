package com.dialogapp.dialog.di

import android.app.Application
import com.dialogapp.dialog.di.modules.AppModule
import com.dialogapp.dialog.ui.home.ListFragment
import com.dialogapp.dialog.ui.login.LoginFragment
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

    fun inject(loginFragment: LoginFragment)
    fun inject(listFragment: ListFragment)
}