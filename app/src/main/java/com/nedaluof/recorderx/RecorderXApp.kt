package com.nedaluof.recorderx

import android.app.Application
import android.os.Build
import android.os.FileObserver
import com.nedaluof.recorderx.model.Status.ERROR
import com.nedaluof.recorderx.model.Status.SUCCESS
import com.nedaluof.recorderx.repository.RecorderXRepository
import com.nedaluof.recorderx.util.getAppPath
import com.nedaluof.recorderx.util.getAppPathAsFile
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
 * Created by nedaluof on 10/28/2020.
 */
@HiltAndroidApp
class RecorderXApp : Application() {

		@Inject
		lateinit var repository: RecorderXRepository

		override fun onCreate() {
				super.onCreate()
				if (BuildConfig.DEBUG) {
						Timber.plant(DebugTree())
				}
				initFileObserver()
		}

		/**
		 * file observer if the user delete records outside the app will detect it
		 * and delete it from the database
		 * */
		private lateinit var fileObserver: FileObserver

		private fun initFileObserver() {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
						fileObserver = object : FileObserver(getAppPathAsFile()) {
								override fun onEvent(event: Int, fileName: String?) {
										if (event == DELETE) {
												Timber.d("filePath from system: $fileName")
												// user deletes a recording file out of the app
												val recordPath = "${getAppPath()}/$fileName"
												Timber.d("filePath from me: $recordPath")
												deleteRecordX(recordPath)
										}
								}
						}
				} else {
						fileObserver = object : FileObserver(getAppPath()) {
								override fun onEvent(event: Int, fileName: String?) {
										if (event == DELETE) {
												Timber.d("fileName from system: $fileName")
												// user deletes a recording file out of the app
												val recordPath = "${getAppPath()}/$fileName"
												Timber.d("fileName from me: $recordPath")
												deleteRecordX(recordPath)
										}
								}
						}
				}
				if (this::fileObserver.isInitialized) {
						fileObserver.startWatching()
				}
		}

		fun deleteRecordX(recordxPath: String) {
				GlobalScope.launch(Dispatchers.IO) {
						val foundRecordX = repository.getRecordXByRecordXPath(recordxPath)
						when (foundRecordX.status) {
								SUCCESS -> {
										val resultOfDeletion = repository.deleteRecordX(foundRecordX.data!!)
										when (resultOfDeletion.status) {
												SUCCESS -> Timber.i("deletion: record deleted after observation detection")
												ERROR -> {
														val msg = "deletion: Error : ${foundRecordX.message}"
														Timber.e(msg)
												}
										}
								}
								ERROR -> {
										val msg = "getRecordXByPath Error: ${foundRecordX.message}"
										Timber.e(msg)
								}
						}
				}
		}


}
