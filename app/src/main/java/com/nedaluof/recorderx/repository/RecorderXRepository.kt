package com.nedaluof.recorderx.repository

import com.nedaluof.recorderx.db.RecorderXDao
import com.nedaluof.recorderx.model.RecordX
import com.nedaluof.recorderx.model.Result
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber

/**
 * Created by nedaluof on 10/30/2020.
 */
@Singleton
class RecorderXRepository @Inject constructor(
		private val recorderXDao: RecorderXDao,
) {

		fun getAllRecordX() =
				recorderXDao.getAllRecordX()
						.distinctUntilChanged()

		suspend fun addNewRecordX(recordx: RecordX): Result<Boolean> {
				return try {
						recorderXDao.addNewRecordX(recordx)
						Result.success(true)
				} catch (e: Exception) {
						Timber.d("addNewRecordX: ${e.message!!}")
						Result.error(null, e.message!!)
				}
		}

		suspend fun deleteRecordX(recordx: RecordX): Result<Boolean> {
				return try {
						recorderXDao.deleteRecordX(recordx)
						Result.success(true)
				} catch (e: Exception) {
						Timber.d("deleteRecord: ${e.message!!}")
						Result.error(null, e.message!!)
				}
		}

		suspend fun updateRecordX(recordx: RecordX): Result<Boolean> {
				return try {
						recorderXDao.updateRecordX(recordx)
						Result.success(true)
				} catch (e: Exception) {
						Timber.d("updateRecord: ${e.message!!}")
						Result.error(null, e.message!!)
				}
		}

		suspend fun getRecordXByRecordXPath(recordxPath: String): Result<RecordX> {
				return try {
						val foundRecordX = recorderXDao.getRecordXByPath(recordxPath)
						Result.success(foundRecordX)
				} catch (e: Exception) {
						Timber.d("getRecordXByRecordPath: ${e.message!!}")
						Result.error(null, e.message!!)
				}
		}

		fun getRecordXCount() = recorderXDao.countRecordX()
}
