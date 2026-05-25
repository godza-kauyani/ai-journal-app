package com.example.data

import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(private val taskDao: TaskDao) : TaskRepository {
    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
    }

    override suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task)
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    override suspend fun deleteTaskById(id: Int) {
        taskDao.deleteTaskById(id)
    }
}
