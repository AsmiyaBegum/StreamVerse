package com.ab.streamverse.util

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ab.streamverse.R
import com.ab.streamverse.databinding.CastListRowBinding
import com.ab.streamverse.databinding.RecentReleaseImageBinding
import com.ab.streamverse.databinding.StreamCategoryListLayoutBinding
import com.ab.streamverse.model.Casting
import com.ab.streamverse.model.VideoStreamCategory
import com.ab.streamverse.model.StreamDetails
import com.ab.streamverse.streamVerseHome.StreamDetailsAdapter
import com.ab.streamverse.streamVerseHome.StreamDetailsViewHolder
import com.ab.streamverse.util.Utils.showVisibility
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation

object AdapterUtils {

    fun setUpVideoStreamCategoryListAdapter(streamCategoryList : List<VideoStreamCategory>, delegate: StreamDetailsViewHolder.StreamDetailsDelegate) : GenericAdapter<VideoStreamCategory, StreamCategoryListLayoutBinding,List<Unit>>{

        val adapter = GenericAdapter(R.layout.stream_category_list_layout,object : GenericAdapterInteraction<VideoStreamCategory, StreamCategoryListLayoutBinding,List<Unit>>(){

            override fun bindingViewHolder(
                binding: StreamCategoryListLayoutBinding,
                data: VideoStreamCategory,
                holder: GenericAdapter.GenericViewHolder<VideoStreamCategory, StreamCategoryListLayoutBinding, List<Unit>>,
                additionalData: List<Unit>?
            ) {
                binding.streamCategoryName.text = data.streamName
                updateStreamList(data.streamList,binding,data.isTopTen)
            }


            private fun updateStreamList(streamList : List<StreamDetails>,binding: StreamCategoryListLayoutBinding,isTop10 : Boolean){
                binding.streamList.layoutManager = LinearLayoutManager(binding.root.context,RecyclerView.HORIZONTAL,false)
                binding.streamList.adapter = StreamDetailsAdapter(streamList,delegate,isTop10)
            }

            override fun onClicked(data: VideoStreamCategory, binding: StreamCategoryListLayoutBinding) {
                //override fun not implemented
            }
        })
        adapter.addItems(streamCategoryList)

        return adapter
    }


    fun setUpRecentTrendsAdapter(streamDetails : List<StreamDetails>,delegate : StreamDetailsViewHolder.StreamDetailsDelegate) :  GenericAdapter<StreamDetails, RecentReleaseImageBinding,List<Unit>>{

        val adapter = GenericAdapter(R.layout.recent_release_image,object : GenericAdapterInteraction<StreamDetails, RecentReleaseImageBinding,List<Unit>>(){

            override fun bindingViewHolder(binding: RecentReleaseImageBinding, data: StreamDetails, holder: GenericAdapter.GenericViewHolder<StreamDetails, RecentReleaseImageBinding, List<Unit>>, additionalData: List<Unit>?) {
                binding.imageFreeTag.showVisibility(data.free == Constants.YES)
                handleViewImg(binding,data.thumbnail)
            }


            override fun onClicked(data: StreamDetails, binding: RecentReleaseImageBinding) {
                delegate.selectedStreamVideo(data)
                //override fun not implemented
            }

            private fun handleViewImg(binding: RecentReleaseImageBinding,thumbnail : String){
                glideRequestBuilder(false,thumbnail,binding).into(binding.releaseImage)
                glideRequestBuilder(true,thumbnail,binding).into(binding.releaseImageBg)
            }

            private fun glideRequestBuilder(blurImg: Boolean,thumbnail : String,binding: RecentReleaseImageBinding) : RequestBuilder<Drawable> {
                val glideBuilder = Glide.with(binding.root.context)
                    .load(thumbnail)
                if(blurImg){
                    glideBuilder.apply(
                        RequestOptions.bitmapTransform(
                            BlurTransformation(
                        25,
                        3)
                        ))
                }
                return glideBuilder
            }
        })
        adapter.addItems(streamDetails)

        return adapter
    }
}