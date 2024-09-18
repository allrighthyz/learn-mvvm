package com.transsion.mediaplayerdemo.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.transsion.mediaplayerdemo.ViewModel.CameraViewModel
import com.transsion.mediaplayerdemo.databinding.FragmentNextBinding

class NextFragment : Fragment() {
    // 获取 ViewModel
    private lateinit var viewModel: CameraViewModel
    // 获取 ViewBinding
    private var _binding: FragmentNextBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNextBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(CameraViewModel::class.java)

        // 获取 SurfaceView
        val surfaceView = binding.surfaceView
        // 获取按钮
        val startButton = binding.startButton
        val pauseButton = binding.pauseButton
        val resumeButton = binding.resumeButton
        val stopButton = binding.stopButton

        // 监听按钮点击事件
        startButton.setOnClickListener { viewModel.startRecording(surfaceView.holder.surface) }
        pauseButton.setOnClickListener { viewModel.pauseRecording() }
        resumeButton.setOnClickListener { viewModel.resumeRecording() }
        stopButton.setOnClickListener { viewModel.stopRecording() }

        // 观察录制状态变化
        viewModel.isRecording.observe(viewLifecycleOwner) {
            updateButtonStates()
        }
        // 观察暂停状态变化
        viewModel.isPaused.observe(viewLifecycleOwner) {
            updateButtonStates()
        }
        // 观察开始状态变化
        viewModel.startButtonEnabled.observe(viewLifecycleOwner) {
            startButton.isEnabled = it
        }
        // 观察暂停状态变化
        viewModel.pauseButtonEnabled.observe(viewLifecycleOwner) {
            pauseButton.isEnabled = it
        }
        // 观察恢复状态变化
        viewModel.resumeButtonEnabled.observe(viewLifecycleOwner) {
            resumeButton.isEnabled = it
        }
        // 观察停止状态变化
        viewModel.stopButtonEnabled.observe(viewLifecycleOwner) {
            stopButton.isEnabled = it
        }

