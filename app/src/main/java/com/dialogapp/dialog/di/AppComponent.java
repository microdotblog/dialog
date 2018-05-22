package com.dialogapp.dialog.di;

import android.app.Application;

import com.dialogapp.dialog.MicroblogApp;
import com.dialogapp.dialog.di.viewmodule.ConversationActivityModule;
import com.dialogapp.dialog.di.viewmodule.FavoritesActivityModule;
import com.dialogapp.dialog.di.viewmodule.ImageViewerActivityModule;
import com.dialogapp.dialog.di.viewmodule.LauncherActivityModule;
import com.dialogapp.dialog.di.viewmodule.LoginActivityModule;
import com.dialogapp.dialog.di.viewmodule.MainActivityModule;
import com.dialogapp.dialog.di.viewmodule.ProfileActivityModule;
import com.dialogapp.dialog.di.viewmodule.ReplyActivityModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        AppModule.class,
        LoginActivityModule.class,
        FavoritesActivityModule.class,
        ProfileActivityModule.class,
        ImageViewerActivityModule.class,
        ConversationActivityModule.class,
        LauncherActivityModule.class,
        ReplyActivityModule.class,
        MainActivityModule.class
})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }
    void inject(MicroblogApp microblogApp);
}
