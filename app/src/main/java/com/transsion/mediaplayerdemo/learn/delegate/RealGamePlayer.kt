package com.transsion.mediaplayerdemo.learn.delegate

import android.util.Log

class RealGamePlayer(private val name:String) : IGamePlayer {
    companion object{
        const val TAG = "RealGamePlayer"
    }
    override fun rank() {
       println("$TAG $name is ranking")
    }

    override fun upgrade() {
        println("$TAG $name is upgrading")
    }
}