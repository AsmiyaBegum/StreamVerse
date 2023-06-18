package com.ab.streamverse.streamversepreview

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.ab.streamverse.R
import com.ab.streamverse.databinding.FragmentSecondBinding
import com.ab.streamverse.model.StreamDetails
import com.ab.streamverse.model.VideoDetails
import com.ab.streamverse.streamVerseHome.StreamDetailsAdapter
import com.ab.streamverse.streamVerseHome.StreamDetailsViewHolder
import com.ab.streamverse.streamVerseHome.StreamVerseViewModel
import com.ab.streamverse.util.Constants
import com.ab.streamverse.util.IOrientationChanged
import com.ab.streamverse.util.Utils.showVisibility
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.jakewharton.rxbinding.view.clicks
import rx.android.schedulers.AndroidSchedulers


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class PreviewFragment : Fragment(), StreamDetailsViewHolder.StreamDetailsDelegate,IOrientationChanged {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var movieKey : String = ""
    private lateinit var viewModel: StreamVerseViewModel

    private lateinit var player: ExoPlayer



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        movieKey = requireArguments().getString(Constants.MOVIE_KEY)?:""
        viewModel = ViewModelProvider(this).get(StreamVerseViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePlayer()
        viewModel.getVideoDetail(movieKey)
        viewModel.videoDetail.observe(viewLifecycleOwner){ videoDetail ->
            setUpVideoDetail(videoDetail)
            setupRecommendation(videoDetail.recommendationKey)
            setupPlayer(videoDetail.movieKey)
        }
    }

    private fun setUpVideoDetail(videoDetail : VideoDetails) {
        binding.videoName.text = videoDetail.movieName
        binding.videoYear.text = videoDetail.year
        binding.certificate.text = videoDetail.certificate
        binding.videoDuration.text = videoDetail.duration
        binding.videoDescription.text = videoDetail.description
        binding.director.text = getString(R.string.director,videoDetail.cast.directors.joinToString ( "," ))
        binding.writer.text = getString(R.string.writer,videoDetail.cast.writers.joinToString ( "," ))
        binding.starring.text = getString(R.string.starring,videoDetail.cast.actors.joinToString ( "," ))
        binding.genres.text = getString(R.string.genres,videoDetail.genres.joinToString(","))
    }

    private fun setupRecommendation(recommendationKey : String) {
        viewModel.getRecommendedVideoList(recommendationKey)
        viewModel.recommendedVideoList.observe(viewLifecycleOwner) { streamVideoDetails ->
            binding.recommendedList.layoutManager = GridLayoutManager(binding.root.context, 2)
            binding.recommendedList.adapter = StreamDetailsAdapter(streamVideoDetails,this)
        }
    }


    private fun setupPlayer(videoStreamURL : String){
        prepareMediaSource(videoStreamURL)
    }

    private fun startVideo(){
        startPlayback()
        binding.videoPlayer.player = player
        addCustomView()
        handleProgressAndSeekbar()
        handleForwardBackward()
    }

    private fun handleForwardBackward() {
        binding.customControlView.getBinding().forward.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                moveForward()
                updateSeekBar()
            }

        binding.customControlView.getBinding().backward.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleBackward()
                updateSeekBar()
            }

        binding.customControlView.getBinding().playPauseButton.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if(player.isPlaying) {
                    binding.customControlView.getBinding().playPauseButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play_button))
                    stopPlayback()
                } else {
                    binding.customControlView.getBinding().playPauseButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_pause_button))
                    startPlayback()
                }
            }
    }

    private fun handleBackward() {
        val currentPosition = player.currentPosition
        val newPosition = currentPosition - Constants.INC_DEC_DURATION
        if(newPosition < 0) {
            player.seekTo(0)
        } else {
            player.seekTo(newPosition)
        }
    }



    private fun moveForward() {
        val currentPosition = player.currentPosition
        val newPosition = currentPosition + Constants.INC_DEC_DURATION
        if(newPosition > player.duration){
            player.seekTo(player.duration)
        } else {
            player.seekTo(newPosition)
        }
    }

    private fun addCustomView() {
        // Remove the custom control view from its existing parent
        val customControlParent = binding.customControlView.parent as? ViewGroup
        customControlParent?.removeView(binding.customControlView)
        binding.videoPlayer.addView(binding.customControlView)
    }

    private fun updateSeekBar() {
        binding.customControlView.getBinding().seekBar.max = player.duration.toInt()
        startUpdatingSeekBar()
    }

    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            val currentPosition = player.currentPosition.toInt()
            binding.customControlView.getBinding().seekBar.progress = currentPosition
            binding.customControlView.getBinding().seekBar.postDelayed(this, 1000) // Update every 1 second
        }
    }

    private fun startUpdatingSeekBar() {
        binding.customControlView.getBinding().seekBar.post(updateSeekBarRunnable)
    }
    private fun stopUpdatingSeekBar() {
        binding.customControlView.getBinding().seekBar.removeCallbacks(updateSeekBarRunnable)
    }


    private fun showLoader(showLoader : Boolean) {
        binding.customControlView.getBinding().loader.showVisibility(showLoader)
    }

    private fun handleProgressAndSeekbar() {
        val seekBar = binding.customControlView.getBinding().seekBar

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                showLoader(true)
                if (fromUser) {
                    player.seekTo(progress.toLong()) // Enable automatic progress updates after dragging
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                stopPlayback() // Disable automatic progress updates while dragging
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                startPlayback() // Enable automatic progress updates after dragging
            }
        })

            val playerListener = object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        updateSeekBar()
                        showLoader(false)
                    } else {
                        stopUpdatingSeekBar()
                    }
                }
            }
            player.addListener(playerListener)


    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(requireContext()).build()
    }

    private fun prepareMediaSource(url : String) {
        val dataSourceFactory = DefaultDataSourceFactory(requireContext(), "user-agent")
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
        player.setMediaSource(mediaSource)
        player.prepare()
        startVideo()
    }

    override fun onResume() {
        startPlayback()
        super.onResume()
    }
    private fun startPlayback() {
        if(this::player.isInitialized){
            player.playWhenReady = true
        }
    }

    private fun stopPlayback() {
        if(this::player.isInitialized){
            player.playWhenReady = false
        }
    }

    override fun onPause() {
        stopPlayback()
        super.onPause()
    }

    override fun selectedStreamVideo(data: StreamDetails) {

    }

    override fun onOrientationChanged(orientation: Int) {
        binding.previewInfoDetailLayout.showVisibility(orientation == Configuration.ORIENTATION_PORTRAIT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}