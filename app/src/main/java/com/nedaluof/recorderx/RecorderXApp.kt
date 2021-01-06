package com.nedaluof.recorderx

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
 * Created by nedaluof on 10/28/2020.
 */
@HiltAndroidApp
class RecorderXApp : Application(){
		override fun onCreate() {
				super.onCreate()
				if (BuildConfig.DEBUG){
						Timber.plant(DebugTree())
				}
		}
}
