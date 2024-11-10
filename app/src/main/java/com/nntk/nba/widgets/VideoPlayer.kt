package com.nntk.nba.widgets

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ResourceUtils
import java.io.File

class VideoPlayer {

    companion object {
        @JvmStatic
        @OptIn(UnstableApi::class)
        fun initVideo(context: Context, bgVideo: PlayerView) {

            val bgMp4Path = PathUtils.getInternalAppDataPath() + File.separator + "nba_on_espn.mp4"
            ResourceUtils.copyFileFromRaw(R.raw.nba_on_espn, bgMp4Path)

            val player = ExoPlayer.Builder(context).build()
            val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(context)
            val videoSource: MediaSource =
                ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                    MediaItem.fromUri(Uri.parse(bgMp4Path))
                )
            player.setMediaSource(videoSource)
            player.prepare()
            bgVideo.controllerAutoShow = false
            bgVideo.player = player
            player.playWhenReady = true
            player.volume = 0f
            player.repeatMode = REPEAT_MODE_ONE
        }
    }

}