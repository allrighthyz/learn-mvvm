package com.transsion.mediaplayerdemo.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.transsion.mediaplayerdemo.R
import java.io.File

class NextFragment : Fragment(), SurfaceHolder.Callback {

    private lateinit var surfaceView: SurfaceView
    private lateinit var mediaRecorder: MediaRecorder
    private var isRecording = false
    private var videoFile: File? = null

    private val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_next, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        surfaceView = view.findViewById(R.id.surfaceView)
        surfaceView.holder.addCallback(this)

        view.findViewById<Button>(R.id.startButton).setOnClickListener {
            startRecording()
        }

        view.findViewById<Button>(R.id.pauseButton).setOnClickListener {
            pauseRecording()
        }

        view.findViewById<Button>(R.id.resumeButton).setOnClickListener {
            resumeRecording()
        }

        view.findViewById<Button>(R.id.stopButton).setOnClickListener {
            stopRecording()
        }

    }

    private fun hasPermissions() = permissions.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(), permissions, PERMISSIONS_REQUEST_CODE)
    }

    private fun setupMediaRecorder() {
        videoFile = File(requireContext().getExternalFilesDir(null), "video.mp4")
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.CAMERA)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setOutputFile(videoFile?.absolutePath)
            setVideoSize(1920, 1080)
            setVideoFrameRate(30)
            setVideoEncodingBitRate(10000000)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
            setPreviewDisplay(surfaceView.holder.surface)
            prepare()
        }
    }

    private fun startRecording() {
        if (!hasPermissions()) {
            requestPermissions()
            return
        }
        setupMediaRecorder()
        mediaRecorder.start()
        isRecording = true
    }

    private fun stopRecording() {
        if (isRecording) {
            mediaRecorder.stop()
            mediaRecorder.reset()
            isRecording = false
        }
    }

    private fun pauseRecording() {
        if (isRecording) {
            mediaRecorder.pause() // 暂停录制
        }
    }

    private fun resumeRecording() {
        if (isRecording) {
            mediaRecorder.resume() // 恢复录制
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // Surface is created, start recording if needed
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Surface changed, update recording parameters if needed
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopRecording() // Ensure recording is stopped when surface is destroyed
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 1
    }

}