        // 初始化相机并传递 Surface
        viewModel.initializeCamera(this, surfaceView.holder.surface)
    }

    // 请求相机和麦克风权限
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            // 相机和麦克风权限请求码
            viewModel.cameraAndAudioPermissionRequest.requestCode -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    val surfaceView = binding.surfaceView
                    viewModel.initializeCamera(this, surfaceView.holder.surface)
                } else {
                    showToast("相机和麦克风权限被拒绝，无法录制视频和音频")
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    // 更新按钮状态
    private fun updateButtonStates() {
        binding.startButton.isEnabled = viewModel.startButtonEnabled.value ?: false
        binding.pauseButton.isEnabled = viewModel.pauseButtonEnabled.value ?: false
        binding.resumeButton.isEnabled = viewModel.resumeButtonEnabled.value ?: false
        binding.stopButton.isEnabled = viewModel.stopButtonEnabled.value ?: false
    }

    // 显示 Toast 提示
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // 销毁视图时释放资源
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

//class NextFragment : Fragment() {
//
//    private val PERMISSIONS_REQUEST_CODE = 1
//
//    private lateinit var surfaceView: SurfaceView
//    private lateinit var startButton: Button
//    private lateinit var pauseButton: Button
//    private lateinit var resumeButton: Button
//    private lateinit var stopButton: Button
//
//    private lateinit var cameraManager: CameraManager
//    private var cameraDevice: CameraDevice? = null
//    private lateinit var mediaRecorder: MediaRecorder
//    private lateinit var cameraCaptureSession: CameraCaptureSession
//    private lateinit var currentVideoPath: String
//    private var mediaRecorderPrepared = false
//
//    private var isRecording = false
//    private var isPaused = false
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_next, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        surfaceView = view.findViewById(R.id.surfaceView)
//        startButton = view.findViewById(R.id.startButton)
//        pauseButton = view.findViewById(R.id.pauseButton)
//        resumeButton = view.findViewById(R.id.resumeButton)
//        stopButton = view.findViewById(R.id.stopButton)
//
//        setButtonState(false)
//
//        cameraManager = requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
//
//        startButton.setOnClickListener { startRecording() }
//        pauseButton.setOnClickListener { pauseRecording() }
//        resumeButton.setOnClickListener { resumeRecording() }
//        stopButton.setOnClickListener { stopRecording() }
//
//        initializeCamera()
//    }
//
//    @Deprecated("Deprecated in Java")
//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
//    ) {
//        when (requestCode) {
//            cameraAndAudioPermissionRequest.requestCode -> {
//                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
//                    openCamera()
//                } else {
//                    showToast("相机和麦克风权限被拒绝，无法录制视频和音频")
//                }
//            }
//            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        }
//    }
//
//    private fun openCamera(stateCallback: CameraDevice.StateCallback? = null) {
//        try {
//            val cameraId = cameraManager.cameraIdList.first {
//                cameraManager.getCameraCharacteristics(it).get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
//            }
//
//            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                return
//            }
//
//            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
//                override fun onOpened(camera: CameraDevice) {
//                    cameraDevice = camera
//                    startPreview()
//                    setButtonState(true)
//                    stateCallback?.onOpened(camera)
//                }
//
//                override fun onDisconnected(camera: CameraDevice) {
//                    camera.close()
//                    cameraDevice = null
//                    stateCallback?.onOpened(camera)
//                }
//
//                override fun onError(camera: CameraDevice, error: Int) {
//                    camera.close()
//                    cameraDevice = null
//                    stateCallback?.onError(camera, error)
//                }
//            }, null)
//        } catch (e: CameraAccessException) {
//            e.printStackTrace()
//        }
//    }
//
//    private val cameraAndAudioPermissionRequest = PermissionRequest(
//        permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
//        requestCode = PERMISSIONS_REQUEST_CODE,
//        rationaleTitle = "需要相机和麦克风权限",
//        rationaleMessage = "此应用需要相机和麦克风权限来录制视频和音频。请授予权限。"
//    )
//
//    private fun initializeCamera() {
//        if (!PermissionsHelper.hasPermissions(requireContext(), *cameraAndAudioPermissionRequest.permissions)) {
//            PermissionsHelper.requestPermissions(this, cameraAndAudioPermissionRequest)
//            return
//        }
//        openCamera()
//    }
//
//    private fun startPreview() {
//        if (cameraDevice == null) return
//
//        val previewSurface = surfaceView.holder.surface
//
//        try {
//            val previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
//                addTarget(previewSurface)
//            }
//            cameraDevice!!.createCaptureSession(listOf(previewSurface), object : CameraCaptureSession.StateCallback() {
//                override fun onConfigured(session: CameraCaptureSession) {
//                    cameraCaptureSession = session
//                    previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
//                    cameraCaptureSession.setRepeatingRequest(previewRequestBuilder.build(), null, null)
//                }
//
//                override fun onConfigureFailed(session: CameraCaptureSession) {
//                    showToast("Camera preview configuration failed")
//                }
//            }, null)
//        } catch (e: CameraAccessException) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun startRecording() {
//        if (isRecording) return
//
//        if (cameraDevice == null) {
//            openCamera(object : CameraDevice.StateCallback() {
//                override fun onOpened(camera: CameraDevice) {
//                    cameraDevice = camera
//                    startPreview()
//                    prepareMediaRecorder()
//                    startRecording()
//                }
//
//                override fun onDisconnected(camera: CameraDevice) {}
//                override fun onError(camera: CameraDevice, error: Int) {}
//            })
//            return
//        }
//
//        if (!mediaRecorderPrepared) {
//            prepareMediaRecorder()
//            if (!mediaRecorderPrepared) return
//        }
//
//        cameraDevice?.let { camera ->
//            val surface = surfaceView.holder.surface
//            val recordingSurface = mediaRecorder.surface
//
//            try {
//                val captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_RECORD).apply {
//                    addTarget(surface)
//                    addTarget(recordingSurface)
//                }
//
//                camera.createCaptureSession(listOf(surface, recordingSurface), object : CameraCaptureSession.StateCallback() {
//                    override fun onConfigured(session: CameraCaptureSession) {
//                        cameraCaptureSession = session
//                        session.setRepeatingRequest(captureRequestBuilder.build(), null, null)
//                        mediaRecorder.start()
//                        isRecording = true
//                        isPaused = false
//                        updateButtonStates()
//                    }
//
//                    override fun onConfigureFailed(session: CameraCaptureSession) {
//                        showToast("Camera recording configuration failed")
//                    }
//                }, null)
//            } catch (e: CameraAccessException) {
//                e.printStackTrace()
//            }
//        } ?: showToast("Camera is not initialized")
//    }
//
//    private fun prepareMediaRecorder() {
//        currentVideoPath = getOutputMediaFile()
//        mediaRecorder = MediaRecorderHelper.prepareMediaRecorder(
//            context = requireContext(),
//            audioSource = MediaRecorder.AudioSource.MIC,
//            videoSource = MediaRecorder.VideoSource.SURFACE,
//            outputFormat = MediaRecorder.OutputFormat.MPEG_4,
//            outputFile = currentVideoPath,
//            videoEncodingBitRate = 10000000,
//            videoFrameRate = 30,
//            videoSizeWidth = 1920,
//            videoSizeHeight = 1080,
//            videoEncoder = MediaRecorder.VideoEncoder.H264,
//            audioEncoder = MediaRecorder.AudioEncoder.AAC,
//            orientationHint = 90
//        )
//        mediaRecorderPrepared = true
//    }
//
//    private fun getOutputMediaFile(): String {
//        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        val mediaFile = File(requireContext().externalCacheDir, "VID_$timestamp.mp4")
//
//        // 确保目录存在
//        if (!mediaFile.parentFile?.exists()!!) {
//            mediaFile.parentFile?.mkdirs()
//        }
//
//        return mediaFile.absolutePath
//    }
//
//    private fun pauseRecording() {
//        if (!isRecording || isPaused) return
//
//        try {
//            mediaRecorder.pause()
//            isPaused = true
//            updateButtonStates()
//        } catch (e: IllegalStateException) {
//            showToast("暂停录制失败，请确保录制正在进行。")
//        }
//    }
//
//    private fun resumeRecording() {
//        if (!isRecording || !isPaused) return
//
//        try {
//            mediaRecorder.resume()
//            isPaused = false
//            updateButtonStates()
//        } catch (e: IllegalStateException) {
//            showToast("恢复录制失败，请确保录制已暂停。")
//        }
//    }
//
//    private fun stopRecording() {
//        if (isRecording) {
//            try {
//                mediaRecorder.stop()
//            } catch (e: RuntimeException) {
//                e.printStackTrace()
//            } finally {
//                mediaRecorder.reset()
//                mediaRecorder.release()
//                mediaRecorderPrepared = false
//                isRecording = false
//                isPaused = false
//                updateButtonStates()
//                saveVideoToLocalFolder()
//            }
//        }
//    }
//
//    private fun saveVideoToLocalFolder() {
//        val videoFileName = "video_${System.currentTimeMillis()}.mp4"
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, videoFileName)
//            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
//            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/MediaPlayerDemo")
//        }
//
//        val contentResolver = requireContext().contentResolver
//        val videoUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//        try {
//            val tempVideoFile = File(currentVideoPath)
//            videoUri?.let { uri ->
//                contentResolver.openOutputStream(uri).use { outputStream ->
//                    FileInputStream(tempVideoFile).use { inputStream ->
//                        inputStream.copyTo(outputStream!!)
//                    }
//                }
//                if (tempVideoFile.exists()) {
//                    tempVideoFile.delete()
//                }
//                showToast("视频已保存到: $videoFileName")
//            } ?: throw IOException("无法创建媒体存储记录")
//        } catch (e: IOException) {
//            showToast("保存视频失败: ${e.message}")
//        }
//    }
//
//    private fun updateButtonStates() {
//        startButton.isEnabled = !isRecording || isPaused
//        pauseButton.isEnabled = isRecording && !isPaused
//        resumeButton.isEnabled = isRecording && isPaused
//        stopButton.isEnabled = isRecording
//    }
//
//    private fun setButtonState(enabled: Boolean) {
//        startButton.isEnabled = enabled
//        pauseButton.isEnabled = enabled
//        resumeButton.isEnabled = enabled
//        stopButton.isEnabled = enabled
//    }
//
//    private fun showToast(message: String) {
//        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        cameraCaptureSession.close()
//        cameraDevice?.close()
//        if (::mediaRecorder.isInitialized) {
//            mediaRecorder.release()
//        }
//    }
//}

