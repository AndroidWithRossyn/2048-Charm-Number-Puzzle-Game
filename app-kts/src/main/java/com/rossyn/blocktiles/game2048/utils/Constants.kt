package com.rossyn.blocktiles.game2048.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Constants @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        /** The name of the SharedPreferences file. */
        const val SHARED_PREFERENCE_FILE_NAME = "2048_PREFERENCE"


    }

}