package com.transsion.mediaplayerdemo.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.transsion.mediaplayerdemo.R
import com.transsion.mediaplayerdemo.databinding.FragmentVedioBinding
import com.transsion.mediaplayerdemo.ui.viewModel.VideoViewModel

class VideoFragment : Fragment() {

    private lateinit var binding: FragmentVedioBinding
    private val videoViewModel: VideoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_vedio, container, false)
        binding.viewModel = videoViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val videoUri: Uri = Uri.parse("android.resource://" + requireActivity().packageName + "/" + R.raw.vedio)
        binding.videoView.setVideoURI(videoUri)

        videoViewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            if (isPlaying) {
                binding.videoView.start()
            } else {
                binding.videoView.pause()
            }
        }

        return binding.root
    }
}
