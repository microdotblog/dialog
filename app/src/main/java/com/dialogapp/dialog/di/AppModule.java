package com.dialogapp.dialog.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.dialogapp.dialog.api.MicroblogService;
import com.dialogapp.dialog.db.AccountDao;
import com.dialogapp.dialog.db.MicroBlogDb;
import com.dialogapp.dialog.util.LiveDataCallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module(includes = ViewModelModule.class)
class AppModule {
    @Singleton
    @Provides
    MicroblogService provideMicroblogService() {
        return new Retrofit.Builder()
                .baseUrl("https://micro.blog/")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(MicroblogService.class);
    }

    @Singleton
    @Provides
    MicroBlogDb provideDb(Application app) {
        return Room.databaseBuilder(app, MicroBlogDb.class, "microblog.db").build();
    }

    @Singleton
    @Provides
    AccountDao provideAccountDao(MicroBlogDb db) {
        return db.accountDao();
    }
}
