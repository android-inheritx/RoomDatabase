package com.example.roomdatabasedemo.database

import android.arch.persistence.room.*


@Entity(indices = [Index("phone_number", unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int? = null,
    val name: String,
    val email: String,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    val age: Int
)


