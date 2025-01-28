package com.rossyn.blocktiles.game2048.di

import android.content.Context
import android.content.res.Resources
import com.rossyn.blocktiles.game2048.services.MusicService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module for providing dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the application context.
     *
     * @param context The application context.
     * @return The application context.
     */
    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    /**
     * Provides the resources.
     *
     * @param context The application context.
     * @return The resources.
     */
    @Provides
    fun provideResources(@ApplicationContext context: Context): Resources {
        return context.resources
    }


    @Provides
    @Singleton
    fun provideMusicService(): MusicService {
        return MusicService()
    }
}