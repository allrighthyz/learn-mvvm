package com.transsion.mediaplayerdemo.fragment
import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.*
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.transsion.mediaplayerdemo.R
import com.transsion.mediaplayerdemo.medio.MediaRecorderHelper
import com.transsion.mediaplayerdemo.permission.PermissionRequest
import com.transsion.mediaplayerdemo.permission.PermissionsHelper
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class NextFragment : Fragment() {

    private val PERMISSIONS_REQUEST_CODE = 1 // 用于请求多个权限

    private lateinit var surfaceView: SurfaceView
    private lateinit var startButton: Button
    private lateinit var pauseButton: Button
    private lateinit var resumeButton: Button
    private lateinit var stopButton: Button

    private lateinit var cameraManager: CameraManager
    private var cameraDevice: CameraDevice? = null
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var cameraCaptureSession: CameraCaptureSession

    private var isRecording = false
    private var isPaused = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_next, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        surfaceView = view.findViewById(R.id.surfaceView)
        startButton = view.findViewById(R.id.startButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        resumeButton = view.findViewById(R.id.resumeButton)
        stopButton = view.findViewById(R.id.stopButton)

        // 禁用按钮，直到相机初始化完成
        startButton.isEnabled = false
        pauseButton.isEnabled = false
        resumeButton.isEnabled = false
        stopButton.isEnabled = false

        cameraManager = requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager

        startButton.setOnClickListener { startRecording() }
        pauseButton.setOnClickListener { pauseRecording() }
        resumeButton.setOnClickListener { resumeRecording() }
        stopButton.setOnClickListener { stopRecording() }

        //初始化相机
        initializeCamera()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            cameraAndAudioPermissionRequest.requestCode -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // 权限被授予，初始化相机
                    openCamera()
                } else {
                    // 权限被拒绝，解释功能将无法工作
                    Toast.makeText(requireContext(), "相机和麦克风权限被拒绝，无法录制视频和音频", Toast.LENGTH_SHORT).show()
                }
            }
            // 其他权限请求的处理
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    private fun openCamera() {
        try {
            val cameraId = cameraManager.cameraIdList.first { id ->
                cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
            }
            // 1. 获取相机管理器（cameraManager）中所有可用相机的ID列表，并找到朝向后置的相机ID。

            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // 2. 检查相机权限是否已经授予，如果没有权限则直接返回。这段代码在调用此方法之前已经请求并获得了权限，所以这里通常应该返回true。
                return
            }

            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    Log.d("Camera", "Camera opened")
                    cameraDevice = camera
                    startPreview()
                    // 3. 当相机成功打开时，记录相机设备实例，并启动预览。

                    startButton.isEnabled = true
                    stopButton.isEnabled = true
                    // 4. 启用开始和停止按钮，允许用户进行录制操作。
                }

                override fun onDisconnected(camera: CameraDevice) {
                    Log.d("Camera", "Camera disconnected")
                    camera.close()
                    cameraDevice = null
                    // 5. 当相机断开连接时，关闭相机并将设备实例置为null。
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    Log.e("Camera", "Camera error: $error")
                    camera.close()
                    cameraDevice = null
                    // 6. 当相机发生错误时，记录错误信息，关闭相机并将设备实例置为null。
                }
            }, null)
            // 7. 打开相机，传入相机ID和相机状态回调对象。
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            // 8. 捕获并处理可能的 CameraAccessException 异常。
        }
    }

    private val cameraAndAudioPermissionRequest = PermissionRequest(
        // 权限列表
        permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
        requestCode = PERMISSIONS_REQUEST_CODE,
        rationaleTitle = "需要相机和麦克风权限",
        rationaleMessage = "此应用需要相机和麦克风权限来录制视频和音频。请授予权限。"
    )

    private fun initializeCamera() {
        if (!PermissionsHelper.hasPermissions(requireContext(), *cameraAndAudioPermissionRequest.permissions)) {
            // 如果没有权限，请求权限
            PermissionsHelper.requestPermissions(this, cameraAndAudioPermissionRequest)
            return
        }
        // 如果已经有权限，继续相机的初始化
        openCamera()
    }


    private fun startPreview() {
        // 确保cameraDevice已经初始化
        if (cameraDevice == null) {
            Log.e("Camera", "Camera device not initialized")
            return
        }

        val surfaceHolder = surfaceView.holder
        val previewSurface = surfaceHolder.surface

        try {
            val previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            previewRequestBuilder.addTarget(previewSurface)

            cameraDevice!!.createCaptureSession(listOf(previewSurface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    Log.d("Camera", "Preview configured")
                    cameraCaptureSession = session
                    previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
                    cameraCaptureSession.setRepeatingRequest(previewRequestBuilder.build(), null, null)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Toast.makeText(requireContext(), "Camera preview configuration failed", Toast.LENGTH_SHORT).show()
                }
            }, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }



    private fun startRecording() {
        if (isRecording) return

        if (cameraDevice == null) {
            Toast.makeText(requireContext(), "Camera is not initialized", Toast.LENGTH_SHORT).show()
            return
        }

        prepareMediaRecorder()
        val surfaceHolder = surfaceView.holder
        val surface = surfaceHolder.surface
        val recordingSurface = mediaRecorder.surface

        try {
            val captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
            captureRequestBuilder.addTarget(surface)
            captureRequestBuilder.addTarget(recordingSurface)

            cameraDevice!!.createCaptureSession(listOf(surface, recordingSurface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    Log.d("Camera", "Recording configured")
                    cameraCaptureSession = session
                    captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)

                    mediaRecorder.start()
                    isRecording = true
                    isPaused = false
                    pauseButton.isEnabled = true
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Toast.makeText(requireContext(), "Camera recording configuration failed", Toast.LENGTH_SHORT).show()
                }
            }, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun prepareMediaRecorder() {
        mediaRecorder = MediaRecorderHelper.prepareMediaRecorder(
            context = requireContext(),
            audioSource = MediaRecorder.AudioSource.MIC,
            videoSource = MediaRecorder.VideoSource.SURFACE,
            outputFormat = MediaRecorder.OutputFormat.MPEG_4,
            outputFile = "video.mp4",
            videoEncodingBitRate = 10000000,
            videoFrameRate = 30,
            videoSizeWidth = 1920,
            videoSizeHeight = 1080,
            videoEncoder = MediaRecorder.VideoEncoder.H264,
            audioEncoder = MediaRecorder.AudioEncoder.AAC,
            orientationHint = 90
        )
    }

    private fun pauseRecording() {
        if (!isRecording || isPaused) return

        try {
            mediaRecorder.pause()
            isPaused = true
            // 更新按钮状态的代码
        } catch (e: IllegalStateException) {
            Log.e("NextFragment", "尝试暂停录制时出错", e)
            Toast.makeText(requireContext(), "暂停录制失败，请确保录制正在进行。", Toast.LENGTH_SHORT).show()
        }
    }


    private fun resumeRecording() {
        if (!isRecording || !isPaused) return

        try {
            mediaRecorder.resume()
            isPaused = false
            // 更新按钮状态的代码
        } catch (e: IllegalStateException) {
            Log.e("NextFragment", "尝试恢复录制时出错", e)
            Toast.makeText(requireContext(), "恢复录制失败，请确保录制已暂停。", Toast.LENGTH_SHORT).show()
        }
    }


    private fun stopRecording() {
        if (!isRecording) return

        try {
            mediaRecorder.stop()
            isRecording = false
            isPaused = false
            startPreview() // 重新开始预览
            saveVideoToLocalFolder() // 保存视频到本地文件夹
        } catch (e: RuntimeException) {
            Toast.makeText(requireContext(), "停止录制失败，请确保录制正在进行且已经录制了一段时间。", Toast.LENGTH_SHORT).show()
        } finally {
            mediaRecorder.reset()
            updateButtonStates()
        }
    }

    private fun saveVideoToLocalFolder() {
        Log.d("Camera", "Save video to local folder")
        val videoFileName = "video_${System.currentTimeMillis()}.mp4"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, videoFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/MediaPlayerDemo") // YourAppFolder 是你的应用的文件夹名称
        }

        val contentResolver = requireContext().contentResolver
        val videoUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
        try {
            val tempVideoFile = File(requireContext().externalCacheDir, "video.mp4") // 这是临时视频文件的路径
            videoUri?.let { uri ->
                contentResolver.openOutputStream(uri).use { outputStream ->
                    val inputStream = FileInputStream(tempVideoFile)
                    inputStream.copyTo(outputStream!!)
                    inputStream.close()
                }
                // 删除临时文件
                tempVideoFile.delete()
                Toast.makeText(requireContext(), "视频已保存到: $videoFileName", Toast.LENGTH_LONG).show()
            } ?: throw IOException("无法创建媒体存储记录")
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "保存视频失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateButtonStates() {
        startButton.isEnabled = !isRecording || isPaused
        pauseButton.isEnabled = isRecording && !isPaused
        resumeButton.isEnabled = isRecording && isPaused
        stopButton.isEnabled = isRecording
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraDevice?.close()
        mediaRecorder.release()
    }
}

