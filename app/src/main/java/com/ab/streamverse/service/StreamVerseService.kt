package com.ab.githubtrackerapplication.service

import com.ab.githubtrackerapplication.api.RetrofitWrapper
import com.ab.streamverse.model.Category
import com.ab.streamverse.model.StreamDetails
import com.ab.streamverse.model.VideoDetails
import com.ab.streamverse.model.VideoStreamCategory

class StreamVerseService  {

    private val streamVerseService = RetrofitWrapper.streamVerseApiInterface


    suspend fun getRecentTrendVideoList(): Result<List<StreamDetails>> {
        return  kotlin.runCatching {
            streamVerseService.getRecentTrendVideoList()
        }
    }


    suspend fun getStreamCategoryList(): Result<List<Category>> {
        return  kotlin.runCatching {
            streamVerseService.getStreamCategoryList()
        }
    }


    suspend fun getVideoStreamCategory(): Result<List<VideoStreamCategory>> {
        return  kotlin.runCatching {
            streamVerseService.getVideoStreamCategory()
        }
    }

    suspend fun getVideoDetails(movieKey : String): Result<VideoDetails> {
        return  kotlin.runCatching {
            streamVerseService.getVideoDetails()
        }
    }

    suspend fun getRecommendedVideoList(recommendationKey : String): Result<List<StreamDetails>> {
        return  kotlin.runCatching {
            streamVerseService.getRecommendedVideo()
        }
    }

}
