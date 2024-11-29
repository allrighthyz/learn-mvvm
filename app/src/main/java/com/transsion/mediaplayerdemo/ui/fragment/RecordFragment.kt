package com.transsion.mediaplayerdemo.ui.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.transsion.mediaplayerdemo.ui.viewModel.CameraViewModel
import com.transsion.mediaplayerdemo.databinding.FragmentRecordBinding

class RecordFragment : Fragment() {
    // 获取 viewModel
    private lateinit var viewModel: CameraViewModel
    // 获取 ViewBinding
    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
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

