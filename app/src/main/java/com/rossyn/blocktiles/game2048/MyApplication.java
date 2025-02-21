/*
 * Copyright (c) 2024 RohitrajKhorwal
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

package com.rossyn.blocktiles.game2048;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDexApplication;

import com.gu.toolargetool.TooLargeTool;

import timber.log.Timber;


/**
 * 2048
 * <p>
 * Project Created on: 27 JAN 2025
 *
 * @author Rohitraj Khorwal
 * @see <a href="mailto:banrossyn@gmail.com">banrossyn@gmail.com</a>
 * @since v1.0.0
 */
public class MyApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private static final boolean isDebug = BuildConfig.DEBUG;
    private static MyApplication instance;


    public static Context getContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        this.registerActivityLifecycleCallbacks(this);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        if (isDebug) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectUnsafeIntentLaunch().build());
            }
            TooLargeTool.startLogging(this);

            Timber.plant();
        }

        Timber.d("onCreate: ");
    }

    // only in emulator
    @Override
    public void onTerminate() {
        Timber.d("onTerminate: ");
        super.onTerminate();
    }


    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if (isDebug) TooLargeTool.stopLogging(this);
        this.unregisterActivityLifecycleCallbacks(this);
//        stopService(new Intent(this, MusicService.class));
        DefaultLifecycleObserver.super.onDestroy(owner);

    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }


}
