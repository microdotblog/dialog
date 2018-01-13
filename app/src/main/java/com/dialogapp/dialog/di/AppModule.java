package com.dialogapp.dialog.di;

import com.dialogapp.dialog.api.MicroblogService;

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
                .build()
                .create(MicroblogService.class);
    }
}
