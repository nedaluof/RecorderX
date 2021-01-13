package com.nedaluof.recorderx.component

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.nedaluof.recorderx.R
import com.nedaluof.recorderx.model.RecordX
import com.nedaluof.recorderx.model.Status
import com.nedaluof.recorderx.repository.RecorderXRepository
import com.nedaluof.recorderx.util.createNotification
import com.nedaluof.recorderx.util.getAppPath
import com.nedaluof.recorderx.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by nedaluof on 10/29/2020.
 */
@AndroidEntryPoint
class RecorderXService : LifecycleService() {

		@Inject
		lateinit var repository: RecorderXRepository

		private var mediaRecorder = MediaRecorder()
		private var fileName: String? = null
		private var filePath: String? = null
		private var startingTimeMillis = 0L
		private var elapsedMillis = 0L
		private var elapsedSeconds = 0
		private val timer = Timer()
		private var incrementTimerTask: TimerTask? = null
		private val notificationMgr by lazy {
				getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		}

		private var count = 0

		override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
				super.onStartCommand(intent, flags, startId)
				lifecycleScope.launch {
						repository.getRecordXCount().collect {
								updateCount(it)
						}
				}
				return START_NOT_STICKY
		}

		private fun updateCount(it: Int) {
				count = it
				startRecordX()
		}

		private fun startRecordX() {
				prepareFilePath()
				mediaRecorder.apply {
						setAudioSource(MediaRecorder.AudioSource.MIC)
						setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
						setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
						setAudioChannels(1)
						setOutputFile(filePath)
						// high quality record / may eat the battery
						setAudioSamplingRate(44100)
						setAudioEncodingBitRate(192000)
				}

				try {
						mediaRecorder.prepare()
						mediaRecorder.start()
						startingTimeMillis = System.currentTimeMillis()

						// startTimer()
						// startForeground(1, createNotification())
				} catch (e: Exception) {
						Timber.d("startRecordX: ${e.message}")
				}
		}

		private fun prepareFilePath() {
				fileName =
						getString(R.string.default_file_name) + "_" + (count + 1) + ".mp4"
				filePath = "${this.getAppPath()}/$fileName"
		}

		@Suppress("UNUSED")
		private fun startTimer() {
				Timber.d("startTimer: ")
				incrementTimerTask = object : TimerTask() {
						override fun run() {
								elapsedSeconds++
								notificationMgr.notify(1, createNotification(notificationMgr, elapsedSeconds))
								Timber.d("startTimer: notification manager notify")
						}
				}
				timer.scheduleAtFixedRate(incrementTimerTask, 1000, 1000)
		}

		private fun stopRecordX() {
				val recordFinish = getString(R.string.toast_recording_finish) + " " + filePath
				try {
						mediaRecorder.stop()
						elapsedMillis = System.currentTimeMillis() - startingTimeMillis
						mediaRecorder.release()
						incrementTimerTask?.cancel()
						incrementTimerTask = null
						val record = RecordX(fileName!!, filePath!!, elapsedMillis)
						GlobalScope.launch {
								val result = repository.addNewRecordX(record)
								when (result.status) {
										Status.SUCCESS -> {
												addedSuccessFully(recordFinish)
										}
										Status.ERROR -> {
												failToAdd(result.message!!)
										}
								}
						}
				} catch (r: RuntimeException) {
						Timber.d("stopRecordX: ${r.message}")
				}
		}

		private fun failToAdd(message: String) {
				Handler(Looper.getMainLooper()).post {
						this@RecorderXService.toast("$message : Not Saved")
				}
		}

		private fun addedSuccessFully(recordFinish: String) {
				Handler(Looper.getMainLooper()).post {
						this@RecorderXService.toast(recordFinish)
				}
		}

		override fun onDestroy() {
				stopRecordX()
				super.onDestroy()
		}
}
