package com.diogo.replog.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.replogDataStore: DataStore<Preferences> by preferencesDataStore(name = "replog_preferences")

/**
 * DataStore wrapper for persisting user onboarding preferences.
 */
object OnboardingDataStore {

    private val KEY_ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
    private val KEY_UNIT = stringPreferencesKey("unit") // "kg" or "lbs"
    private val KEY_GENDER = stringPreferencesKey("gender") // "male" or "female"
    private val KEY_WEIGHT = floatPreferencesKey("weight_kg")
    private val KEY_GOAL = stringPreferencesKey("goal")
    private val KEY_EXPERIENCE = stringPreferencesKey("experience")
    private val KEY_HEIGHT = androidx.datastore.preferences.core.intPreferencesKey("height_cm")
    private val KEY_FREQUENCY = stringPreferencesKey("weekly_frequency")

    fun isOnboardingDone(context: Context): Flow<Boolean> =
        context.replogDataStore.data.map { prefs -> prefs[KEY_ONBOARDING_DONE] ?: false }

    suspend fun completeOnboarding(
        context: Context,
        unit: String,
        gender: String,
        weightKg: Float,
        goal: String,
        experience: String,
        heightCm: Int,
        weeklyFrequency: String
    ) {
        context.replogDataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_DONE] = true
            prefs[KEY_UNIT] = unit
            prefs[KEY_GENDER] = gender
            prefs[KEY_WEIGHT] = weightKg
            prefs[KEY_GOAL] = goal
            prefs[KEY_EXPERIENCE] = experience
            prefs[KEY_HEIGHT] = heightCm
            prefs[KEY_FREQUENCY] = weeklyFrequency
        }
    }

    suspend fun resetOnboarding(context: Context) {
        context.replogDataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_DONE] = false
        }
    }

    /** Read a single snapshot to check if onboarding was completed (blocking). */
    suspend fun isOnboardingDoneSnapshot(context: Context): Boolean =
        context.replogDataStore.data.map { it[KEY_ONBOARDING_DONE] ?: false }.first()
}
