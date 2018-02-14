package com.dialogapp.dialog.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.dialogapp.dialog.api.MicroblogEnvelopeConverterFactory;
import com.dialogapp.dialog.api.MicroblogService;
import com.dialogapp.dialog.api.ServiceInterceptor;
import com.dialogapp.dialog.db.AccountDao;
import com.dialogapp.dialog.db.MicroBlogDb;
import com.dialogapp.dialog.db.PostsDao;
import com.dialogapp.dialog.util.LiveDataCallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module(includes = ViewModelModule.class)
class AppModule {
    @Singleton
    @Provides
    MicroblogService provideMicroblogService(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl("https://micro.blog/")
                .addConverterFactory(new MicroblogEnvelopeConverterFactory())
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(okHttpClient)
                .build()
                .create(MicroblogService.class);
    }

    @Singleton
    @Provides
    OkHttpClient provideOkHttpClient(ServiceInterceptor interceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
    }

    @Singleton
    @Provides
    ServiceInterceptor provideServiceInterceptor() {
        return new ServiceInterceptor();
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

    @Singleton
    @Provides
    PostsDao providePostsDao(MicroBlogDb db) {
        return db.postsDao();
    }
}