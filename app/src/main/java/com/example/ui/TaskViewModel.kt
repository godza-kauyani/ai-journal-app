package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Task
import com.example.data.TaskDatabase
import com.example.data.TaskRepository
import com.example.data.TaskRepositoryImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.first

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    // Retrieve tasks reactively
    val tasks: StateFlow<List<Task>> = repository.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Tab State for Mobile UI
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    // Pomodoro Timer States
    private val _timerDurationTotal = MutableStateFlow(25 * 60) // 25 minutes default
    val timerDurationTotal: StateFlow<Int> = _timerDurationTotal.asStateFlow()

    private val _timerSecondsLeft = MutableStateFlow(25 * 60)
    val timerSecondsLeft: StateFlow<Int> = _timerSecondsLeft.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Feed initial dummy data if the database is blank
        viewModelScope.launch {
            try {
                val list = repository.getAllTasks().first()
                if (list.isEmpty()) {
                    seedInitialTasks()
                }
            } catch (e: Exception) {
                // Gracefully handle any startup exceptions
            }
        }
    }

    // Start or Pause Pomodoro Focus Timer
    fun toggleTimer() {
        if (_isTimerRunning.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_timerSecondsLeft.value > 0 && _isTimerRunning.value) {
                delay(1000)
                _timerSecondsLeft.value -= 1
            }
            if (_timerSecondsLeft.value == 0) {
                _isTimerRunning.value = false
            }
        }
    }

    private fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer(minutes: Int = 25) {
        pauseTimer()
        _timerDurationTotal.value = minutes * 60
        _timerSecondsLeft.value = minutes * 60
    }

    private suspend fun seedInitialTasks() {
        repository.insertTask(
            Task(
                title = "Welcome to TaskSphere!",
                description = "TaskSphere is a minimalist Kanban board. Swipe right or click options on this card of ours.",
                status = "TODO",
                priority = "LOW",
                category = "Personal"
            )
        )
        repository.insertTask(
            Task(
                title = "Study Design System Guidelines",
                description = "Read standard guidelines for Jetpack Compose and practice implementing beautiful Material 3 cards.",
                status = "IN_PROGRESS",
                priority = "MEDIUM",
                category = "Study"
            )
        )
        repository.insertTask(
            Task(
                title = "Revise Physics Lecture Notes",
                description = "Go over thermal formulas and sample equations from textbook chapter 4.",
                status = "TODO",
                priority = "HIGH",
                category = "Study"
            )
        )
        repository.insertTask(
            Task(
                title = "Clean the environment setup",
                description = "Ready tools and compile basic testing code successfully.",
                status = "DONE",
                priority = "LOW",
                category = "Work"
            )
        )
    }

    // Add normal Task
    fun addTask(title: String, description: String, priority: String, category: String, status: String = "TODO") {
        viewModelScope.launch {
            repository.insertTask(
                Task(
                    title = title,
                    description = description,
                    priority = priority,
                    category = category,
                    status = status
                )
            )
        }
    }

    // Update Task category, priority, status etc
    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    // Change status / move step
    fun moveTask(task: Task, newStatus: String) {
        viewModelScope.launch {
            repository.updateTask(task.copy(status = newStatus))
        }
    }

    // Delete Task
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun deleteTaskById(id: Int) {
        viewModelScope.launch {
            repository.deleteTaskById(id)
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val database = TaskDatabase.getDatabase(context.applicationContext)
                val repository = TaskRepositoryImpl(database.taskDao)
                return TaskViewModel(repository) as T
            }
        }
    }
}
