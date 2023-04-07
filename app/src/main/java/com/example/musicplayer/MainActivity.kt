package com.example.musicplayer

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.example.musicplayer.databinding.ActivityMainBinding
import com.google.android.material.slider.Slider
import java.sql.Time
import java.time.Duration
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var mediaPlayer: MediaPlayer
    lateinit var timer: Timer
    var isPlaying = true
    var isUserChanging = false
    private var isMute = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareMusic()


        binding.btnPlayPause.setOnClickListener { configureMusic() }
        binding.btnVolume.setOnClickListener { configureVolume() }
        binding.btnBackward.setOnClickListener { goBackwardMusic() }
        binding.btnForward.setOnClickListener { goForwardMusic() }

        binding.mainSlider.addOnChangeListener { slider, value, fromUser ->
            binding.txtStart.text = convertMillsToString(value.toLong())
            isUserChanging = fromUser
        }

        binding.mainSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
            }

            override fun onStopTrackingTouch(slider: Slider) {
                mediaPlayer.seekTo(slider.value.toInt())
                binding.mainSlider.value = mediaPlayer.currentPosition.toFloat()
            }

        })


    }


    /********************************************************************************/
    private fun prepareMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.mymusic)
        mediaPlayer.start()
        isPlaying = true
        binding.btnPlayPause.setImageResource(R.drawable.ic_pause)

        binding.mainSlider.valueTo = mediaPlayer.duration.toFloat()

        binding.txtEnd.text = convertMillsToString(mediaPlayer.duration.toLong())


        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (!isUserChanging) {
                        binding.mainSlider.value = mediaPlayer.currentPosition.toFloat()
                    }
                }

            }
        }, 1000, 1000)


    }

    //******************************************************************************
    private fun goForwardMusic() {
        mediaPlayer.seekTo(mediaPlayer.currentPosition + 15000)

    }
    //******************************************************************************

    private fun goBackwardMusic() {

        mediaPlayer.seekTo(mediaPlayer.currentPosition - 15000)
    }
    //******************************************************************************


    private fun configureVolume() {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        if (isMute) {
            audioManager.adjustVolume(AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI)
            binding.btnVolume.setImageResource(R.drawable.ic_volume_on)
            isMute = false
        } else {
            audioManager.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI)
            binding.btnVolume.setImageResource(R.drawable.ic_volume_off)
            isMute = true
        }
    }
    //******************************************************************************


    private fun configureMusic() {
        if (isPlaying) {
            mediaPlayer.pause()
            binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            isPlaying = false

        } else {
            mediaPlayer.start()
            binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
            isPlaying = true
        }
    }

    //******************************************************************************
    private fun convertMillsToString(duration: Long): String {
        val second = duration / 1000 % 60
        val minute = duration / (1000 * 60) % 60
        return java.lang.String.format(Locale.US, "%02d:%02d", minute, second)

    }
    //******************************************************************************

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        mediaPlayer.release()
    }


}