package com.transsion.mediaplayerdemo.ui.fragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.VideoView
import com.transsion.mediaplayerdemo.R

class VedioFragment : Fragment() {

    private lateinit var videoView: VideoView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_vedio, container, false)

        videoView = rootView.findViewById(R.id.videoView)

        // 设置视频路径
        val videoUri: Uri = Uri.parse("android.resource://" + requireActivity().packageName + "/" + R.raw.vedio)
        videoView.setVideoURI(videoUri)

        // 播放按钮
        rootView.findViewById<Button>(R.id.playButton).setOnClickListener {
            if (!videoView.isPlaying) {
                videoView.start() // 开始播放
            }
        }

        // 暂停按钮
        rootView.findViewById<Button>(R.id.pauseButton).setOnClickListener {
            if (videoView.isPlaying) {
                videoView.pause() // 暂停播放
            }
        }

        // 停止按钮
        rootView.findViewById<Button>(R.id.stopButton).setOnClickListener {
            videoView.stopPlayback() // 停止播放并释放资源
            videoView.setVideoURI(videoUri) // 重新设置视频URI以准备下一次播放
        }

        return rootView
    }

}