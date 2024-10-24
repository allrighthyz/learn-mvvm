package com.transsion.mediaplayerdemo.service

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.transsion.mediaplayerdemo.R
import com.transsion.mediaplayerdemo.manager.TileManager
import com.transsion.mediaplayerdemo.ui.activity.BatteryDialogActivity

class IconService : TileService() {

    override fun onTileAdded() {
        super.onTileAdded()
        // 初始化Tile
        TileManager.initTile(this)
        // 设置初始状态
        TileManager.updateTile {
            state = Tile.STATE_INACTIVE
            label = getString(R.string.tile_label)
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        // 更新Tile状态
        updateTileState()
    }

    override fun onStopListening() {
        super.onStopListening()
        // 停止监听时的处理
        TileManager.resetTile()
    }

    override fun onClick() {
        super.onClick()
        BatteryDialogActivity.start(this)
        TileManager.updateTile {
            state = if (state == Tile.STATE_ACTIVE) {
                Tile.STATE_INACTIVE
            } else {
                Tile.STATE_ACTIVE
            }
        }
    }

    private fun updateTileState() {
        // 根据某个条件更新Tile状态
        TileManager.updateTile {
            state = Tile.STATE_INACTIVE
        }
    }
}
