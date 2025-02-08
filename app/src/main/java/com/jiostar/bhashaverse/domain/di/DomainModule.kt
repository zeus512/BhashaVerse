package com.jiostar.bhashaverse.domain.di

import android.content.Context
import com.jiostar.bhashaverse.data.ApiService
import com.jiostar.bhashaverse.data.di.MediaOkHttpClient
import com.jiostar.bhashaverse.domain.AppRepository
import com.jiostar.bhashaverse.domain.AudioPlayer
import com.jiostar.bhashaverse.domain.usecase.ExtractEventsUseCase
import com.jiostar.bhashaverse.domain.usecase.ProcessAudioUseCase
import com.jiostar.bhashaverse.domain.usecase.ProcessTextUseCase
import com.jiostar.bhashaverse.domain.usecase.TranscribeAudioUseCase
import com.jiostar.bhashaverse.domain.usecase.TranslateTextUseCase
import com.jiostar.bhashaverse.domain.usecase.TtsAudioUseCase
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


    @Provides
    @Singleton
    fun provideAppRepository(apiService: ApiService): AppRepository {
        return AppRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideTranslateTextUseCase(appRepository: AppRepository): TranslateTextUseCase {
        return TranslateTextUseCase(appRepository)
    }

    @Provides
    @Singleton
    fun provideTranscribeAudioUseCase(appRepository: AppRepository): TranscribeAudioUseCase {
        return TranscribeAudioUseCase(appRepository)
    }

    @Provides
    @Singleton
    fun provideExtractEventsUseCase(appRepository: AppRepository): ExtractEventsUseCase {
        return ExtractEventsUseCase(appRepository)
    }

    @Provides
    @Singleton
    fun provideProcessTextUseCase(appRepository: AppRepository): ProcessTextUseCase {
        return ProcessTextUseCase(appRepository)
    }

    @Provides
    @Singleton
    fun provideProcessAudioUseCase(appRepository: AppRepository): ProcessAudioUseCase {
        return ProcessAudioUseCase(appRepository)
    }

    @Provides
    @Singleton
    fun provideTtsAudioUseCase(appRepository: AppRepository): TtsAudioUseCase {
        return TtsAudioUseCase(appRepository)
    }

}