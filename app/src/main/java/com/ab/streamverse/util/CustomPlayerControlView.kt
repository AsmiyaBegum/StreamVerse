package com.ab.streamverse.util

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.AudioManager
import android.os.Handler
import android.provider.Settings
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.ab.streamverse.R
import com.ab.streamverse.databinding.CustomControlPlayerBinding
import com.ab.streamverse.util.Utils.showVisibility
import com.jakewharton.rxbinding.view.clicks
import rx.Observable
import rx.android.schedulers.AndroidSchedulers

class CustomPlayerControlView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: CustomControlPlayerBinding
    private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var previousVolume : Int = 0
    private var screenLocked : Boolean = false

    init {
        val inflater = LayoutInflater.from(context)
        binding = CustomControlPlayerBinding.inflate(inflater, this, true)
        handleMuteAudio()
        handleOrientation()
        handleLockScreen()
        handleVolumeSeekBar()
        handleBrightnessSeekBar()
    }

    private fun updateSoundProgress(progress : Int){
        binding.soundValueText.text = progress.toString()
        val soundImage = when {
            progress == 0 -> ContextCompat.getDrawable(context, R.drawable.ic_volume_zero)
            progress < 50 -> ContextCompat.getDrawable(context, R.drawable.ic_volume_fifty)
            else -> ContextCompat.getDrawable(context,R.drawable.ic_speaker)
        }
        binding.speakerIconSeeker.setImageDrawable(soundImage)
    }

    private fun handleVolumeSeekBar() {
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolumePercent = ((currentVolume.toDouble() / maxVolume.toDouble()) * 100).toInt()
        binding.soundSeekBar.progress = currentVolumePercent
        updateSoundProgress(currentVolumePercent)
        binding.soundSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    updateSoundProgress(progress)
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ((progress.toDouble() * maxVolume.toDouble())/100).toInt(), 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun updateBrightnessProgress(progress: Int) {
        binding.brightnessValueText.text = progress.toString()
        val brightnessImage = when {
            progress == 0 -> ContextCompat.getDrawable(context, R.drawable.ic_brightness_zero)
            progress < 50 -> ContextCompat.getDrawable(context, R.drawable.ic_brightness_fifty)
            else -> ContextCompat.getDrawable(context,R.drawable.ic_brightness)
        }
        binding.brightnessSeekerIcon.setImageDrawable(brightnessImage)
    }

    private fun handleBrightnessSeekBar(){
        val currentBrightness = getCurrentBrightnessPercentage()
        binding.brightnessSeekBar.progress = currentBrightness
        updateBrightnessProgress(currentBrightness)
        binding.brightnessSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateBrightnessProgress(progress)
                setBrightness(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun handleLockScreen() {
        binding.lockIconLayout.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleWhenScreenLockEnabled(true)
            }

        binding.screenLockedIcon.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleWhenScreenLockEnabled(false)
            }
    }

    private fun handleWhenScreenLockEnabled(enabled : Boolean) {
        if(enabled) {
            handleScreenLockedTouch()
        } else {
            showLockedScreenInfoLayout(false)
        }
        showLayoutOptions(!enabled)
        screenLocked = enabled
    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(screenLocked) {
            handleScreenLockedTouch()
            return true
        } else {
            handleLayoutTimerBasedVisibility()
        }
        return super.onTouchEvent(event)
    }

    private fun handleLayoutTimerBasedVisibility() {
        if(binding.middleLayout.visibility == View.GONE) {
            val handler = Handler()
            val runnable = Runnable {
                    showLayoutOptions(false)
            }
            handler.postDelayed(runnable,5000)
            showLayoutOptions(true)
        }
    }

    private fun showLayoutOptions(show: Boolean) {
        binding.backButton.showVisibility(show && isFullScreen())
        binding.movieName.showVisibility(show && isFullScreen())
        binding.seekBarLayout.showVisibility(show)
        binding.middleLayout.showVisibility(show)
        binding.volumeControlLayout.showVisibility(show && isFullScreen())
        binding.brightnessControlLayout.showVisibility(show && isFullScreen())
        binding.footerFullScreenLayout.showVisibility(show && isFullScreen())
    }

    private fun handleScreenLockedTouch() {
        if(binding.lockScreenLayout.visibility == View.GONE) {
            val handler = Handler()
            val runnable = Runnable {
                showLockedScreenInfoLayout(false)
            }
            handler.postDelayed(runnable,5000)
            showLockedScreenInfoLayout(true)
        }
    }

    private fun showLockedScreenInfoLayout(show: Boolean) {
        binding.lockScreenLayout.showVisibility(show)
    }

    private fun isFullScreen() : Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }


    private fun handleOrientation() {
        Observable.merge(binding.changeOrientation.clicks(),binding.changeOrientationFullScreenIcon.clicks(),binding.changeOrientationFullScreenIcon.clicks())
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
                if(binding.muteAudio.contentDescription == context.getString(R.string.speaker)) {
                    previousVolume = currentVolume
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI)
                    binding.muteAudio.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_mute))
                    binding.muteAudio.contentDescription = context.getString(R.string.mute)
                } else {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, AudioManager.FLAG_SHOW_UI)
                    binding.muteAudio.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_speaker))
                    binding.muteAudio.contentDescription = context.getString(R.string.speaker)
                }
            }
    }


    private fun getCurrentBrightnessPercentage(): Int {
        val contentResolver = context.contentResolver
        val currentBrightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        val maxBrightness = 255 // The maximum brightness level (can vary on different devices)
        val brightnessPercentage = (currentBrightness.toFloat() / maxBrightness.toFloat()) * 100
        return brightnessPercentage.toInt()
    }

    private fun setBrightness(brightness: Int) {
        val brightnessValue = brightness.toFloat() / 100F
        val layoutParams = (context as Activity).window.attributes
        layoutParams.screenBrightness = brightnessValue
        (context as Activity).window.attributes = layoutParams
    }

    fun getBinding() : CustomControlPlayerBinding {
        return binding
    }

    fun showLoader(show : Boolean) {
        binding.loader.showVisibility(show)
        if(show) {
            showLayoutOptions(false)
        }
    }
}
