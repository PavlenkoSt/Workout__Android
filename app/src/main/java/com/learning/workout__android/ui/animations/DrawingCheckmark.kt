import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import kotlin.math.PI

@Composable
fun DrawingCheckmark(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    size: Int = 64,
    circleColor: Color = Color.White,
    checkColor: Color = Color.White,
    backgroundColor: Color = Color(0xFF14B8A6) // Teal color
) {
    // Animate circle progress (0f to 1f)
    val circleProgress by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "circle"
    )

    // Animate checkmark progress with delay
    val checkProgress by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 300),
        label = "check"
    )

    Canvas(modifier = modifier.size(size.dp)) {
        val canvasSize = this.size.minDimension
        val center = Offset(canvasSize / 2f, canvasSize / 2f)
        val radius = canvasSize / 2f - 2.dp.toPx()

        // Draw background circle
        drawCircle(
            color = backgroundColor,
            radius = radius,
            center = center
        )

        // Draw animated circle stroke
        val circleLength = 2f * PI.toFloat() * radius
        drawCircle(
            color = circleColor,
            radius = radius,
            center = center,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(circleLength, circleLength),
                    phase = circleLength * (1f - circleProgress)
                )
            )
        )

        // Draw animated checkmark
        // Calculate checkmark points
        val checkStartX = canvasSize * 0.27f
        val checkStartY = canvasSize * 0.52f
        val checkMiddleX = canvasSize * 0.44f
        val checkMiddleY = canvasSize * 0.69f
        val checkEndX = canvasSize * 0.73f
        val checkEndY = canvasSize * 0.35f

        // First stroke of checkmark (short one)
        val stroke1Length = kotlin.math.sqrt(
            (checkMiddleX - checkStartX) * (checkMiddleX - checkStartX) +
                    (checkMiddleY - checkStartY) * (checkMiddleY - checkStartY)
        )

        // Second stroke of checkmark (long one)
        val stroke2Length = kotlin.math.sqrt(
            (checkEndX - checkMiddleX) * (checkEndX - checkMiddleX) +
                    (checkEndY - checkMiddleY) * (checkEndY - checkMiddleY)
        )

        val totalLength = stroke1Length + stroke2Length

        // Draw first part of check
        if (checkProgress > 0f) {
            val progress1 = (checkProgress * totalLength).coerceAtMost(stroke1Length)
            val ratio1 = progress1 / stroke1Length

            drawLine(
                color = checkColor,
                start = Offset(checkStartX, checkStartY),
                end = Offset(
                    checkStartX + (checkMiddleX - checkStartX) * ratio1,
                    checkStartY + (checkMiddleY - checkStartY) * ratio1
                ),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Draw second part of check
        if (checkProgress * totalLength > stroke1Length) {
            val progress2 =
                (checkProgress * totalLength - stroke1Length).coerceAtMost(stroke2Length)
            val ratio2 = progress2 / stroke2Length

            drawLine(
                color = checkColor,
                start = Offset(checkMiddleX, checkMiddleY),
                end = Offset(
                    checkMiddleX + (checkEndX - checkMiddleX) * ratio2,
                    checkMiddleY + (checkEndY - checkMiddleY) * ratio2
                ),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Preview
@Composable
fun StrokeDrawingCheckmarkPreview() {
    Workout__AndroidTheme() {
        DrawingCheckmark(
            isVisible = true,
            size = 120
        )
    }
}