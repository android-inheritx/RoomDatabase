package com.example.roomdatabasedemo.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Update
    fun updateUser(user: User)

    @Query("SELECT * FROM User WHERE name LIKE :name")
    fun getUserByName(name: String): LiveData<List<User>>

    @Query("SELECT * FROM user WHERE email IN (:emails)")
    fun getUsersFromEmail(emails: List<String>): LiveData<List<User>>

    @Query("SELECT * FROM User")
    fun getAllUser(): LiveData<List<User>>

    @Delete
    fun deleteUser(user: User)

    @Query("DELETE FROM User WHERE userId == :userId")
    fun deleteUserById(userId: Int)
}