package com.transsion.mediaplayerdemo.learn.delegate

import org.testng.annotations.Test

class GamePlayerDelegateTest {
    @Test
    fun test() {
        val RealGamePlayer = RealGamePlayer("XXX")
        val delegateGamePlayer = DelegateGamePlayer(RealGamePlayer)
        delegateGamePlayer.rank()
        delegateGamePlayer.upgrade()
    }

}