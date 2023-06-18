package com.ab.streamverse.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.provider.MediaStore.Audio
import android.provider.Settings
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.ab.streamverse.R
import com.ab.streamverse.databinding.CustomControlPlayerBinding
import com.jakewharton.rxbinding.view.clicks
import rx.android.schedulers.AndroidSchedulers
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
class CustomPlayerControlView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: CustomControlPlayerBinding
    private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var initialVolume = 0
    private var initialBrightness = 0
    private val gestureDetector: GestureDetector
    private var previousVolume : Int = 0


    init {
        val inflater = LayoutInflater.from(context)
        binding = CustomControlPlayerBinding.inflate(inflater, this, true)
        gestureDetector = GestureDetector(context, VolumeGestureListener(this.height))

        controlVolumeGesture()
        handleMuteAudio()
        handleOrientation()
//        binding.brightnessControlLayout.setOnTouchListener { view, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    initialBrightness = getBrightness()
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    val delta = event.y - view.y
//                    val maxBrightness = 255
//                    val deltaBrightness = (delta / view.height * maxBrightness).toInt()
//
//                    val newBrightness = initialBrightness - deltaBrightness
//                    val clampedBrightness = newBrightness.coerceIn(0, maxBrightness)
//
//                    setBrightness(clampedBrightness)
//                    binding.brightnessSeekBar.progress = clampedBrightness
//                }
//            }
//            true
//        }

        setupVolumeSeekBar()
        setupBrightnessSeekBar()
    }


    private fun handleOrientation(){
        binding.changeOrientation.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val currentOrientation = resources.configuration.orientation
                val activity = context as Activity
                if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    binding.changeOrientation.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_expand))
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    binding.changeOrientation.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_compress))
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }

            }
    }

    private fun handleMuteAudio() {
        binding.muteAudio.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) // stream music to get the media volume
                if(currentVolume > 0) {
                    previousVolume = currentVolume
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI)
                    binding.muteAudio.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_mute))
                } else {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, AudioManager.FLAG_SHOW_UI)
                    binding.muteAudio.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_speaker))
                }

            }
    }

    private fun controlVolumeGesture(){
        binding.volumeControlLayout.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }


    private fun setupVolumeSeekBar() {
        binding.volumeSeekBar.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        binding.volumeSeekBar.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    private fun setupBrightnessSeekBar() {
        binding.brightnessSeekBar.max = 255
        binding.brightnessSeekBar.progress = getBrightness()
    }

    private fun getBrightness(): Int {
        return try {
            Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
            0
        }
    }

    private fun setBrightness(brightness: Int) {
        val layoutParams = (context as Activity).window.attributes
        layoutParams.screenBrightness = brightness / 255f
        (context as Activity).window.attributes = layoutParams
    }

    fun getBinding() : CustomControlPlayerBinding {
        return binding
    }

    inner class VolumeGestureListener(private val playerHeight : Int) : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            initialVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            val distanceThreshold = 10
            if (abs(distanceY) > distanceThreshold) {
                adjustVolume(distanceY)
            }
            return true
        }

        private fun adjustVolume(distanceY: Float) {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val scrollPercentage = abs(distanceY) / playerHeight
            val volumeAdjustment = (scrollPercentage * maxVolume).toInt()
            val newVolume = if(distanceY < 0) currentVolume + volumeAdjustment else currentVolume - volumeAdjustment
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_SHOW_UI)
        }
    }
}
