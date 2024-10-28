package com.transsion.mediaplayerdemo.data.repository

import com.transsion.mediaplayerdemo.data.dao.UserDao
import com.transsion.mediaplayerdemo.data.entities.User


class UserRepository(private val userDao: UserDao) {

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }
}
