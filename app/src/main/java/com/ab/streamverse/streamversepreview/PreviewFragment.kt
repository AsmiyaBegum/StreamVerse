package com.ab.streamverse.streamversepreview

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ab.streamverse.MainActivity
import com.ab.streamverse.R
import com.ab.streamverse.databinding.PreviewFragmentBinding
import com.ab.streamverse.model.StreamDetails
import com.ab.streamverse.model.VideoDetails
import com.ab.streamverse.streamVerseHome.StreamDetailsAdapter
import com.ab.streamverse.streamVerseHome.StreamDetailsViewHolder
import com.ab.streamverse.streamVerseHome.StreamVerseViewModel
import com.ab.streamverse.util.Constants
import com.ab.streamverse.util.IOrientationChanged
import com.ab.streamverse.util.Utils.expandView
import com.ab.streamverse.util.Utils.showVisibility
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.jakewharton.rxbinding.view.clicks
import rx.Observable
import rx.android.schedulers.AndroidSchedulers


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class PreviewFragment : Fragment(), StreamDetailsViewHolder.StreamDetailsDelegate,IOrientationChanged {

    private var _binding: PreviewFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var movieKey : String = ""
    private lateinit var viewModel: StreamVerseViewModel
    private var videoDetails : VideoDetails = VideoDetails()
    private lateinit var player: ExoPlayer



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        handleBackPress()
        _binding = PreviewFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    private fun handleBackPress() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPress()
            }
        }

        // Add the callback to the OnBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        movieKey = requireArguments().getString(Constants.MOVIE_KEY)?:""
        viewModel = ViewModelProvider(this).get(StreamVerseViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePlayer()
        bind()
        viewModel.getVideoDetail(movieKey)
        viewModel.videoDetail.observe(viewLifecycleOwner){ videoDetail ->
            videoDetails = videoDetail
            setUpVideoDetail(videoDetail)
            setupRecommendation(videoDetail.recommendationKey)
            setupPlayer(videoDetail.trailerKey)
        }
    }

    private fun bind() {
        listOf(binding.genres,binding.director,binding.writer,binding.starring).expandView()
        binding.icPlusIcon.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val msg = if(binding.icPlusIcon.contentDescription == getString(R.string.add_to_my_list)){
                    binding.icPlusIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_tick))
                    binding.icPlusIcon.contentDescription = getString(R.string.remove_from_my_list)
                    getString(R.string.added_to_my_list)
                }else{
                    binding.icPlusIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_plus))
                    binding.icPlusIcon.contentDescription = getString(R.string.add_to_my_list)
                    getString(R.string.removed_from_my_list)
                }
                Toast.makeText(requireContext(),msg,Toast.LENGTH_SHORT).show()

                //implement add to list functionality
            }

        binding.icRateIcon.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if(binding.icRateIcon.contentDescription == getString(R.string.rate)){
                    binding.icRateIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_like))
                    binding.icRateIcon.contentDescription = getString(R.string.like)
                }else{
                    binding.icRateIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_rate))
                    binding.icRateIcon.contentDescription = getString(R.string.rate)
                }

                //implement rate functionality
            }

        binding.icShareIcon.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                shareVideoDetail(videoDetails)
            }

        binding.downloadButton.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Toast.makeText(requireContext(),getString(R.string.download_not_supported),Toast.LENGTH_SHORT).show()
            }

        binding.playButton.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.customControlView.getBinding().preview.showVisibility(false)
                prepareMediaSource(videoDetails.movieKey)
                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

        Observable.merge(binding.backButton.clicks(),binding.customControlView.getBinding().backButton.clicks())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                onBackPress()
            }

    }

    private fun shareVideoDetail(videoDetail: VideoDetails) {
        // share Video detail
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.check_out_link))
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.seen_on_streamverse,"\"${videoDetail.movieName}\"",videoDetail.movieKey))
        startActivity(Intent.createChooser(intent, getString(R.string.share_link_via)))
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
            binding.recommendedList.adapter = StreamDetailsAdapter(streamVideoDetails,this,false)
        }
    }


    private fun setupPlayer(videoStreamURL : String){
        prepareMediaSource(videoStreamURL)
    }

    private fun startVideo(){
        startPlayback()
        binding.videoPlayer.player = player
        addCustomView()
        setupMovieName()
        handleProgressAndSeekbar()
        handleForwardBackward()
    }

    private fun setupMovieName() {
        binding.customControlView.getBinding().movieName.text = videoDetails.movieName
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
                    stopPlayback()
                } else {
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
            binding.customControlView.getBinding().pendingDuration.showVisibility(isFullScreen())
            updatePendingDuration()
        }
    }

    private fun updatePendingDuration() {
        val totalDuration = player.duration
        val currentPosition = player.currentPosition
        val pendingDurationMillis = totalDuration - currentPosition
        val pendingDurationMinutes = pendingDurationMillis / (1000 * 60)
        val remainingSeconds = (pendingDurationMillis % (1000 * 60)) / 1000
        binding.customControlView.getBinding().pendingDuration.text = String.format("%02d:%02d", pendingDurationMinutes, remainingSeconds)
    }

    private fun startUpdatingSeekBar() {
        binding.customControlView.getBinding().seekBar.post(updateSeekBarRunnable)
    }
    private fun stopUpdatingSeekBar() {
        binding.customControlView.getBinding().seekBar.removeCallbacks(updateSeekBarRunnable)
    }


    private fun showLoader(showLoader : Boolean) {
        if(_binding != null){ // to avoid the crash if the fragment is destroyed
            binding.customControlView.showLoader(showLoader)
        }
    }

    private fun handleProgressAndSeekbar() {
        val seekBar = binding.customControlView.getBinding().seekBar

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
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
                    } else {
                        stopUpdatingSeekBar()
                    }
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if(_binding != null){
                        if(playbackState == Player.STATE_ENDED) {
                            showImageThumbnail(true)
                            Glide.with(binding.root)
                                .load(videoDetails.thumbnail)
                                .into(binding.imageThumbnail)
                        } else {
                            showImageThumbnail(false)
                        }
                        super.onPlayerStateChanged(playWhenReady, playbackState)
                    }

                }

                override fun onIsLoadingChanged(isLoading: Boolean) {
                    showLoader(isLoading)
                    super.onIsLoadingChanged(isLoading)
                }
            }
            player.addListener(playerListener)
    }

    private fun showImageThumbnail(show: Boolean) {
        binding.imageThumbnail.showVisibility(show)
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
        (requireActivity() as MainActivity).showBottomNavigation(false)
        super.onResume()
    }
    private fun startPlayback() {
        if(this::player.isInitialized){
            player.playWhenReady = true
            binding.customControlView.getBinding().playPauseButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_pause_button))
        }
    }

    private fun stopPlayback() {
        if(this::player.isInitialized){
            player.playWhenReady = false
            binding.customControlView.getBinding().playPauseButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play_button))
        }
    }

    override fun onPause() {
        stopPlayback()
        super.onPause()
    }

    override fun selectedStreamVideo(data: StreamDetails) {

    }

    override fun onOrientationChanged(orientation: Int) {
        handleFullScreenLayout(orientation == Configuration.ORIENTATION_PORTRAIT)
    }

    private fun handleFullScreenLayout(show : Boolean){
        binding.previewInfoDetailLayout.showVisibility(show)
        binding.backButton.showVisibility(show)
        binding.customControlView.getBinding().changeOrientation.showVisibility(show)
        binding.customControlView.getBinding().muteAudio.showVisibility(show)
        binding.customControlView.getBinding().footerFullScreenLayout.showVisibility(!show)
        binding.customControlView.getBinding().volumeControlLayout.showVisibility(!show)
        binding.customControlView.getBinding().brightnessControlLayout.showVisibility(!show)
        binding.customControlView.getBinding().backButton.showVisibility(!show)
        binding.customControlView.getBinding().movieName.showVisibility(!show)
    }

    fun onBackPress() {
        if(isFullScreen()) {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            findNavController().navigate(R.id.action_PreviewFragment_to_HomeFragment)
        }
    }

    private fun isFullScreen() : Boolean {
        return requireContext().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}