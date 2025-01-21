package com.transsion.mediaplayerdemo.ui.viewModel

import android.net.Uri
import android.util.Log
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

    private val _selectImageEvent = MutableLiveData<Unit>()
    val selectImageEvent: LiveData<Unit> get() = _selectImageEvent

    // LiveData to hold user input data
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    var avatarUri: Uri? = null

    private fun insertUser() {
        val user = User(
            name = name.value ?: "",
            email = email.value ?: "",
            phone = phone.value ?: "",
            avatarUri = avatarUri?.toString()
        )
        viewModelScope.launch {
            repository.insert(user)
            _userAdded.value = true // 通知UI用户已添加
        }
    }

    fun onSelectImageClick() {
        _selectImageEvent.value = Unit // 通知UI选择图片事件
    }

    fun onAddUserClick() {
        // 检查输入是否有效
        if (name.value.isNullOrEmpty() || email.value.isNullOrEmpty() || phone.value.isNullOrEmpty()) {
            // 添加逻辑来通知UI显示错误信息
            return
        }
        // 插入用户
        insertUser()
    }

    fun showGallery(){
     Log.d("show","show")
    }
}
