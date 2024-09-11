package com.transsion.mediaplayerdemo.permission
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionsHelper {

    fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermissions(fragment: Fragment, permissionRequest: PermissionRequest) {
        if (shouldShowRequestPermissionRationale(fragment, permissionRequest.permissions)) {
            // 向用户解释为什么需要这些权限
            AlertDialog.Builder(fragment.requireContext())
                .setTitle(permissionRequest.rationaleTitle)
                .setMessage(permissionRequest.rationaleMessage)
                .setPositiveButton("确定") { _, _ ->
                    // 重新请求权限
                    fragment.requestPermissions(
                        permissionRequest.permissions,
                        permissionRequest.requestCode
                    )
                }
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        } else {
            // 直接请求权限
            fragment.requestPermissions(
                permissionRequest.permissions,
                permissionRequest.requestCode
            )
        }
    }

    private fun shouldShowRequestPermissionRationale(fragment: Fragment, permissions: Array<String>): Boolean {
        return permissions.any { fragment.shouldShowRequestPermissionRationale(it) }
    }
    // ... 其他与权限请求相关的辅助函数
}
