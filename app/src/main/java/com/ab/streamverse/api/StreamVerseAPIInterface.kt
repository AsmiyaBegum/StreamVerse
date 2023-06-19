package com.ab.githubtrackerapplication.api

import com.ab.streamverse.model.Category
import com.ab.streamverse.model.VideoDetails
import com.ab.streamverse.model.VideoStreamCategory
import com.ab.streamverse.model.StreamDetails
import retrofit2.http.GET
import retrofit2.http.Path

interface StreamVerseAPIInterface {

    @GET("v3/13027f57-b827-43be-bc2b-6dd363da701d")
    suspend fun getRecentTrendVideoList():List<StreamDetails>

    @GET("v3/49fec598-1f91-40fc-99c2-9e71470c02ee")
    suspend fun getStreamCategoryList() : List<Category>

    @GET("v3/d129ce75-34b2-4c9f-b828-1310e2e1ad73")
    suspend fun getVideoStreamCategory() : List<VideoStreamCategory>

    @GET("v3/20414898-c00b-4663-b985-9a74e1e15a5d")
    suspend fun getVideoDetails() : VideoDetails

    @GET("v3/f9d940d8-f176-46dd-9a0e-f6fce8783790")
    suspend fun getRecommendedVideo() : List<StreamDetails>

}