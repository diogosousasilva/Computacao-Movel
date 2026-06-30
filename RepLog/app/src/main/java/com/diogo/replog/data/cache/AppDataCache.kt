package com.diogo.replog.data.cache

import com.diogo.replog.data.model.Workout
import com.diogo.replog.data.repository.ExerciseRepository
import com.diogo.replog.data.repository.WorkoutRepository
import com.diogo.replog.viewmodel.ExerciseProgress
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Singleton in-memory cache for all heavy Firestore data.
 *
 * Pre-fetches and pre-computes Home stats and Progress data in background
 * as soon as the user is authenticated. ViewModels read from this cache
 * and display data instantly without recalculating on the UI thread.
 *
 * Cache is invalidated and re-fetched automatically after a workout is saved.
 */
object AppDataCache {

    // -----------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------
    private val workoutRepository = WorkoutRepository()
    private val exerciseRepository = ExerciseRepository()

    // Dedicated background scope — survives ViewModel recreation
    private val cacheScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var prefetchJob: Job? = null

    // -----------------------------------------------------------------------
    // Home cache
    // -----------------------------------------------------------------------
    data class HomeCache(
        val recentWorkouts: List<Workout> = emptyList(),
        val totalWorkoutCount: Int = 0,
        val todayWorkoutCount: Int = 0,
        val isReady: Boolean = false,
    )

    private val _homeCache = MutableStateFlow(HomeCache())
    val homeCache: StateFlow<HomeCache> = _homeCache.asStateFlow()

    // -----------------------------------------------------------------------
    // Progress cache
    // -----------------------------------------------------------------------
    data class ProgressCache(
        val exerciseProgressList: List<ExerciseProgress> = emptyList(),
        val isReady: Boolean = false,
    )

    private val _progressCache = MutableStateFlow(ProgressCache())
    val progressCache: StateFlow<ProgressCache> = _progressCache.asStateFlow()

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Start pre-fetching all data in background.
     * Safe to call multiple times — cancels the previous job first.
     * Should be called as soon as Firebase auth is confirmed.
     */
    fun prefetchAll() {
        prefetchJob?.cancel()
        prefetchJob = cacheScope.launch {
            // Run both fetches in parallel
            val homeJob = launch { fetchHomeData() }
            val progressJob = launch { fetchProgressData() }
            homeJob.join()
            progressJob.join()
        }
    }

    /**
     * Invalidate cache and re-fetch everything.
     * Call this immediately after saving a new workout.
     */
    fun invalidateAndRefetch() {
        _homeCache.value = HomeCache(isReady = false)
        _progressCache.value = ProgressCache(isReady = false)
        prefetchAll()
    }

    // -----------------------------------------------------------------------
    // Private fetch logic
    // -----------------------------------------------------------------------

    private suspend fun fetchHomeData() {
        try {
            // All Firestore I/O — runs on Default, Firestore SDK uses its own thread pool internally
            val recentWorkouts = workoutRepository
                .getRecentWorkouts(5).getOrDefault(emptyList())
            val totalCount = workoutRepository
                .getRecentWorkouts(1000).getOrDefault(emptyList()).size
            val todayCount = workoutRepository.getTodayWorkoutCount()

            _homeCache.value = HomeCache(
                recentWorkouts = recentWorkouts,
                totalWorkoutCount = totalCount,
                todayWorkoutCount = todayCount,
                isReady = true
            )
        } catch (_: Exception) {
            // Leave cache not ready — ViewModel will show spinner
        }
    }

    private suspend fun fetchProgressData() {
        try {
            val exercises = exerciseRepository.getAllExercises().getOrDefault(emptyList())

            val progressList = withContext(Dispatchers.Default) {
                // Fetch all workouts and their sub-collection exercises in parallel (extremely fast!)
                val workoutsWithExercises = workoutRepository
                    .getAllWorkoutsWithExercises(limit = 100)
                    .getOrDefault(emptyList())

                // Build a map of exerciseId -> List<Pair<Timestamp, Double>> (history) in memory
                val historyMap = mutableMapOf<String, MutableList<Pair<Timestamp, Double>>>()
                // Iterate chronologically (workouts retrieved in descending order, so reverse to process oldest to newest)
                for ((workout, wExercises) in workoutsWithExercises.reversed()) {
                    for (ex in wExercises) {
                        val maxWeight = ex.sets.maxOfOrNull { it.weightKg } ?: 0.0
                        if (maxWeight > 0) {
                            historyMap.getOrPut(ex.exerciseId) { mutableListOf() }
                                .add(workout.date to maxWeight)
                        }
                    }
                }

                val result = mutableListOf<ExerciseProgress>()
                for (exercise in exercises) {
                    val history = historyMap[exercise.id] ?: emptyList()
                    if (history.isNotEmpty()) {
                        val currentMax = history.lastOrNull()?.second ?: 0.0
                        val previousMax = if (history.size >= 2) history[history.size - 2].second else currentMax
                        val delta = if (previousMax > 0) ((currentMax - previousMax) / previousMax) * 100 else 0.0
                        val twoWeeksAgo = Timestamp(Timestamp.now().seconds - (14 * 24 * 60 * 60), 0)
                        val recentEntries = history.filter { it.first >= twoWeeksAgo }
                        val isPlateaued = (recentEntries.size >= 2) &&
                                recentEntries.all { kotlin.math.abs(it.second - recentEntries.first().second) < 0.5 }
                        result.add(
                            ExerciseProgress(
                                exercise = exercise,
                                history = history,
                                currentMax = currentMax,
                                previousMax = previousMax,
                                deltaPercent = delta,
                                isPlateaued = isPlateaued
                            )
                        )
                    }
                }
                result.sortedByDescending { it.history.size }
            }

            _progressCache.value = ProgressCache(
                exerciseProgressList = progressList,
                isReady = true
            )
        } catch (_: Exception) {
            // Leave cache not ready
        }
    }
}
