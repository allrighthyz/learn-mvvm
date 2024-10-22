package com.transsion.mediaplayerdemo.ui.ViewModel

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.transsion.mediaplayerdemo.R
import com.transsion.mediaplayerdemo.ui.fragment.CommunicationFragment
import com.transsion.mediaplayerdemo.ui.fragment.RecordFragment
import com.transsion.mediaplayerdemo.ui.fragment.VedioFragment

class MainViewModel : ViewModel() {

    // LiveData用于存储当前的Fragment类型
    private val _currentFragment = MutableLiveData<Fragment>()
    val currentFragment: LiveData<Fragment> get() = _currentFragment

    // 设置当前Fragment
    fun setFragment(itemId: Int) {
        val fragment = when (itemId) {
            R.id.navigation_vedio -> VedioFragment()
            R.id.navigation_record -> RecordFragment()
            R.id.navigation_communication -> CommunicationFragment()
            else -> throw IllegalArgumentException("Unknown navigation item")
        }
        _currentFragment.value = fragment
    }
}
