package com.transsion.mediaplayerdemo.ui.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    // 使用LiveData来观察数据变化
    private val _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title

    fun setTitle(newTitle: String) {
        _title.value = newTitle
    }
}
