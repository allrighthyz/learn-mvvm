package com.transsion.mediaplayerdemo.ui.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.transsion.mediaplayerdemo.data.repository.UserRepository

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}