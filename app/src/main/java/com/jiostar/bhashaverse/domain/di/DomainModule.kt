package com.jiostar.bhashaverse.domain.di

import android.content.Context
import com.jiostar.bhashaverse.data.di.MediaOkHttpClient
import com.jiostar.bhashaverse.domain.AudioPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    @IoDispatcher
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideAudioPlayer(
        @ApplicationContext context: Context,
        @MediaOkHttpClient okMediaHttpClient: OkHttpClient
    ): AudioPlayer {
        return AudioPlayer(context, okMediaHttpClient)
    }
}