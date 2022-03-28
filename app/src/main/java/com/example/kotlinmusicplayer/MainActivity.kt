package com.example.kotlinmusicplayer

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var mp: MediaPlayer
    private var totalTime: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val volumeBar = findViewById<SeekBar>(R.id.volumeBar)
        val progressBar = findViewById<SeekBar>(R.id.positionBar)
        mp = MediaPlayer.create(this, R.raw.audio)
        mp.isLooping = true
        mp.setVolume(0.5f, 0.5f)
        totalTime = mp.duration
        volumeBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        val volumeNum = progress / 100.0f
                        mp.setVolume(volumeNum, volumeNum)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            }
        )
        progressBar.max = totalTime
        progressBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        mp.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            }
        )
        Thread(Runnable {
            while (mp != null) {
                try {
                    val msg = Message()
                    msg.what = mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {

                }
            }
        }).start()
    }

    @SuppressLint("HandlerLeak")
    var handler = object : Handler() {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            val currentPosition = msg.what

            val progressBar = findViewById<SeekBar>(R.id.positionBar)
            val elapedTimeLabel = findViewById<TextView>(R.id.elapsedTimeLabel)
            val remainingTimeLabel = findViewById<TextView>(R.id.remainingTimeLabel)

            progressBar.progress = currentPosition

            val elapsedTime = createTimeLabel(currentPosition)
            elapedTimeLabel.text = elapsedTime

            val remainingTime = createTimeLabel(totalTime - currentPosition)
            remainingTimeLabel.text = "-$remainingTime"
        }
    }

    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        val min = time / 1000 / 60
        val sec = time / 1000 % 60
        timeLabel = "$min:"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec
        return timeLabel
    }

    fun playBtnClick(v: View) {
        val playBtn = findViewById<Button>(R.id.playBtn)
        if (mp.isPlaying) {
            mp.pause()
            playBtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
        } else {
            mp.start()
            playBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24)
        }
    }

}