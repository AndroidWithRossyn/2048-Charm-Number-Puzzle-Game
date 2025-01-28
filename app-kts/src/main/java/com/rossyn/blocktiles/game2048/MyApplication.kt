/*
 * Copyright (c) 2025 RohitrajKhorwal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.rossyn.blocktiles.game2048

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.google.android.material.color.DynamicColors
import com.gu.toolargetool.TooLargeTool
import com.rossyn.blocktiles.game2048.services.MusicService
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * 2048
 *
 * Project Created on: 27 JAN 2025
 * @author Rohitraj Khorwal
 * @since v1.0.0
 * @see <a href="mailto:banrossyn@gmail.com">banrossyn@gmail.com</a>
 */
@HiltAndroidApp
class MyApplication : MultiDexApplication(), Application.ActivityLifecycleCallbacks,
    DefaultLifecycleObserver {

    companion object {
        val isDebug: Boolean by lazy { BuildConfig.DEBUG }
    }

    override fun onCreate() {
        super<MultiDexApplication>.onCreate()

        this.registerActivityLifecycleCallbacks(this)

//        DynamicColors.applyToActivitiesIfAvailable(this)
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        if (isDebug) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                StrictMode.setVmPolicy(
                    StrictMode.VmPolicy.Builder().detectUnsafeIntentLaunch().build()
                )
            }
            TooLargeTool.startLogging(this)

            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return "(${element.fileName}:${element.lineNumber})"
                }

                override fun log(
                    priority: Int, tag: String?, message: String, t: Throwable?
                ) {
                    val enhancedMessage = "[2048]--->  $message"
                    super.log(priority, tag, enhancedMessage, t)
                }
            })

        }

        Timber.d("onCreate: ")
    }

    // only in emulator
    override fun onTerminate() {
        Timber.d("onTerminate: ")
        super.onTerminate()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (isDebug) TooLargeTool.stopLogging(this)
        this.unregisterActivityLifecycleCallbacks(this)
        stopService(Intent(this, MusicService::class.java))
        super.onDestroy(owner)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

}