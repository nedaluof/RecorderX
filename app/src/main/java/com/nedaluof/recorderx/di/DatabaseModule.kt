package com.nedaluof.recorderx.di

import android.content.Context
import androidx.room.Room
import com.nedaluof.recorderx.db.RecorderXDao
import com.nedaluof.recorderx.db.RecorderXDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by nedaluof on 10/28/2020.
 */
@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
  @Singleton
  @Provides
  fun provideRecorderXDatabase(@ApplicationContext context: Context): RecorderXDatabase =
    Room.databaseBuilder(
      context,
      RecorderXDatabase::class.java,
      RecorderXDatabase.DB_NAME
    ).build()

  @Singleton
  @Provides
  fun provideRecorderXDao(database: RecorderXDatabase): RecorderXDao =
    database.recorderXDao
}
