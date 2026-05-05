package com.androidforge.habitflow.presentation.ui.habits

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.androidforge.habitflow.R
import com.androidforge.habitflow.domain.model.Habit
import com.androidforge.habitflow.domain.model.HabitCompletion
import com.androidforge.habitflow.domain.model.HabitStatus
import com.androidforge.habitflow.presentation.ui.common.components.AdBannerView
import com.androidforge.habitflow.presentation.ui.common.components.HabitCard
import com.androidforge.habitflow.presentation.ui.common.components.LoadingShimmer
import com.androidforge.habitflow.presentation.ui.theme.HabitFlowTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HabitListScreen(
    viewModel: HabitListViewModel = hiltViewModel(),
    onNavigateToAddEditHabit: (Long?) -> Unit,
    onNavigateToHabitDetail: (Long) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, can proceed with notifications
        } else {
            // Permission denied, handle gracefully (e.g., show a message)
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.habit_list_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.scale(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.settings_content_description),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.9f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                label = "FAB Scale Animation"
            )

            FloatingActionButton(
                onClick = { onNavigateToAddEditHabit(null) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                interactionSource = interactionSource,
                modifier = Modifier
                    .scale(scale)
                    .padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_habit_content_description)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {\ paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Crossfade(targetState = uiState, label = "HabitListUiState Transition") {\ state ->
                when (state) {
                    is HabitListUiState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Spacer(Modifier.height(8.dp))
                            repeat(5) {
                                LoadingShimmer(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp))
                            }
                        }
                    }
                    is HabitListUiState.Success -> {
                        if (state.habits.isEmpty()) {
                            EmptyState(
                                message = stringResource(R.string.habit_list_empty_message),
                                illustrationResId = R.drawable.ic_empty_list // Placeholder
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(state.habits, key = { it.id }) {\ habit ->
                                    HabitCard(
                                        habit = habit,
                                        onToggleCompletion = { habitToToggle, date ->
                                            viewModel.onHabitCompletionToggled(habitToToggle.id, date)
                                        },
                                        onClick = { habitId ->
                                            onNavigateToHabitDetail(habitId)
                                        },
                                        modifier = Modifier
                                            .animateItemPlacement()
                                            .fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = Random.nextInt(0, 100)))
                                            .slideInVertically(animationSpec = tween(durationMillis = 300, delayMillis = Random.nextInt(0, 100)), initialOffsetY = { it / 2 })
                                    )
                                }
                                item {
                                    AdBannerView(adUnitId = stringResource(R.string.admob_banner_ad_unit_id))
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                    is HabitListUiState.Empty -> {
                        EmptyState(
                            message = state.message,
                            illustrationResId = state.illustrationResId
                        )
                    }
                    is HabitListUiState.Error -> {
                        ErrorState(
                            message = state.message,
                            onRetry = state.onRetry,
                            illustrationResId = R.drawable.ic_error_state // Placeholder
                        )
                    }
                    is HabitListUiState.Offline -> {
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
private fun EmptyState(message: String, illustrationResId: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = illustrationResId),
            contentDescription = stringResource(R.string.empty_state_illustration_content_description),
            modifier = Modifier.size(180.dp),
            alpha = 0.7f // Soften the illustration
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(Modifier.height(48.dp))
        Text(
            text = stringResource(R.string.empty_state_suggestion),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.7f)
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

// Reusable Button with press animation
@Composable
fun ButtonWithPressAnimation(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "Button Scale Animation"
    )

    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .indication(interactionSource, rememberRipple())
            .height(56.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = MaterialTheme.shapes.small,
        elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHabitListScreen() {
    HabitFlowTheme {
        HabitListScreen(
            viewModel = previewHabitListViewModel(),
            onNavigateToAddEditHabit = { /* no-op */ },
            onNavigateToHabitDetail = { /* no-op */ },
            onNavigateToSettings = { /* no-op */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmptyState() {
    HabitFlowTheme {
        EmptyState(
            message = stringResource(R.string.habit_list_empty_message),
            illustrationResId = R.drawable.ic_empty_list // Placeholder
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewErrorState() {
    HabitFlowTheme {
        ErrorState(
            message = "Failed to load habits. Please check your connection.",
            onRetry = { /* no-op */ },
            illustrationResId = R.drawable.ic_error_state // Placeholder
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOfflineState() {
    HabitFlowTheme {
        OfflineState(
            onRetry = { /* no-op */ },
            illustrationResId = R.drawable.ic_offline_state // Placeholder
        )
    }
}