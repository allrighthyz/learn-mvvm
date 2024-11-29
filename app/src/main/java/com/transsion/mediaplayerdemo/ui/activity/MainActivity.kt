package com.transsion.mediaplayerdemo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.transsion.mediaplayerdemo.R
import com.transsion.mediaplayerdemo.ui.viewModel.MainViewModel
import com.transsion.mediaplayerdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化ViewModel
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // 设置观察者
        viewModel.title.observe(this, Observer { title ->
            // 更新UI，themes 标题栏已经去除(NoActionBar) 这里是示例
             supportActionBar?.title = title
        })

        val navController: NavController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.navView, navController)
    }
}