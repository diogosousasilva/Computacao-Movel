package com.diogo.replog.data.model

/**
 * Represents an exercise in the library (e.g., Bench Press, Squat).
 * Shared exercises stored at: exercises/{exerciseId}
 * Custom exercises stored at: users/{uid}/custom_exercises/{exerciseId}
 */
data class Exercise(
    val id: String = "",
    val name: String = "",
    val muscleGroup: MuscleGroup = MuscleGroup.OTHER,
    val description: String = "",
    val isCustom: Boolean = false,
    val createdBy: String? = null      // uid of creator for custom exercises
) {
    constructor() : this(id = "")
}

/**
 * Muscle group categories for exercises.
 */
enum class MuscleGroup(val displayName: String) {
    CHEST("Chest"),
    BACK("Back"),
    SHOULDERS("Shoulders"),
    BICEPS("Biceps"),
    TRICEPS("Triceps"),
    LEGS("Legs"),
    CORE("Core"),
    CARDIO("Cardio"),
    OTHER("Other");

    companion object {
        fun fromString(value: String): MuscleGroup {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: OTHER
        }
    }
}

/**
 * Default exercise library that gets seeded into Firestore.
 */
object DefaultExercises {
    val exercises = listOf(
        // CHEST
        Exercise(id = "1", name = "Barbell Bench Press", muscleGroup = MuscleGroup.CHEST, description = ""),
        Exercise(id = "2", name = "Incline Dumbbell Bench Press", muscleGroup = MuscleGroup.CHEST, description = ""),
        Exercise(id = "3", name = "Decline Barbell Press", muscleGroup = MuscleGroup.CHEST, description = ""),
        Exercise(id = "4", name = "Dumbbell Chest Fly", muscleGroup = MuscleGroup.CHEST, description = ""),
        Exercise(id = "5", name = "Cable Crossover", muscleGroup = MuscleGroup.CHEST, description = ""),
        Exercise(id = "6", name = "Chest Press Machine", muscleGroup = MuscleGroup.CHEST, description = ""),
        Exercise(id = "7", name = "Parallel Bar Dips", muscleGroup = MuscleGroup.CHEST, description = ""),
        Exercise(id = "8", name = "Push-Ups", muscleGroup = MuscleGroup.CHEST, description = ""),
        Exercise(id = "9", name = "Incline Cable Fly", muscleGroup = MuscleGroup.CHEST, description = ""),
        Exercise(id = "10", name = "Pec Deck Fly", muscleGroup = MuscleGroup.CHEST, description = ""),

        // BACK
        Exercise(id = "11", name = "Barbell Deadlift", muscleGroup = MuscleGroup.BACK, description = ""),
        Exercise(id = "12", name = "Wide-Grip Lat Pulldown", muscleGroup = MuscleGroup.BACK, description = ""),
        Exercise(id = "13", name = "Pull-Ups", muscleGroup = MuscleGroup.BACK, description = ""),
        Exercise(id = "14", name = "Bent-Over Barbell Row", muscleGroup = MuscleGroup.BACK, description = ""),
        Exercise(id = "15", name = "Seated Cable Row", muscleGroup = MuscleGroup.BACK, description = ""),
        Exercise(id = "16", name = "One-Arm Dumbbell Row", muscleGroup = MuscleGroup.BACK, description = ""),
        Exercise(id = "17", name = "T-Bar Row", muscleGroup = MuscleGroup.BACK, description = ""),
        Exercise(id = "18", name = "Chin-Ups", muscleGroup = MuscleGroup.BACK, description = ""),
        Exercise(id = "19", name = "Hyperextensions", muscleGroup = MuscleGroup.BACK, description = ""),
        Exercise(id = "20", name = "Straight-Arm Cable Pulldown", muscleGroup = MuscleGroup.BACK, description = ""),

        // LEGS
        Exercise(id = "21", name = "Barbell Back Squat", muscleGroup = MuscleGroup.LEGS, description = ""),
        Exercise(id = "22", name = "Leg Press 45°", muscleGroup = MuscleGroup.LEGS, description = ""),
        Exercise(id = "23", name = "Romanian Deadlift (RDL)", muscleGroup = MuscleGroup.LEGS, description = ""),
        Exercise(id = "24", name = "Leg Extensions", muscleGroup = MuscleGroup.LEGS, description = ""),
        Exercise(id = "25", name = "Seated Leg Curl", muscleGroup = MuscleGroup.LEGS, description = ""),
        Exercise(id = "26", name = "Lying Leg Curl", muscleGroup = MuscleGroup.LEGS, description = ""),
        Exercise(id = "27", name = "Dumbbell Lunges", muscleGroup = MuscleGroup.LEGS, description = ""),
        Exercise(id = "28", name = "Bulgarian Split Squat", muscleGroup = MuscleGroup.LEGS, description = ""),
        Exercise(id = "29", name = "Standing Calf Raises", muscleGroup = MuscleGroup.LEGS, description = ""),
        Exercise(id = "30", name = "Seated Calf Raises", muscleGroup = MuscleGroup.LEGS, description = ""),
        Exercise(id = "31", name = "Hack Squat", muscleGroup = MuscleGroup.LEGS, description = ""),

        // SHOULDERS
        Exercise(id = "32", name = "Overhead Barbell Press", muscleGroup = MuscleGroup.SHOULDERS, description = ""),
        Exercise(id = "33", name = "Dumbbell Shoulder Press", muscleGroup = MuscleGroup.SHOULDERS, description = ""),
        Exercise(id = "34", name = "Dumbbell Lateral Raise", muscleGroup = MuscleGroup.SHOULDERS, description = ""),
        Exercise(id = "35", name = "Cable Lateral Raise", muscleGroup = MuscleGroup.SHOULDERS, description = ""),
        Exercise(id = "36", name = "Dumbbell Front Raise", muscleGroup = MuscleGroup.SHOULDERS, description = ""),
        Exercise(id = "37", name = "Rear Delt Dumbbell Fly", muscleGroup = MuscleGroup.SHOULDERS, description = ""),
        Exercise(id = "38", name = "Face Pulls", muscleGroup = MuscleGroup.SHOULDERS, description = ""),
        Exercise(id = "39", name = "Barbell Shrugs", muscleGroup = MuscleGroup.SHOULDERS, description = ""),
        Exercise(id = "40", name = "Arnold Press", muscleGroup = MuscleGroup.SHOULDERS, description = "")
    )
}
