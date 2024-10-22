package com.transsion.mediaplayerdemo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.transsion.mediaplayerdemo.R
import com.transsion.mediaplayerdemo.ui.ViewModel.MainViewModel
import com.transsion.mediaplayerdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // 观察ViewModel中的Fragment变化
        viewModel.currentFragment.observe(this, Observer { fragment ->
            fragment?.let { replaceFragment(it) }
        })

        // 设置初始Fragment
        viewModel.setFragment(R.id.navigation_vedio)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            viewModel.setFragment(item.itemId)
            true
        }
    }

    /**
     * 用给定的Fragment替换当前显示的Fragment。
     */
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}

//class MainActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityMainBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding=ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        replaceFragment(VedioFragment())
//        binding.bottomNavigation.setOnNavigationItemSelectedListener {
//            when (it.itemId) {
//                R.id.navigation_first -> replaceFragment(VedioFragment())
//                R.id.navigation_second -> replaceFragment(NextFragment())
//                R.id.navigation_third -> replaceFragment(LastFragment())
//            }
//            true
//        }
//
//    }
//
//    private fun replaceFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, fragment)
//            .commit()
//    }
//}