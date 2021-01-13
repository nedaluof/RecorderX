@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package com.nedaluof.recorderx.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.util.Util
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nedaluof.recorderx.R
import com.nedaluof.recorderx.ui.record.RecorderXActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by nedaluof on 12/9/2020.
 */
fun Context.toast(msg: String) =
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

fun Context.toast(@StringRes msg: Int) =
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

fun Context.createNotification(
		notificationMgr: NotificationManager,
		elapsedSeconds: Int,
): Notification? {
		val channelId = this.getString(R.string.default_notification_channel_id)
		val channelName = this.getString(R.string.default_notification_channel_name)
		val timerFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				val channel = NotificationChannel(
						channelId,
						channelName,
						NotificationManager.IMPORTANCE_HIGH
				)
				notificationMgr.createNotificationChannel(channel)
		}

		return NotificationCompat.Builder(this, channelId)
				.setSmallIcon(R.drawable.ic_record)
				.setContentTitle(getString(R.string.notification_recording_txt))
				.setContentText(timerFormat.format(elapsedSeconds * 1000))
				.setOngoing(true)
				.setContentIntent(
						PendingIntent.getActivities(
								applicationContext,
								0,
								arrayOf(
										Intent(
												applicationContext,
												RecorderXActivity::class.java
										)
								),
								0
						)
				).build()
}

fun Context.getAppPath(): String =
		File(this.getExternalFilesDir(null).toString())
				.absolutePath

fun Context.getAppPathAsFile(): File =
		File(this.getExternalFilesDir(null).toString())

fun String.formatDate(): String {
		val indexOfT = this.indexOf("T")
		val str1 = this.substring(0, indexOfT)
		val str2 = this.substring(indexOfT + 1, indexOfT + 6)
		return "created in : $str1  At  $str2"
}

fun Long.formatLength(): String {
		val minutes = TimeUnit.MILLISECONDS.toMinutes(this)
		val seconds = (
						TimeUnit.MILLISECONDS.toSeconds(this) -
										TimeUnit.MINUTES.toSeconds(minutes)
						)
		return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)
}

fun BottomSheetDialogFragment.initBottomSheetBehavior(stateChanged: (Int) -> Unit) {
		// expand the bottom sheet
		(dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
		// Set the callback to know the state of the bottom sheet
		val sheetBehavior = (this.dialog as BottomSheetDialog).behavior
		sheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
				override fun onStateChanged(bottomSheet: View, newState: Int) {
						stateChanged.invoke(newState) // only the state needed in this use case
				}

				override fun onSlide(bottomSheet: View, slideOffset: Float) {}
		})
}

//Todo: used to run foreground service
@Suppress("UNUSED")
fun Context.startForegroundService(intent: Intent?) {
		if (Util.SDK_INT >= 26) {
				this.startForegroundService(intent)
		} else {
				this.startService(intent)
		}
}