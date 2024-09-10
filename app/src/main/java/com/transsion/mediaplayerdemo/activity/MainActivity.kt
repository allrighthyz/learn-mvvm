package com.transsion.mediaplayerdemo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.transsion.mediaplayerdemo.R
import com.transsion.mediaplayerdemo.databinding.ActivityMainBinding
import com.transsion.mediaplayerdemo.fragment.LastFragment
import com.transsion.mediaplayerdemo.fragment.NextFragment
import com.transsion.mediaplayerdemo.fragment.VedioFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(VedioFragment())
        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_first -> replaceFragment(VedioFragment())
                R.id.navigation_second -> replaceFragment(NextFragment())
                R.id.navigation_third -> replaceFragment(LastFragment())
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}