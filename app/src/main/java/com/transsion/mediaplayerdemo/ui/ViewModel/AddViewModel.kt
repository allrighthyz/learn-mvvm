package com.transsion.mediaplayerdemo.ui.ViewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transsion.mediaplayerdemo.data.entities.User
import com.transsion.mediaplayerdemo.data.repository.UserRepository
import kotlinx.coroutines.launch
class AddViewModel(private val repository: UserRepository) : ViewModel() {

    private val _userAdded = MutableLiveData<Boolean>()
    val userAdded: LiveData<Boolean> get() = _userAdded

    fun insertUser(name: String, email: String, phone: String, avatarUri: Uri?) {
        val user = User(
            name = name,
            email = email,
            phone = phone,
            avatarUri = avatarUri?.toString()
        )
        viewModelScope.launch {
            repository.insert(user)
            _userAdded.value = true // 通知UI用户已添加
        }
    }
}
