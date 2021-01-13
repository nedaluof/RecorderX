package com.nedaluof.recorderx.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nedaluof.recorderx.model.RecordX
import kotlinx.coroutines.flow.Flow

/**
 * Created by nedaluof on 10/28/2020.
 */
@Dao
interface RecorderXDao {

		@Insert(onConflict = OnConflictStrategy.REPLACE)
		suspend fun addNewRecordX(recordX: RecordX)

		@Delete
		suspend fun deleteRecordX(recordX: RecordX)

		@Update
		suspend fun updateRecordX(recordX: RecordX)

		@Query("SELECT COUNT(*) FROM RecordX")
		fun countRecordX(): Flow<Int>

		@Query("SELECT * FROM RecordX")
		fun getAllRecordX(): Flow<List<RecordX>>

		@Query("SELECT * FROM RecordX WHERE filePath = :recordPath")
		suspend fun getRecordXByPath(recordPath: String): RecordX
}
