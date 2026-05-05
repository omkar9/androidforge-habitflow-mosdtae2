package com.androidforge.habitflow.presentation.ui.common.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidforge.habitflow.presentation.ui.theme.HabitFlowTheme

/**
 * Applies a shimmer loading effect to a Composable.
 * This modifier creates a visual indication that content is being loaded asynchronously.
 *
 * @param durationMillis The duration of one shimmer animation cycle in milliseconds.
 * @param delayMillis The delay before the shimmer animation starts.
 * @param modifier The modifier to be applied to the shimmer effect.
 */
fun Modifier.shimmer(durationMillis: Int = 1500, delayMillis: Int = 200): Modifier = composed {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "ShimmerInfiniteTransition")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, delayMillis = delayMillis),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ShimmerTranslateAnimation"
    )

    this.background(Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnimation - 500f, y = translateAnimation - 500f),
        end = Offset(x = translateAnimation, y = translateAnimation)
    ))
}

/**
 * A generic Composable for displaying a loading shimmer effect.
 * It's designed to mimic the shape of a [HabitCard] for a consistent loading experience.
 *
 * @param modifier The modifier to be applied to the loading shimmer container.
 */
@Composable
fun LoadingShimmer(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium) // Match HabitCard shape
            .shimmer()
    ) {
        // Inner layout to provide structure resembling a habit card
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.1f))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.08f))
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoadingShimmer() {
    HabitFlowTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LoadingShimmer(modifier = Modifier.fillMaxWidth().height(120.dp))
            LoadingShimmer(modifier = Modifier.fillMaxWidth().height(120.dp))
            LoadingShimmer(modifier = Modifier.fillMaxWidth().height(120.dp))
        }
    }
}