package com.transsion.mediaplayerdemo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.transsion.mediaplayerdemo.R
import com.transsion.mediaplayerdemo.ui.ViewModel.MainViewModel
import com.transsion.mediaplayerdemo.databinding.ActivityMainBinding
import com.transsion.mediaplayerdemo.ui.splash.SplashFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // 显示启动动画
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SplashFragment())
                .commit()
        }

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
    /**
     * 导航到主界面
     */
    fun navigateToMain() {
        viewModel.setFragment(R.id.navigation_vedio)
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