package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.LowPriority
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Task
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBoardScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    var showSheet by remember { mutableStateOf(false) }
    
    // Bottom Sheet State
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    // Status tabs helper
    val columns = listOf("TODO" to "To Do", "IN_PROGRESS" to "In Progress", "DONE" to "Done")
    
    // Priority filter (Low, Medium, High, All)
    var priorityFilter by remember { mutableStateOf("ALL") }
    var categoryFilter by remember { mutableStateOf("ALL") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.secondary
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "T",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TaskSphere",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Students & Professionals Focus Hub",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                },
                actions = {
                    FilterMenu(
                        currentPriority = priorityFilter,
                        currentCategory = categoryFilter,
                        onPrioritySelected = { priorityFilter = it },
                        onCategorySelected = { categoryFilter = it }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .testTag("add_task_fab")
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Productive task")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Focus Goal", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val isWideScreen = maxWidth >= 600.dp
            
            // Filter tasks based on settings
            val filteredTasks = tasks.filter { task ->
                val matchesPriority = priorityFilter == "ALL" || task.priority.uppercase() == priorityFilter
                val matchesCategory = categoryFilter == "ALL" || task.category.uppercase() == categoryFilter
                matchesPriority && matchesCategory
            }

            Column(modifier = Modifier.fillMaxSize()) {
                // Collapsible Focus Timer (Pomodoro) Section
                PomodoroTimerCard(viewModel = viewModel)
                
                if (isWideScreen) {
                    // Side-by-Side Horizontal Columns for Expanded screens
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        columns.forEach { (status, label) ->
                            val columnTasks = filteredTasks.filter { it.status == status }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                // Header for wide screen columns
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(statusColor(status))
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = label,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = columnTasks.size.toString(),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                TaskColumnList(
                                    tasks = columnTasks,
                                    status = status,
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                } else {
                    // Sliding tab row & single selected-tab page for compact screens
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        columns.forEachIndexed { index, (status, label) ->
                            val badgeCount = filteredTasks.count { it.status == status }
                            Tab(
                                selected = selectedTab == index,
                                onClick = { viewModel.selectTab(index) },
                                modifier = Modifier.testTag("tab_$status")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (selectedTab == index)
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                                else
                                                    MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            .padding(horizontal = 6.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = badgeCount.toString(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val currentStatus = columns[selectedTab].first
                    val currentColumnTasks = filteredTasks.filter { it.status == currentStatus }
                    
                    TaskColumnList(
                        tasks = currentColumnTasks,
                        status = currentStatus,
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                    )
                }
            }
        }
        
        // Modal Sheet for adding productive goals
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                dragHandle = { Spacer(modifier = Modifier.height(16.dp)) }
            ) {
                AddTaskFormSheet(
                    onDismiss = { showSheet = false },
                    onTaskCreated = { title, desc, priority, category ->
                        viewModel.addTask(title, desc, priority, category)
                        showSheet = false
                    }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// FILTER DROPDOWN MENU
// -------------------------------------------------------------
@Composable
fun FilterMenu(
    currentPriority: String,
    currentCategory: String,
    onPrioritySelected: (String) -> Unit,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.testTag("filter_menu_button")
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Search Filter Toggle",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            Text(
                text = "Priority Filter",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
            val priorities = listOf("ALL", "LOW", "MEDIUM", "HIGH")
            priorities.forEach { p ->
                val active = currentPriority == p
                DropdownMenuItem(
                    text = {
                        Text(
                            text = p.lowercase().replaceFirstChar { it.uppercase() },
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onPrioritySelected(p)
                        expanded = false
                    }
                )
            }
            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
            Text(
                text = "Category Filter",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
            val categories = listOf("ALL", "WORK", "PERSONAL", "STUDY")
            categories.forEach { c ->
                val active = currentCategory == c
                DropdownMenuItem(
                    text = {
                        Text(
                            text = c.lowercase().replaceFirstChar { it.uppercase() },
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onCategorySelected(c)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun Divider(color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color)
    )
}

// -------------------------------------------------------------
// POMODORO TIMER COMPOSABLE (Designed to aid deep focus)
// -------------------------------------------------------------
@Composable
fun PomodoroTimerCard(viewModel: TaskViewModel) {
    val secondsLeft by viewModel.timerSecondsLeft.collectAsState()
    val totalSeconds by viewModel.timerDurationTotal.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    
    var isTimerExpanded by remember { mutableStateOf(false) }
    
    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val progress = if (totalSeconds > 0) secondsLeft.toFloat() / totalSeconds.toFloat() else 0f
    
    val timerString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isTimerExpanded = !isTimerExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Pomodoro Clock Launcher",
                        tint = if (isTimerRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pomodoro Timer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (isTimerRunning) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "FOCUS ZONE ACTIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                Text(
                    text = if (isTimerExpanded) "Collapse ▲" else "$timerString ■ Expand ▼",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            AnimatedVisibility(
                visible = isTimerExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timerString,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        // Control Buttons
                        Row {
                            Button(
                                onClick = { viewModel.toggleTimer() },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isTimerRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isTimerRunning) Icons.Default.Close else Icons.Default.PlayArrow,
                                        contentDescription = "Timer Play/Pause",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isTimerRunning) "Pause" else "Start",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            OutlinedButton(
                                onClick = { viewModel.resetTimer(25) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Timer Reset",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Interval Presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(15, 25, 45, 60).forEach { mins ->
                            val isPresetRunning = totalSeconds == mins * 60
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = 1.dp,
                                        color = if (isPresetRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .background(
                                        if (isPresetRunning) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        else Color.Transparent
                                    )
                                    .clickable { viewModel.resetTimer(mins) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$mins m",
                                    fontSize = 12.sp,
                                    fontWeight = if (isPresetRunning) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isPresetRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TASK COLUMN COLUMN/LIST
// -------------------------------------------------------------
@Composable
fun TaskColumnList(
    tasks: List<Task>,
    status: String,
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    if (tasks.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 40.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Inbox,
                    contentDescription = "No Focus Goals",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = when (status) {
                        "TODO" -> "No focus goals draft"
                        "IN_PROGRESS" -> "No active focus blocks"
                        else -> "No finished metrics"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (status) {
                        "TODO" -> "Add a task using '+' below to start planning."
                        "IN_PROGRESS" -> "Start a task from your layout."
                        else -> "Finish your goals to populate statistics."
                    },
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(tasks, key = { it.id }) { task ->
                SwipeDismissContainer(
                    task = task,
                    onDismissed = { viewModel.deleteTask(task) }
                ) {
                    TaskCard(task = task, viewModel = viewModel)
                }
            }
            item {
                Spacer(modifier = Modifier.height(80.dp)) // Avoid covering list by FAB
            }
        }
    }
}

// Swipe To Dismiss container wrapper using Material 3 SwipeToDismissBox
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeDismissContainer(
    task: Task,
    onDismissed: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDismissed()
                true
            } else {
                false
            }
        }
    )
    
    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    else -> Color.Transparent
                },
                label = "delete_color_anim"
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Swipe Delete Action",
                    tint = Color.White
                )
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        content = {
            content()
        }
    )
}

// -------------------------------------------------------------
// TASK CARD COMPOSABLE (Kanban card)
// -------------------------------------------------------------
@Composable
fun TaskCard(
    task: Task,
    viewModel: TaskViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Priority Tag and Category indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Priority Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(getPriorityColor(task.priority).copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (task.priority.uppercase()) {
                                "HIGH" -> Icons.Default.PriorityHigh
                                else -> Icons.Default.LowPriority
                            },
                            contentDescription = "Priority Flag",
                            tint = getPriorityColor(task.priority),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${task.priority} Priority",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = getPriorityColor(task.priority)
                        )
                    }
                }
                
                // Category Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(getCategoryColor(task.category).copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (task.category.uppercase()) {
                                "PERSONAL" -> Icons.Default.Person
                                "WORK" -> Icons.Default.BusinessCenter
                                "STUDY" -> Icons.Default.Book
                                else -> Icons.Default.Home
                            },
                            contentDescription = "Category Flag",
                            tint = getCategoryColor(task.category),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = task.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = getCategoryColor(task.category)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Title
            Text(
                text = task.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textDecoration = if (task.status == "DONE") TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Description
            if (task.description.isNotBlank()) {
                Text(
                    text = task.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            // Card bottom controls to shift columns AND delete tasks directly
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Delete button
                IconButton(
                    onClick = { viewModel.deleteTask(task) },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("delete_task_${task.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Single task directly",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Quick columns shift triggers
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (task.status != "TODO") {
                        TriggerCardShiftButton(
                            icon = Icons.Default.ArrowBack,
                            label = when (task.status) {
                                "IN_PROGRESS" -> "To Do"
                                else -> "Start"
                            },
                            onClick = {
                                val prevStatus = when (task.status) {
                                    "DONE" -> "IN_PROGRESS"
                                    else -> "TODO"
                                }
                                viewModel.moveTask(task, prevStatus)
                            }
                        )
                    }
                    
                    if (task.status != "TODO" && task.status != "DONE") {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    
                    if (task.status != "DONE") {
                        TriggerCardShiftButton(
                            icon = Icons.Default.ArrowForward,
                            label = when (task.status) {
                                "TODO" -> "Start"
                                else -> "Finish"
                            },
                            onClick = {
                                val nextStatus = when (task.status) {
                                    "TODO" -> "IN_PROGRESS"
                                    else -> "DONE"
                                }
                                viewModel.moveTask(task, nextStatus)
                            },
                            isPrimary = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TriggerCardShiftButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    isPrimary: Boolean = false
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon == Icons.Default.ArrowBack) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            if (icon == Icons.Default.ArrowForward) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp))
            }
        }
    }
}

// -------------------------------------------------------------
// ADD TASK BOTTOM SHEET FORM
// -------------------------------------------------------------
@Composable
fun AddTaskFormSheet(
    onDismiss: () -> Unit,
    onTaskCreated: (title: String, desc: String, priority: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("MEDIUM") }
    var category by remember { mutableStateOf("Personal") }
    
    val priorities = listOf("LOW", "MEDIUM", "HIGH")
    val categories = listOf("Personal", "Work", "Study")
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Map Deep Focus Goal",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close Panel")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Input Title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Task Focus Title") },
            placeholder = { Text("Enter task name...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_task_title_input"),
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Input Description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description & Notes") },
            placeholder = { Text("Add instructions or materials to study...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .testTag("add_task_desc_input"),
            maxLines = 4,
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Select Priority
        Text(
            text = "Select Intent Priority",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            priorities.forEach { p ->
                val active = priority == p
                val activeBg = remember(p) { getPriorityColor(p) }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (active) activeBg.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                        .border(
                            width = 1.5.dp,
                            color = if (active) activeBg else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { priority = p }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = p,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (active) activeBg else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Select Category
        Text(
            text = "Category Stream",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val active = category == cat
                val activeBg = remember(cat) { getCategoryColor(cat) }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (active) activeBg.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                        .border(
                            width = 1.5.dp,
                            color = if (active) activeBg else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { category = cat }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (active) activeBg else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Submit
        Button(
            onClick = {
                if (title.isNotBlank()) {
                    onTaskCreated(title, description, priority, category)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("submit_task_button"),
            enabled = title.isNotBlank(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Activate Focus Goal",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// -------------------------------------------------------------
// HELPER METHODS
// -------------------------------------------------------------
fun getPriorityColor(priority: String): Color {
    return when (priority.uppercase()) {
        "LOW" -> PriorityLowColor
        "MEDIUM" -> PriorityMediumColor
        "HIGH" -> PriorityHighColor
        else -> PriorityLowColor
    }
}

fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "work" -> CatWorkColor
        "study" -> CatStudyColor
        "personal" -> CatPersonalColor
        else -> CatOtherColor
    }
}

fun statusColor(status: String): Color {
    return when (status) {
        "TODO" -> PriorityMediumColor
        "IN_PROGRESS" -> SlateLightPrimary
        "DONE" -> PriorityLowColor
        else -> CatOtherColor
    }
}
