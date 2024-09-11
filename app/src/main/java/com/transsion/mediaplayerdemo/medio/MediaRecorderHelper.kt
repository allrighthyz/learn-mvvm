package com.transsion.mediaplayerdemo.medio

import android.content.Context
import android.media.MediaRecorder
import java.io.File

object MediaRecorderHelper {

    fun prepareMediaRecorder(
        context: Context,
        audioSource: Int,
        videoSource: Int,
        outputFormat: Int,
        outputFile: String,
        videoEncodingBitRate: Int,
        videoFrameRate: Int,
        videoSizeWidth: Int,
        videoSizeHeight: Int,
        videoEncoder: Int,
        audioEncoder: Int,
        orientationHint: Int
    ): MediaRecorder {
        return MediaRecorder().apply {
            setAudioSource(audioSource)
            setVideoSource(videoSource)
            setOutputFormat(outputFormat)
            setOutputFile(File(context.externalCacheDir, outputFile).absolutePath)
            setVideoEncodingBitRate(videoEncodingBitRate)
            setVideoFrameRate(videoFrameRate)
            setVideoSize(videoSizeWidth, videoSizeHeight)
            setVideoEncoder(videoEncoder)
            setAudioEncoder(audioEncoder)
            setOrientationHint(orientationHint)
            prepare()
        }
    }
}