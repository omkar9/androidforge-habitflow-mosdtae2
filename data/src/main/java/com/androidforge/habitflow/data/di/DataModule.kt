package com.androidforge.habitflow.data.di

import android.content.Context
import androidx.room.Room
import com.androidforge.habitflow.core.common.Constants
import com.androidforge.habitflow.data.local.dao.HabitCompletionDao
import com.androidforge.habitflow.data.local.dao.HabitDao
import com.androidforge.habitflow.data.local.database.HabitFlowDatabase
import com.androidforge.habitflow.data.local.mapper.HabitCompletionMapper
import com.androidforge.habitflow.data.local.mapper.HabitMapper
import com.androidforge.habitflow.data.local.util.Converters
import com.androidforge.habitflow.data.repository.HabitRepositoryImpl
import com.androidforge.habitflow.domain.repository.HabitRepository
import com.androidforge.habitflow.domain.usecase.streak.CalculateStreakUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing dependencies for the data layer.
 * This includes Room database, DAOs, mappers, and repository implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    /**
     * Provides the Room database instance for the application.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HabitFlowDatabase {
        return Room.databaseBuilder(
            context,
            HabitFlowDatabase::class.java,
            Constants.DATABASE_NAME
        )
        .addTypeConverter(Converters::class)
        .fallbackToDestructiveMigration()
        .build()
    }

    /**
     * Provides the [HabitDao] from the database.
     */
    @Provides
    @Singleton
    fun provideHabitDao(database: HabitFlowDatabase): HabitDao {
        return database.habitDao()
    }

    /**
     * Provides the [HabitCompletionDao] from the database.
     */
    @Provides
    @Singleton
    fun provideHabitCompletionDao(database: HabitFlowDatabase): HabitCompletionDao {
        return database.habitCompletionDao()
    }

    /**
     * Provides the [HabitRepository] implementation.
     */
    @Provides
    @Singleton
    fun provideHabitRepository(
        habitDao: HabitDao,
        habitCompletionDao: HabitCompletionDao,
        habitMapper: HabitMapper,
        habitCompletionMapper: HabitCompletionMapper,
        calculateStreakUseCase: CalculateStreakUseCase
    ): HabitRepository {
        return HabitRepositoryImpl(
            habitDao,
            habitCompletionDao,
            habitMapper,
            habitCompletionMapper,
            calculateStreakUseCase
        )
    }
}