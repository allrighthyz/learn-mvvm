package com.transsion.mediaplayerdemo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.transsion.mediaplayerdemo.data.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}