package com.rossyn.blocktiles.game2048.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.rossyn.blocktiles.game2048.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

/**
 * Dagger module that provides SharedPreferences-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object SharedPreferencesModule {

    /**
     * Provides a MasterKey for EncryptedSharedPreferences using AES256_GCM encryption.
     *
     * @param context The application context.
     * @return The MasterKey instance.
     */
    @Provides
    @Singleton
    fun provideMasterKey(@ApplicationContext context: Context): MasterKey {
        return MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    }

    /**
     * Provides an instance of SharedPreferences, using EncryptedSharedPreferences if possible.
     *
     * @param context The application context.
     * @param masterKey The MasterKey instance for encryption.
     * @return The SharedPreferences instance.
     */
    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context, masterKey: MasterKey
    ): SharedPreferences {
        return try {
            EncryptedSharedPreferences.create(
                context,
                Constants.SHARED_PREFERENCE_FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Timber.e("Exception: ${e.message}")
            context.getSharedPreferences(
                Constants.SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE
            )
        }
    }

    /**
     * Provides an instance of SharedPref.
     *
     * @param preferences The SharedPreferences instance.
     * @param context The application context.
     * @return The SharedPref instance.
     */
    @Provides
    @Singleton
    fun provideSharedPref(
        preferences: SharedPreferences, @ApplicationContext context: Context
    ): SharedPref {
        return SharedPref(preferences, context)
    }
}