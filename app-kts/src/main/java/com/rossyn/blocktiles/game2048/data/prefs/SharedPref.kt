package com.rossyn.blocktiles.game2048.data.prefs

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * This object handles the SharedPreferences operations.
 * It provides methods to save, retrieve, and delete preferences.
 * This class demonstrates the use of SharedPreferences for storing user preferences.
 *
 */
@Singleton
class SharedPref @Inject constructor(
    private val preferences: SharedPreferences, @ApplicationContext private val context: Context
) {
    companion object {
        const val MUTE_MUSIC = "mute_music"
        const val MUTE_SOUND = "mute_sounds"
        const val TUTORIAL_PLAYED = "tutorial_played"
        const val TOP_SCORE = "Top_score"
    }

    /**
     * Registers a listener to be notified when a shared preference is changed.
     *
     * @param listener The callback that will run when a shared preference is changed.
     */
    fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        Timber.tag("SharedPref").d("registerOnSharedPref")
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    /**
     * Unregisters a previously registered listener.
     *
     * @param listener The callback to be unregistered.
     */
    fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        Timber.tag("SharedPref").d("unregisterOnSharedPref")
        preferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    var isMuteMusic by preference(MUTE_MUSIC, false)
    var isMuteSound by preference(MUTE_SOUND, false)
    var isTutorialPlayed by preference(TUTORIAL_PLAYED, false)

    /**
     * Generic preference delegate for all types (nullable and non-nullable).
     * @param name The key for the preference.
     * @param defaultValue The default value for the preference (nullable).
     */
    private inline fun <reified T> preference(name: String, defaultValue: T) =
        object : ReadWriteProperty<SharedPref, T> {

            override fun getValue(thisRef: SharedPref, property: KProperty<*>): T {
                return when (T::class) {
                    Boolean::class -> preferences.getBoolean(name, defaultValue as Boolean) as T
                    Int::class -> preferences.getInt(name, defaultValue as Int) as T
                    Long::class -> preferences.getLong(name, defaultValue as Long) as T
                    Float::class -> preferences.getFloat(name, defaultValue as Float) as T
                    String::class -> preferences.getString(name, defaultValue as String?) as T
                    else -> {
                        Timber.tag("SharedPref").d("Type not supported: ${T::class.java}")
                        defaultValue // Return default value if type is unsupported
                    }
                }
            }

            override fun setValue(thisRef: SharedPref, property: KProperty<*>, value: T) {
                CoroutineScope(Dispatchers.IO).launch {
                    preferences.edit().apply {
                        when (value) {
                            is Boolean -> putBoolean(name, value)
                            is Int -> putInt(name, value)
                            is Long -> putLong(name, value)
                            is Float -> putFloat(name, value)
                            is String? -> putString(name, value)
                            else -> {
                                Timber.tag("SharedPref").d("Type not supported: ${T::class.java}")
                                // Do nothing if type is unsupported
                            }
                        }
                    }.apply()
                }
            }
        }
}