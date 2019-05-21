package com.example.roomdatabasedemo.database

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

class UserViewModel(application: Application) : AndroidViewModel(application) {
    var userRepository: UserRepository? = null

    init {
        userRepository = UserRepository(application)
    }

    fun getAllUser(): LiveData<List<User>>? {
        return userRepository?.getAllUser()
    }

    fun insertUser(user: User) {
        userRepository?.insertUser(user)
    }

    fun updateUser(user: User) {
        userRepository?.updateUser(user)
    }

    fun getUserByName(name: String): LiveData<List<User>>? {
        return userRepository?.getUserByName(name)
    }

    fun getUsersFromEmail(emails: List<String>): LiveData<List<User>>? {
        return userRepository?.getUsersFromEmail(emails)
    }

    fun deleteUserById(userId: Int) {
        userRepository?.deleteUserById(userId)
    }


}