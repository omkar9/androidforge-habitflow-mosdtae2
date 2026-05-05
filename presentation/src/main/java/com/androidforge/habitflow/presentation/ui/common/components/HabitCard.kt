package com.androidforge.habitflow.presentation.ui.common.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidforge.habitflow.R
import com.androidforge.habitflow.domain.model.Habit
import com.androidforge.habitflow.domain.model.HabitCompletion
import com.androidforge.habitflow.domain.model.HabitStatus
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * A reusable Composable component for displaying individual habit information in lists.
 * It shows the habit's name, description, current streak, and allows marking completion.
 *
 * @param habit The [Habit] data to display.
 * @param onToggleCompletion Lambda to be invoked when the completion status for today is toggled.
 * @param onClick Lambda to be invoked when the card itself is clicked, navigating to habit details.
 * @param modifier The modifier to be applied to the card.
 */
@Composable
fun HabitCard(
    habit: Habit,
    onToggleCompletion: (Habit, LocalDate) -> Unit,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "Habit Card Scale Animation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp) // Fixed height for consistent list items
            .scale(scale)
            .indication(interactionSource, rememberRipple())
            .clickable(onClick = { onClick(habit.id) }, interactionSource = interactionSource, indication = null),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Section: Habit Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = habit.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = stringResource(R.string.current_streak_icon_content_description),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.current_streak_value, habit.currentStreak),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Right Section: Completion Toggle
            Box(
                modifier = Modifier
                    .width(80.dp) // Fixed width for the toggle button area
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false, radius = 40.dp),
                        onClick = { onToggleCompletion(habit, LocalDate.now()) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                val toggleBackgroundColor by animateColorAsState(
                    targetValue = when (habit.completedToday) {
                        HabitStatus.COMPLETED -> MaterialTheme.colorScheme.success
                        HabitStatus.MISSED -> MaterialTheme.colorScheme.error
                        HabitStatus.SKIPPED -> MaterialTheme.colorScheme.warning
                        HabitStatus.NONE -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "Toggle Background Color"
                )

                val toggleIconColor by animateColorAsState(
                    targetValue = when (habit.completedToday) {
                        HabitStatus.COMPLETED, HabitStatus.MISSED, HabitStatus.SKIPPED -> MaterialTheme.colorScheme.onPrimary
                        HabitStatus.NONE -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "Toggle Icon Color"
                )

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(toggleBackgroundColor)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape), // Subtle border
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (habit.completedToday) {
                            HabitStatus.COMPLETED -> Icons.Filled.Check
                            HabitStatus.MISSED -> Icons.Filled.Close
                            HabitStatus.SKIPPED -> Icons.Filled.Remove
                            HabitStatus.NONE -> Icons.Filled.Info // A neutral icon for 'not done'
                        },
                        contentDescription = stringResource(R.string.toggle_habit_completion_content_description, habit.name, habit.completedToday.name),
                        tint = toggleIconColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHabitCardCompleted() {
    HabitFlowTheme {
        HabitCard(
            habit = Habit(
                id = 1,
                name = "Drink Water",
                description = "Drink 8 glasses of water daily for better health.",
                frequency = setOf(DayOfWeek.MONDAY),
                reminderTime = LocalTime.NOON,
                createdAt = LocalDate.now(),
                lastModified = LocalDate.now(),
                isArchived = false,
                currentStreak = 5,
                longestStreak = 7,
                completedToday = HabitStatus.COMPLETED,
                completions = emptyList()
            ),
            onToggleCompletion = { _, _ -> },
            onClick = { _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHabitCardMissed() {
    HabitFlowTheme {
        HabitCard(
            habit = Habit(
                id = 2,
                name = "Workout",
                description = "30 minutes of high-intensity workout. Don't skip leg day!",
                frequency = setOf(DayOfWeek.MONDAY),
                reminderTime = LocalTime.NOON,
                createdAt = LocalDate.now(),
                lastModified = LocalDate.now(),
                isArchived = false,
                currentStreak = 0,
                longestStreak = 3,
                completedToday = HabitStatus.MISSED,
                completions = emptyList()
            ),
            onToggleCompletion = { _, _ -> },
            onClick = { _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHabitCardSkipped() {
    HabitFlowTheme {
        HabitCard(
            habit = Habit(
                id = 3,
                name = "Meditate",
                description = "10 minutes of mindfulness and breathing exercises.",
                frequency = setOf(DayOfWeek.MONDAY),
                reminderTime = LocalTime.NOON,
                createdAt = LocalDate.now(),
                lastModified = LocalDate.now(),
                isArchived = false,
                currentStreak = 2,
                longestStreak = 2,
                completedToday = HabitStatus.SKIPPED,
                completions = emptyList()
            ),
            onToggleCompletion = { _, _ -> },
            onClick = { _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHabitCardNone() {
    HabitFlowTheme {
        HabitCard(
            habit = Habit(
                id = 4,
                name = "Read Book",
                description = "Read 20 pages of a non-fiction book to learn something new.",
                frequency = setOf(DayOfWeek.MONDAY),
                reminderTime = LocalTime.NOON,
                createdAt = LocalDate.now(),
                lastModified = LocalDate.now(),
                isArchived = false,
                currentStreak = 0,
                longestStreak = 0,
                completedToday = HabitStatus.NONE,
                completions = emptyList()
            ),
            onToggleCompletion = { _, _ -> },
            onClick = { _ -> }
        )
    }
}