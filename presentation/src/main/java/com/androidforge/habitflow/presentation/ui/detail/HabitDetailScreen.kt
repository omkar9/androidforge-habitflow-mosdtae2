package com.androidforge.habitflow.presentation.ui.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidforge.habitflow.R
import com.androidforge.habitflow.domain.model.Habit
import com.androidforge.habitflow.domain.model.HabitCompletion
import com.androidforge.habitflow.domain.model.HabitStatus
import com.androidforge.habitflow.presentation.ui.common.components.LoadingShimmer
import com.androidforge.habitflow.presentation.ui.common.util.DateUtils
import com.androidforge.habitflow.presentation.ui.habits.ButtonWithPressAnimation
import com.androidforge.habitflow.presentation.ui.theme.HabitFlowTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HabitDetailScreen(
    viewModel: HabitDetailViewModel = hiltViewModel(),
    onBackPress: () -> Unit,
    onNavigateToEditHabit: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* Title handled by state */ },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_content_description),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    if (uiState is HabitDetailUiState.Success) {
                        val habitId = (uiState as HabitDetailUiState.Success).habit.id
                        IconButton(
                            onClick = { onNavigateToEditHabit(habitId) },
                            modifier = Modifier.scale(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.edit_habit_content_description),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) {\ paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Crossfade(targetState = uiState, label = "HabitDetailUiState Transition") {\ state ->
                when (state) {
                    is HabitDetailUiState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            LoadingShimmer(modifier = Modifier.fillMaxWidth().height(80.dp))
                            LoadingShimmer(modifier = Modifier.fillMaxWidth().height(60.dp))
                            Spacer(Modifier.height(16.dp))
                            LoadingShimmer(modifier = Modifier.fillMaxWidth().height(250.dp))
                        }
                    }
                    is HabitDetailUiState.Success -> {
                        val habit = state.habit
                        val currentMonth = state.currentMonth
                        val completionHistory = state.completionHistory
                        val daysInMonth = currentMonth.lengthOfMonth()
                        val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7 // 0 for Sunday, 1 for Monday...

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                                .animateContentSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = habit.name,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = habit.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(0.9f)
                            )
                            Spacer(Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatCard(
                                    icon = Icons.Filled.LocalFireDepartment,
                                    label = stringResource(R.string.current_streak_label),
                                    value = habit.currentStreak.toString(),
                                    contentDescription = stringResource(R.string.current_streak_content_description, habit.currentStreak)
                                )
                                StatCard(
                                    icon = Icons.Filled.LocalFireDepartment,
                                    label = stringResource(R.string.longest_streak_label),
                                    value = habit.longestStreak.toString(),
                                    contentDescription = stringResource(R.string.longest_streak_content_description, habit.longestStreak)
                                )
                            }
                            Spacer(Modifier.height(24.dp))

                            // Calendar View
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(onClick = viewModel::onPreviousMonthClick) {
                                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.previous_month_content_description))
                                        }
                                        Text(
                                            text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        IconButton(onClick = viewModel::onNextMonthClick) {
                                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.next_month_content_description), modifier = Modifier.scale(-1f))
                                        }
                                    }
                                    Spacer(Modifier.height(16.dp))

                                    // Day of week headers
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        DayOfWeek.values().forEach { dayOfWeek ->
                                            Text(
                                                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(8.dp))

                                    // Days grid
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(7),
                                        modifier = Modifier.fillMaxWidth().height( ( (daysInMonth + firstDayOfMonth + 6) / 7 * 48 ).dp ), // Dynamic height
                                        userScrollEnabled = false
                                    ) {
                                        // Empty cells for days before the 1st of the month
                                        items(firstDayOfMonth) { Spacer(Modifier.aspectRatio(1f)) }

                                        items(1..daysInMonth) { dayOfMonth ->
                                            val date = currentMonth.atDay(dayOfMonth)
                                            val completionStatus = completionHistory[date] ?: HabitStatus.NONE
                                            CalendarDayCell(
                                                date = date,
                                                status = completionStatus,
                                                isToday = date == LocalDate.now(),
                                                onToggle = { viewModel.onHabitCompletionToggled(habit.id, date) }
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                    is HabitDetailUiState.Empty -> {
                        EmptyState(
                            message = state.message,
                            illustrationResId = state.illustrationResId,
                            onBackPress = onBackPress
                        )
                    }
                    is HabitDetailUiState.Error -> {
                        ErrorState(
                            message = state.message,
                            onRetry = state.onRetry,
                            illustrationResId = R.drawable.ic_error_state // Placeholder
                        )
                    }
                    is HabitDetailUiState.Offline -> {
                        OfflineState(
                            onRetry = state.onRetry,
                            illustrationResId = R.drawable.ic_offline_state // Placeholder
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.width(150.dp).height(90.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarDayCell(
    date: LocalDate,
    status: HabitStatus,
    isToday: Boolean,
    onToggle: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
    )

    val backgroundColor = when (status) {
        HabitStatus.COMPLETED -> MaterialTheme.colorScheme.success.copy(alpha = 0.8f)
        HabitStatus.SKIPPED -> MaterialTheme.colorScheme.warning.copy(alpha = 0.8f)
        HabitStatus.MISSED -> MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
        HabitStatus.NONE -> MaterialTheme.colorScheme.surface
    }

    val contentColor = when (status) {
        HabitStatus.COMPLETED, HabitStatus.SKIPPED, HabitStatus.MISSED -> MaterialTheme.colorScheme.onPrimary
        HabitStatus.NONE -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .scale(scale)
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .border(
                width = if (isToday) 2.dp else 0.dp,
                color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .indication(interactionSource, rememberRipple(bounded = true))
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onToggle(date) },
                onLongClick = { /* Optionally handle long press for more options */ }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = contentColor
        )
    }
}

@Composable
private fun EmptyState(message: String, illustrationResId: Int, onBackPress: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = illustrationResId),
            contentDescription = stringResource(R.string.empty_state_illustration_content_description),
            modifier = Modifier.size(180.dp),
            alpha = 0.7f
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(Modifier.height(24.dp))
        ButtonWithPressAnimation(
            onClick = onBackPress,
            text = stringResource(R.string.back_button_text),
            containerColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit, illustrationResId: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = illustrationResId),
            contentDescription = stringResource(R.string.error_state_illustration_content_description),
            modifier = Modifier.size(180.dp),
            alpha = 0.7f
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.error_state_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(Modifier.height(24.dp))
        ButtonWithPressAnimation(
            onClick = onRetry,
            text = stringResource(R.string.retry_button_text),
            containerColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun OfflineState(onRetry: () -> Unit, illustrationResId: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = illustrationResId),
            contentDescription = stringResource(R.string.offline_state_illustration_content_description),
            modifier = Modifier.size(180.dp),
            alpha = 0.7f
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.offline_state_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.offline_state_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(Modifier.height(24.dp))
        ButtonWithPressAnimation(
            onClick = onRetry,
            text = stringResource(R.string.retry_button_text),
            containerColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHabitDetailScreen() {
    HabitFlowTheme {
        HabitDetailScreen(
            viewModel = previewHabitDetailViewModel(habitId = 1L),
            onBackPress = { /* no-op */ },
            onNavigateToEditHabit = { /* no-op */ }
        )
    }
}