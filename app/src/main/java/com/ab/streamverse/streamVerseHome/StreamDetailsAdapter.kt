package com.ab.streamverse.streamVerseHome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ab.streamverse.R
import com.ab.streamverse.databinding.StreamListLayoutBinding
import com.ab.streamverse.model.StreamDetails
import com.ab.streamverse.util.Constants
import com.ab.streamverse.util.Utils.showVisibility
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding.view.clicks

class StreamDetailsAdapter(private val streamDetailList : List<StreamDetails>, private val delegate : StreamDetailsViewHolder.StreamDetailsDelegate,private val isTop10: Boolean)  : RecyclerView.Adapter<StreamDetailsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamDetailsViewHolder {
        val binding = StreamListLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return StreamDetailsViewHolder(binding,delegate,isTop10)
    }

    override fun onBindViewHolder(holder: StreamDetailsViewHolder, position: Int) {
       holder.onBind(streamDetailList[position])
    }

    override fun getItemCount(): Int {
        return streamDetailList.size
    }


}


class StreamDetailsViewHolder(private val binding : StreamListLayoutBinding, private val delegate: StreamDetailsDelegate,val isTop10:Boolean) : RecyclerView.ViewHolder(binding.root){

    fun onBind(data  :StreamDetails){

        Glide.with(binding.root)
            .load(data.thumbnail)
            .into(binding.imageThumbnail)

        binding.imageFreeTag.showVisibility(data.free == Constants.YES)

        binding.imageThumbnail.clicks()
            .subscribe {
                delegate.selectedStreamVideo(data)
            }

        binding.top10IndexImg.showVisibility(isTop10)
        setTop10Img(position,binding)
    }

    private fun setTop10Img(position: Int,binding: StreamListLayoutBinding){
        val drawable = when(position+1) {
            1 -> R.drawable.__outline1
            2 -> R.drawable.__outline2
            3 -> R.drawable.__outline3
            4 -> R.drawable.__outline4
            5 -> R.drawable.__outline5
            6 -> R.drawable.__outline6
            7 -> R.drawable.__outline7
            8 -> R.drawable.__outline8
            9 -> R.drawable.__outline9
            else -> R.drawable.__outline10
        }
        binding.top10IndexImg.background = binding.root.context.resources.getDrawable(drawable)
    }
    interface StreamDetailsDelegate{
        fun selectedStreamVideo(data: StreamDetails)
    }
}