package com.example.roomdatabasedemo.database

import android.app.Application
import android.arch.lifecycle.LiveData

class UserRepository(application: Application) {
    var userDao: UserDao? = null

    init {
        val userDatabase = AppDatabase.getAppDataBase(application)
        userDao = userDatabase?.userDao()
    }

    fun getAllUser(): LiveData<List<User>>? {
        return userDao?.getAllUser()
    }

    fun insertUser(user: User) {
        userDao?.insertUser(user)
    }

    fun updateUser(user: User) {
        userDao?.updateUser(user)
    }

    fun getUserByName(name: String): LiveData<List<User>>? {
        return userDao?.getUserByName(name)
    }

    fun getUsersFromEmail(emails: List<String>): LiveData<List<User>>? {
        return userDao?.getUsersFromEmail(emails)
    }

    fun deleteUserById(userId: Int) {
        userDao?.deleteUserById(userId)
    }
}