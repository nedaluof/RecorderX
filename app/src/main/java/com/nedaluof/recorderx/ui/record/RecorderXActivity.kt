package com.nedaluof.recorderx.ui.record

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.SystemClock
import android.widget.Chronometer
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nedaluof.recorderx.R
import com.nedaluof.recorderx.component.RecorderXService
import com.nedaluof.recorderx.databinding.ActivityRecorderxBinding
import com.nedaluof.recorderx.ui.savedrecords.SavedRecordsBottomSheet
import com.nedaluof.recorderx.util.toast
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class RecorderXActivity : AppCompatActivity() {


		// chronometer
		private var pauseOffset: Long = 0
		private var running = true

		//progress drawable
		private var progressValue = 1

		// service
		private val recorderXServiceIntent by lazy {
				Intent(this, RecorderXService::class.java)
		}

		//colors list of start / stop button
		private val colorList by lazy {
				listOf(
						ContextCompat.getColor(this, R.color.red),
						ContextCompat.getColor(this, R.color.x_2)
				)
		}

		//view model



		//view binding
		private lateinit var binding: ActivityRecorderxBinding

		override fun onCreate(savedInstanceState: Bundle?) {
				super.onCreate(savedInstanceState)
				binding = ActivityRecorderxBinding.inflate(layoutInflater)
				setContentView(binding.root)
				initChronometer()
				binding.controlRecordBtn.setOnClickListener { askPermission() }
				binding.savedRecordBtn.setOnClickListener { showSavedRecords() }

		}

		private fun askPermission() {
				if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(
								applicationContext, PERMISSION
						)
				) {
						ActivityCompat.requestPermissions(
								this,
								arrayOf(PERMISSION),
								PER_REQ
						)
				} else {
						record()
				}
		}


		private fun showSavedRecords() {
				val bottomSheetDialogFragment = SavedRecordsBottomSheet()
				bottomSheetDialogFragment.show(supportFragmentManager, SavedRecordsBottomSheet.TAG)
		}

		private fun initChronometer() {
				binding.recordChronometer.apply {
						base = SystemClock.elapsedRealtime()
						onChronometerTickListener = Chronometer.OnChronometerTickListener {
								if (progressValue <= 100) {
										progressValue += 1
										updateProgressBar()
								} else if (progressValue > 100) {
										progressValue = 0
										updateProgressBar()
								}
						}
				}
		}

		private fun record() {
				if (running) {
						this.toast(R.string.toast_recording_start)
						binding.recordChronometer.apply {
								base = SystemClock.elapsedRealtime() - pauseOffset
								start()
						}

						binding.controlRecordBtn.apply {
								icon = ContextCompat.getDrawable(
										context,
										R.drawable.ic_stop
								)
								backgroundTintList = ColorStateList.valueOf(colorList[0])
								text = getString(R.string.control_record_btn_label_2)
						}
						binding.recordStatusTxt.text = getString(R.string.txt_user_indicator_recording)
						running = false
						// start RecorderXService
						startService(recorderXServiceIntent)
				} else {
						stopRecord()
				}
		}

		override fun onRequestPermissionsResult(
				requestCode: Int,
				permissions: Array<String?>,
				grantResults: IntArray,
		) {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
						if (requestCode == PER_REQ) {
								record()
						}
				} else {
						Timber.d("Permission failed")
				}
		}

		private fun stopRecord() {
				binding.recordChronometer.stop()
				binding.controlRecordBtn.apply {
						icon = ContextCompat.getDrawable(
								context,
								R.drawable.ic_record
						)
						backgroundTintList = ColorStateList.valueOf(colorList[1])
						text = getString(R.string.control_record_btn_label_1)
				}
				pauseOffset = SystemClock.elapsedRealtime() - binding.recordChronometer.base
				binding.recordStatusTxt.text = getString(R.string.txt_user_indicator_start)
				running = true

				stopService(recorderXServiceIntent)
				// record_chronometer reset
				updateView()
		}

		private fun updateView() {
				binding.recordChronometer.base = SystemClock.elapsedRealtime()
				pauseOffset = 0
				progressValue = 0
				updateProgressBar()
		}

		private fun updateProgressBar() {
				binding.controlledProgressBar.progress = progressValue
		}


		override fun onDestroy() {
				stopRecord()
				super.onDestroy()
		}

		companion object {
				private const val PER_REQ = 21
				private const val PERMISSION = android.Manifest.permission.RECORD_AUDIO
		}
}
