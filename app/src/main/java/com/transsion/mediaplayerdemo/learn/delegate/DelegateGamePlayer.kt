package com.transsion.mediaplayerdemo.learn.delegate

class DelegateGamePlayer(private val gamePlayer: IGamePlayer): IGamePlayer by gamePlayer {

}