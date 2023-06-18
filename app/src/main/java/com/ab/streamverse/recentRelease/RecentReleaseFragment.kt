package com.ab.streamverse.recentRelease

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ab.streamverse.databinding.RecentReleaseImageBinding
import com.ab.streamverse.model.StreamDetails
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import jp.wasabeef.glide.transformations.BlurTransformation


class RecentReleaseFragment :  Fragment() {


    lateinit var binding : RecentReleaseImageBinding
    lateinit var streamDetails: StreamDetails


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RecentReleaseImageBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleViewImg()
    }

    private fun handleViewImg(){
         glideRequestBuilder(false).into(binding.releaseImage)
         glideRequestBuilder(true).into(binding.releaseImageBg)
    }

    private fun glideRequestBuilder(blurImg: Boolean) : RequestBuilder<Drawable>{
       val glideBuilder = Glide.with(this)
            .load(streamDetails.thumbnail)
        if(blurImg){
            glideBuilder.apply(RequestOptions.bitmapTransform(BlurTransformation(
                25,
                3)))
        }
        return glideBuilder
    }

    companion object {
        fun newInstance(streamDetails : StreamDetails)  : RecentReleaseFragment{
            val fragment = RecentReleaseFragment()
            fragment.streamDetails = streamDetails
            return fragment
        }
    }
}