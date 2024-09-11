package com.transsion.mediaplayerdemo.permission

data class PermissionRequest(
    val permissions: Array<String>,
    val requestCode: Int,
    val rationaleTitle: String,
    val rationaleMessage: String
)
