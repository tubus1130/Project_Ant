package com.onban.network.di

import com.onban.network.api.NewsApi
import com.onban.network.mapper.NetworkResponseAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun providesNewsApi(converterFactory: Converter.Factory, callAdapterFactory: CallAdapter.Factory): NewsApi {
        return Retrofit.Builder()
            .baseUrl("http://3.37.25.179")
            .addCallAdapterFactory(callAdapterFactory)
            .addConverterFactory(converterFactory)
            .build()
            .create(NewsApi::class.java)
    }

    @Provides
    @Singleton
    fun providesConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    fun provideCallAdapterFactory(): CallAdapter.Factory {
        return NetworkResponseAdapterFactory()
    }
}