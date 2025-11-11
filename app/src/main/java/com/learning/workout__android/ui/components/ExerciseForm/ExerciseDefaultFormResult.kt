import com.learning.workout__android.data.models.ExerciseType

data class ExerciseDefaultFormResult(
    val name: String,
    val type: ExerciseType,
    val reps: Number,
    val sets: Number,
    val rest: Number
)