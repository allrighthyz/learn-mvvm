package com.transsion.mediaplayerdemo.manager

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

object TileManager {
    private var tile: Tile? = null

    fun initTile(service: TileService) {
        tile = service.qsTile
    }

    fun updateTile(action: Tile.() -> Unit) {
        tile?.apply {
            action(this)
            updateTile()
        }
    }

    fun resetTile() {
        tile = null
    }
}