package com.androidforge.habitflow.presentation.ui.addedit

import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.androidforge.habitflow.presentation.ui.common.components.LoadingShimmer
import com.androidforge.habitflow.presentation.ui.habits.ButtonWithPressAnimation
import com.androidforge.habitflow.presentation.ui.theme.HabitFlowTheme
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AddEditHabitScreen(
    viewModel: AddEditHabitViewModel = hiltViewModel(),
    onBackPress: () -> Unit,
    onHabitSaved: () -> Unit,
    onHabitDeleted: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if (uiState is AddEditHabitUiState.Success && (uiState as AddEditHabitUiState.Success).isSaved) {
            onHabitSaved()
        }
        if (uiState is AddEditHabitUiState.Success && (uiState as AddEditHabitUiState.Success).isDeleted) {
            onHabitDeleted()
        }
    }

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
                    if (uiState is AddEditHabitUiState.Success && (uiState as AddEditHabitUiState.Success).habitId != null) {
                        ButtonWithPressAnimation(
                            onClick = viewModel::onDeleteHabitClick,
                            text = stringResource(R.string.delete_button_text),
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    ButtonWithPressAnimation(
                        onClick = viewModel::onSaveHabitClick,
                        text = stringResource(R.string.save_button_text),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) {\ paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Crossfade(targetState = uiState, label = "AddEditHabitUiState Transition") {\ state ->
                when (state) {
                    is AddEditHabitUiState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            LoadingShimmer(modifier = Modifier.fillMaxWidth().height(56.dp))
                            LoadingShimmer(modifier = Modifier.fillMaxWidth().height(120.dp))
                            LoadingShimmer(modifier = Modifier.fillMaxWidth().height(56.dp))
                            LoadingShimmer(modifier = Modifier.fillMaxWidth().height(100.dp))
                        }
                    }
                    is AddEditHabitUiState.Success -> {
                        val currentHabit = state.habit
                        val isEditing = state.habitId != null

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                                .animateContentSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = if (isEditing) stringResource(R.string.edit_habit_title) else stringResource(R.string.add_habit_title),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))

                            OutlinedTextField(
                                value = currentHabit.name,
                                onValueChange = viewModel::onNameChange,
                                label = { Text(stringResource(R.string.habit_name_label)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = state.nameError != null,
                                supportingText = { if (state.nameError != null) Text(state.nameError) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    errorBorderColor = MaterialTheme.colorScheme.error
                                )
                            )

                            OutlinedTextField(
                                value = currentHabit.description,
                                onValueChange = viewModel::onDescriptionChange,
                                label = { Text(stringResource(R.string.habit_description_label)) },
                                modifier = Modifier.fillMaxWidth().height(120.dp),
                                maxLines = 4,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                )
                            )

                            // Frequency Selector
                            Column {
                                Text(
                                    text = stringResource(R.string.habit_frequency_label),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(8.dp))
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(7),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth().height(100.dp)
                                ) {
                                    items(DayOfWeek.values()) { day ->
                                        val isSelected = currentHabit.frequency.contains(day)
                                        val interactionSource = remember { MutableInteractionSource() }
                                        val isPressed by interactionSource.collectIsPressedAsState()
                                        val scale by animateFloatAsState(
                                            targetValue = if (isPressed) 0.9f else 1f,
                                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
                                        )

                                        Card(
                                            shape = MaterialTheme.shapes.small,
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 4.dp),
                                            modifier = Modifier
                                                .size(48.dp)
                                                .scale(scale)
                                                .indication(interactionSource, rememberRipple(bounded = true))
                                                .clickable(interactionSource = interactionSource, indication = null) { viewModel.onFrequencyChange(day) }
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                                )
                                            }
                                        }
                                    }
                                }
                                AnimatedVisibility(visible = state.frequencyError != null) {
                                    Text(
                                        text = state.frequencyError ?: "",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                    )
                                }
                            }

                            // Reminder Time
                            Column {
                                Text(
                                    text = stringResource(R.string.reminder_time_label),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(8.dp))
                                val timePickerDialog = TimePickerDialog(
                                    context,
                                    { _, hour: Int, minute: Int ->
                                        viewModel.onReminderTimeChange(LocalTime.of(hour, minute))
                                    },
                                    currentHabit.reminderTime?.hour ?: LocalTime.now().hour,
                                    currentHabit.reminderTime?.minute ?: LocalTime.now().minute,
                                    true // is24HourView
                                )

                                OutlinedTextField(
                                    value = currentHabit.reminderTime?.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) ?: stringResource(R.string.no_reminder_set),
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(stringResource(R.string.reminder_time_field_label)) },
                                    trailingIcon = {
                                        IconButton(onClick = { timePickerDialog.show() }) {
                                            Icon(
                                                imageVector = Icons.Filled.AccessTime,
                                                contentDescription = stringResource(R.string.select_time_content_description)
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(onClick = { timePickerDialog.show() }, indication = rememberRipple(), interactionSource = remember { MutableInteractionSource() }),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                    )
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    ButtonWithPressAnimation(
                                        onClick = { viewModel.onReminderTimeChange(null) },
                                        text = stringResource(R.string.clear_reminder_button_text),
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.reminder_note),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                        }

                        AnimatedVisibility(visible = state.isSaving) {
                            Box(
                                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                    is AddEditHabitUiState.Error -> {
                        ErrorState(
                            message = state.message,
                            onRetry = onBackPress, // For add/edit, retry means going back or starting over
                            illustrationResId = R.drawable.ic_error_state // Placeholder
                        )
                    }
                    is AddEditHabitUiState.Offline -> {
                        OfflineState(
                            onRetry = viewModel::loadHabit,
                            illustrationResId = R.drawable.ic_offline_state // Placeholder
                        )
                    }
                }
            }
        }
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
            text = stringResource(R.string.back_button_text),
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
fun PreviewAddEditHabitScreen() {
    HabitFlowTheme {
        AddEditHabitScreen(
            viewModel = previewAddEditHabitViewModel(habitId = null),
            onBackPress = {},
            onHabitSaved = {},
            onHabitDeleted = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditHabitScreen() {
    HabitFlowTheme {
        AddEditHabitScreen(
            viewModel = previewAddEditHabitViewModel(habitId = 1L),
            onBackPress = {},
            onHabitSaved = {},
            onHabitDeleted = {}
        )
    }
}