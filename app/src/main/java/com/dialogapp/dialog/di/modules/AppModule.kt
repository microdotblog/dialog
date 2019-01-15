package com.dialogapp.dialog.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkerFactory
import com.dialogapp.dialog.CoroutinesDispatcherProvider
import com.dialogapp.dialog.StethoImplementation
import com.dialogapp.dialog.api.MicroblogService
import com.dialogapp.dialog.api.ServiceInterceptor
import com.dialogapp.dialog.db.InMemoryDb
import com.dialogapp.dialog.db.MicroBlogDb
import com.dialogapp.dialog.util.DaggerWorkerFactory
import com.dialogapp.dialog.util.calladapters.ApiResponseCallAdapterFactory
import com.dialogapp.dialog.util.calladapters.LiveDataCallAdapterFactory
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideMicroblogService(okHttpClient: OkHttpClient): MicroblogService {
        return Retrofit.Builder()
                .baseUrl("https://micro.blog/")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(ApiResponseCallAdapterFactory())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .client(okHttpClient)
                .build()
                .create(MicroblogService::class.java)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: ServiceInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
                .addInterceptor(interceptor)

        return StethoImplementation.addStetho(builder).build()
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): MicroBlogDb {
        return MicroBlogDb.getInstance(app)!!
    }

    @Singleton
    @Provides
    fun provideInMemoryDb(app: Application): InMemoryDb {
        return InMemoryDb.getInstance(app)!!
    }

    @Singleton
    @Provides
    fun provideSharedPref(app: Application): SharedPreferences {
        return app.getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideCoroutineDispatchers() = CoroutinesDispatcherProvider(
            Dispatchers.Main,
            Dispatchers.Default,
            Dispatchers.IO
    )

    @Provides
    @Singleton
    fun provideWorkerFactory(microblogService: MicroblogService,
                             diskDb: MicroBlogDb,
                             inMemDb: InMemoryDb): WorkerFactory {
        return DaggerWorkerFactory(microblogService, diskDb, inMemDb)
    }
}