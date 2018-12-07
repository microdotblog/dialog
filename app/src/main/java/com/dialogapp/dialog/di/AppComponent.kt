package com.dialogapp.dialog.di

import android.app.Application
import com.dialogapp.dialog.di.modules.AppModule
import com.dialogapp.dialog.ui.home.HomeFragment
import com.dialogapp.dialog.ui.home.MentionsFragment
import com.dialogapp.dialog.ui.home.TimelineFragment
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
    fun inject(homeFragment: HomeFragment)
    fun inject(timelineFragment: TimelineFragment)
    fun inject(mentionsFragment: MentionsFragment)
}