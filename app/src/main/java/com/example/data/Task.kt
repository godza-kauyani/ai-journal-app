package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val status: String,     // "TODO", "IN_PROGRESS", "DONE"
    val priority: String,   // "LOW", "MEDIUM", "HIGH"
    val category: String = "Personal", // "Work", "Personal", "Study", etc.
    val createdAt: Long = System.currentTimeMillis()
)
