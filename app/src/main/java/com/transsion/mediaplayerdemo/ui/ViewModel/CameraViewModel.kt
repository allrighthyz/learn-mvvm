package com.transsion.mediaplayerdemo.ui.ViewModel

import android.Manifest
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.media.MediaRecorder
import android.os.Environment
import android.provider.MediaStore
import android.view.Surface
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.transsion.mediaplayerdemo.medio.MediaRecorderHelper
import com.transsion.mediaplayerdemo.permission.PermissionRequest
import com.transsion.mediaplayerdemo.permission.PermissionsHelper
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    private var previewSurface: Surface? = null
    val isRecording = MutableLiveData<Boolean>()
    val isPaused = MutableLiveData<Boolean>()
    val startButtonEnabled = MutableLiveData<Boolean>()
    val pauseButtonEnabled = MutableLiveData<Boolean>()
    val resumeButtonEnabled = MutableLiveData<Boolean>()
    val stopButtonEnabled = MutableLiveData<Boolean>()

    private val cameraManager: CameraManager = application.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var cameraDevice: CameraDevice? = null
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var currentVideoPath: String
    private var mediaRecorderPrepared = false
    val cameraAndAudioPermissionRequest = PermissionRequest(
        permissions = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        ),
        requestCode = 1,
        rationaleTitle = "需要相机和麦克风权限",
        rationaleMessage = "此应用需要相机和麦克风权限来录制视频和音频。请授予权限。"
    )

    init {
        isRecording.value = false
        isPaused.value = false
        startButtonEnabled.value = false
        pauseButtonEnabled.value = false
        resumeButtonEnabled.value = false
        stopButtonEnabled.value = false
    }

    // 初始化相机
    fun initializeCamera(fragment: Fragment, surface: Surface) {
        previewSurface = surface
        if (!PermissionsHelper.hasPermissions(fragment.requireContext(), *cameraAndAudioPermissionRequest.permissions)) {
            PermissionsHelper.requestPermissions(fragment, cameraAndAudioPermissionRequest)
            return
        }
        openCamera()
    }

    // 打开相机
    private fun openCamera(stateCallback: CameraDevice.StateCallback? = null) {
        try {
            val cameraId = cameraManager.cameraIdList.first {
                cameraManager.getCameraCharacteristics(it).get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
            }

            if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return
            }

            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    // 使用保存的Surface参数
                    previewSurface?.let {
                        startPreview(it)
                    }
                    startButtonEnabled.value = true
                    stopButtonEnabled.value = true
                    stateCallback?.onOpened(camera)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                    cameraDevice = null
                    stateCallback?.onOpened(camera)
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    camera.close()
                    cameraDevice = null
                    stateCallback?.onError(camera, error)
                }
            }, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    // 开始预览
    fun startPreview(surface: Surface) {
        if (cameraDevice == null) return

        try {
            val previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                addTarget(surface)
            }
            cameraDevice!!.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    cameraCaptureSession = session
                    previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
                    cameraCaptureSession.setRepeatingRequest(previewRequestBuilder.build(), null, null)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    showToast("Camera preview configuration failed")
                }
            }, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * 开始视频录制
     * @param surface 用于预览的Surface对象
     */
    fun startRecording(surface: Surface) {
        // 如果已经在录制，则直接返回
        if (isRecording.value == true) return

        // 如果相机设备为空，则打开相机
        if (cameraDevice == null) {
            // 打开相机并设置回调
            openCamera(object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    // 相机打开时，初始化相机设备
                    cameraDevice = camera
                    // 开始预览
                    startPreview(surface)
                    // 准备MediaRecorder
                    prepareMediaRecorder()
                    // 递归调用startRecording以继续录制
                    startRecording(surface)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    // 相机断开连接时的处理
                    showToast("Camera disconnected")
                    camera.close()
                    cameraDevice = null
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    // 相机错误时的处理
                    showToast("Camera error: $error")
                    camera.close()
                    cameraDevice = null
                }
            })
            return
        }

        // 如果MediaRecorder未准备好，则准备MediaRecorder
        if (!mediaRecorderPrepared) {
            prepareMediaRecorder()
            // 如果准备失败，则直接返回
            if (!mediaRecorderPrepared) return
        }

        // 如果相机设备不为空，则开始配置录制
        cameraDevice?.let { camera ->
            // 获取MediaRecorder的录制Surface
            val recordingSurface = mediaRecorder.surface

            try {
                // 创建录制请求的捕获请求构建器
                val captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_RECORD).apply {
                    addTarget(surface) // 添加预览Surface
                    addTarget(recordingSurface) // 添加录制Surface
                }

                // 创建捕获会话
                camera.createCaptureSession(listOf(surface, recordingSurface), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        // 捕获会话配置成功时的处理
                        cameraCaptureSession = session
                        // 设置重复请求以开始录制
                        session.setRepeatingRequest(captureRequestBuilder.build(), null, null)
                        // 开始MediaRecorder
                        mediaRecorder.start()
                        // 更新录制状态
                        isRecording.value = true
                        isPaused.value = false
                        updateButtonStates()
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        // 捕获会话配置失败时的处理
                        showToast("Camera recording configuration failed")
                    }
                }, null)
            } catch (e: CameraAccessException) {
                // 捕获相机访问异常并打印堆栈跟踪
                e.printStackTrace()
            }
        } ?: showToast("Camera is not initialized") // 如果相机未初始化，则显示Toast消息
    }

    // 准备MediaRecorder
    private fun prepareMediaRecorder() {
        currentVideoPath = getOutputMediaFile()
        mediaRecorder = MediaRecorderHelper.prepareMediaRecorder(
            context = getApplication(),
            audioSource = MediaRecorder.AudioSource.MIC,
            videoSource = MediaRecorder.VideoSource.SURFACE,
            outputFormat = MediaRecorder.OutputFormat.MPEG_4,
            outputFile = currentVideoPath,
            videoEncodingBitRate = 10000000,
            videoFrameRate = 30,
            videoSizeWidth = 1920,
            videoSizeHeight = 1080,
            videoEncoder = MediaRecorder.VideoEncoder.H264,
            audioEncoder = MediaRecorder.AudioEncoder.AAC,
            orientationHint = 90
        )
        mediaRecorderPrepared = true
    }

    // 获取输出媒体文件路径
    private fun getOutputMediaFile(): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val mediaFile = File(getApplication<Application>().externalCacheDir, "VID_$timestamp.mp4")
        mediaFile.parentFile?.let {
            if (!it.exists()) {
                it.mkdirs()
            }
        }
        return mediaFile.absolutePath ?: throw IOException("无法创建媒体文件路径")
    }
    // 暂停/恢复录制
    fun pauseRecording() {
        if (isRecording.value == true && isPaused.value == false) {
            try {
                mediaRecorder.pause()
                isPaused.value = true
                updateButtonStates()
            } catch (e: IllegalStateException) {
                showToast("暂停录制失败，请确保录制正在进行。")
            }
        }
    }
    // 恢复录制
    fun resumeRecording() {
        if (isRecording.value == true && isPaused.value == true) {
            try {
                mediaRecorder.resume()
                isPaused.value = false
                updateButtonStates()
            } catch (e: IllegalStateException) {
                showToast("恢复录制失败，请确保录制已暂停。")
            }
        }
    }
    // 停止录制
    fun stopRecording() {
        if (isRecording.value == true) {
            try {
                mediaRecorder.stop()
            } catch (e: RuntimeException) {
                e.printStackTrace()
            } finally {
                mediaRecorder.reset()
                mediaRecorder.release()
                mediaRecorderPrepared = false
                isRecording.value = false
                isPaused.value = false
                updateButtonStates()
                saveVideoToLocalFolder()
            }
        }
    }
    // 保存视频到本地文件夹
    private fun saveVideoToLocalFolder() {
        // 获取当前视频的路径，如果为空则返回
        if (currentVideoPath.isEmpty()) {
            showToast("视频路径无效")
            return
        }
        // 生成一个基于当前时间戳的唯一视频文件名
        val videoFileName = "video_${System.currentTimeMillis()}.mp4"

        // 创建 ContentValues 来描述视频文件
        val contentValues = ContentValues().apply {
            // 显示名称
            put(MediaStore.MediaColumns.DISPLAY_NAME, videoFileName)
            // MIME 类型为 video/mp4
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            // 相对路径
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/MediaPlayerDemo")
        }

        // 从应用上下文获取内容解析器
        val contentResolver = getApplication<Application>().contentResolver

        // 在 MediaStore 中插入一个新视频并获取其 URI
        val videoUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            // 使用当前视频路径获取临时视频文件
            val tempVideoFile = File(currentVideoPath)

            // 如果视频 URI 不为 null，则继续保存视频
            videoUri?.let { uri ->
                // 打开视频 URI 的输出流
                contentResolver.openOutputStream(uri).use { outputStream ->
                    // 打开临时视频文件的输入流
                    FileInputStream(tempVideoFile).use { inputStream ->
                        // 将输入流的内容复制到输出流
                        inputStream.copyTo(outputStream!!)
                    }
                }

                // 如果临时视频文件存在，则将其删除
                if (tempVideoFile.exists()) {
                    tempVideoFile.delete()
                }

                showToast("视频已保存到: $videoFileName")
            } ?: throw IOException("无法创建媒体存储记录")
        } catch (e: IOException) {
            showToast("保存视频失败: ${e.message}")
        }
    }

    // 更新按钮状态
    private fun updateButtonStates() {
        startButtonEnabled.value = isRecording.value == false || isPaused.value == true
        pauseButtonEnabled.value = isRecording.value == true && isPaused.value == false
        resumeButtonEnabled.value = isRecording.value == true && isPaused.value == true
        stopButtonEnabled.value = isRecording.value == true
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }
}


