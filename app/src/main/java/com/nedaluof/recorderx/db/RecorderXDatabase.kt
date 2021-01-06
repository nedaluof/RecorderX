package com.nedaluof.recorderx.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nedaluof.recorderx.model.RecordX

/**
 * Created by nedaluof on 10/28/2020.
 */
@Database(entities = [RecordX::class], exportSchema = false, version = 1)
abstract class RecorderXDatabase : RoomDatabase() {

  abstract val recorderXDao: RecorderXDao

  companion object {
    const val DB_NAME = "RecorderX.db"
  }
}
