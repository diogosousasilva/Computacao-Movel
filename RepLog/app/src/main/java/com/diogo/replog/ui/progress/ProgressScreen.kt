package com.diogo.replog.ui.progress

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.diogo.replog.R
import com.diogo.replog.ui.components.*
import com.diogo.replog.ui.theme.*
import com.diogo.replog.viewmodel.ExerciseProgress
import com.diogo.replog.viewmodel.ProgressViewModel
import java.util.Locale

@Composable
fun ProgressScreen(
    progressViewModel: ProgressViewModel = viewModel(),
    paddingValues: PaddingValues = PaddingValues(),
) {
    val state by progressViewModel.state.collectAsStateWithLifecycle()

    // Only trigger a load if the cache is empty (first visit or after cache invalidation).
    // If state already has data, render it instantly without re-fetching.
    LaunchedEffect(Unit) {
        if (state.exerciseProgressList.isEmpty()) {
            progressViewModel.loadProgress()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> LoadingIndicator()
            state.error != null -> ErrorDisplay(
                message = state.error!!,
                onRetry = { progressViewModel.loadProgress() }
            )
            state.exerciseProgressList.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.no_progress_yet),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.complete_workouts_for_progress),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp + paddingValues.calculateBottomPadding()
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.your_progress),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(state.exerciseProgressList) { progress ->
                        ExerciseProgressCard(
                            progress = progress,
                            suggestion = progressViewModel.getProgressionSuggestion(progress)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseProgressCard(
    progress: ExerciseProgress,
    suggestion: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        onClick = { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier.animateContentSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = progress.exercise.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (progress.isPlateaued) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(containerColor = ErrorRed) {
                                Text("⚠️", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                    Text(
                        text = progress.exercise.muscleGroup.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Blue40
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${String.format(Locale.getDefault(), "%.1f", progress.currentMax)} kg",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    val deltaColor = when {
                        progress.deltaPercent > 0 -> Green40
                        progress.deltaPercent < 0 -> ErrorRed
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    val deltaPrefix = if (progress.deltaPercent > 0) "+" else ""
                    Text(
                        text = "$deltaPrefix${String.format(Locale.getDefault(), "%.1f", progress.deltaPercent)}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = deltaColor
                    )
                }
            }

            // Mini sparkline chart
            if (progress.history.size >= 2) {
                Spacer(modifier = Modifier.height(12.dp))
                MiniChart(
                    data = progress.history.map { it.second },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                )
            }

            // Expanded suggestion
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = suggestion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MiniChart(
    data: List<Double>,
    modifier: Modifier = Modifier,
    lineColor: Color = Blue40
) {
    val chartColor = lineColor
    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas
        val maxVal = data.max()
        val minVal = data.min()
        val range = (maxVal - minVal).coerceAtLeast(1.0)

        val stepX = size.width / (data.size - 1)
        val padding = 4.dp.toPx()

        val path = Path()
        data.forEachIndexed { index, value ->
            val x = index * stepX
            val y = padding + (size.height - 2 * padding) * (1.0 - (value - minVal) / range).toFloat()

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }

            // Draw point
            drawCircle(
                color = chartColor,
                radius = 3.dp.toPx(),
                center = Offset(x, y)
            )
        }

        drawPath(
            path = path,
            color = chartColor,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
