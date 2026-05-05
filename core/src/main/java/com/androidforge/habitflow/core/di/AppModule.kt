package com.androidforge.habitflow.core.di

import android.content.Context
import com.androidforge.habitflow.core.ads.AdManager
import com.androidforge.habitflow.data.ad.AdMobManagerImpl
import com.androidforge.habitflow.core.notifications.NotificationScheduler
import com.androidforge.habitflow.data.notifications.AndroidNotificationScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

/**
 * Hilt module for providing application-level dependencies.
 * This includes Coroutine Dispatchers, Notification Scheduler, and Ad Manager.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the default [CoroutineDispatcher] for general background work.
     */
    @Provides
    @Singleton
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    /**
     * Provides the IO [CoroutineDispatcher] for disk and network operations.
     */
    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provides an implementation of [NotificationScheduler].
     */
    @Provides
    @Singleton
    fun provideNotificationScheduler(@ApplicationContext context: Context): NotificationScheduler {
        return AndroidNotificationScheduler(context)
    }

    /**
     * Provides an implementation of [AdManager].
     */
    @Provides
    @Singleton
    fun provideAdManager(@ApplicationContext context: Context): AdManager {
        return AdMobManagerImpl(context)
    }
}