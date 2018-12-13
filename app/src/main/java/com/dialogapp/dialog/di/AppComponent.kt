package com.dialogapp.dialog.di

import android.app.Application
import com.dialogapp.dialog.di.modules.AppModule
import com.dialogapp.dialog.ui.conversation.ConversationListFragment
import com.dialogapp.dialog.ui.home.*
import com.dialogapp.dialog.ui.login.LoginFragment
import com.dialogapp.dialog.ui.profile.ProfilePostsFragment
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
    fun inject(discoverFragment: DiscoverFragment)
    fun inject(moreFragment: MoreFragment)
    fun inject(profilePostsFragment: ProfilePostsFragment)
    fun inject(conversationListFragment: ConversationListFragment)
}