package com.ab.streamverse.streamVerseHome

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ab.githubtrackerapplication.service.StreamVerseService
import com.ab.streamverse.model.Category
import com.ab.streamverse.model.VideoDetails
import com.ab.streamverse.model.StreamDetails
import com.ab.streamverse.model.VideoStreamCategory
import kotlinx.coroutines.*

class StreamVerseViewModel : ViewModel() {

    private val streamVerseService = StreamVerseService()
    private val networkScope = CoroutineScope(Dispatchers.IO)

    private val _errorDetail = MutableLiveData<Unit>()
    val errorDetail: LiveData<Unit>
        get() = _errorDetail

    private val _recentTrendVideoList = MutableLiveData<List<StreamDetails>>()
    val recentTrendVideoList: LiveData<List<StreamDetails>>
        get() = _recentTrendVideoList

    private val _streamCategoryList = MutableLiveData<List<Category>>()
    val streamCategoryList : LiveData<List<Category>>
        get() = _streamCategoryList

    private val _videoStreamCategories = MutableLiveData<List<VideoStreamCategory>>()
    val videoStreamCategoryList: LiveData<List<VideoStreamCategory>>
        get() = _videoStreamCategories

    private val _videoDetail = MutableLiveData<VideoDetails>()
    val videoDetail: LiveData<VideoDetails>
        get() = _videoDetail

    private val _recommendedVideoList = MutableLiveData<List<StreamDetails>>()
    val recommendedVideoList: LiveData<List<StreamDetails>>
        get() = _recommendedVideoList



    fun getRecentTrendVideoList() {
        networkScope.launch {
            try {
                val resultUser = streamVerseService.getRecentTrendVideoList()

                withContext(Dispatchers.Main) {
                resultUser.onSuccess { recentTrendList ->
                    _recentTrendVideoList.value = recentTrendList
                }
                resultUser.onFailure {
                    _errorDetail.value = Unit
                }
                }
            } catch (e: Exception) {
                // Handle the error scenario
            }
        }
    }

    fun getStreamCategoryList() {
        networkScope.launch {
            try {
                val resultUser = streamVerseService.getStreamCategoryList()

                withContext(Dispatchers.Main) {
                    resultUser.onSuccess { streamCategories ->
                        _streamCategoryList.value = streamCategories
                    }
                    resultUser.onFailure {
                        _errorDetail.value = Unit
                    }
                }
            } catch (e: Exception) {
                // Handle the error scenario
            }
        }
    }

    fun getVideoStreamCategory() {
        networkScope.launch {
            try {
                val resultUser = streamVerseService.getVideoStreamCategory()

                withContext(Dispatchers.Main) {
                    resultUser.onSuccess { videoStreamCategories ->
                        _videoStreamCategories.value = videoStreamCategories
                    }
                    resultUser.onFailure {
                        _errorDetail.value = Unit
                        Log.d("asmi-error",it.toString())
                    }
                }
            } catch (e: Exception) {
                Log.d("asmi-error",e.toString())
            }
        }
    }

    fun getVideoDetail(movieKey : String) {
        networkScope.launch {
            try {
                val resultUser = streamVerseService.getVideoDetails(movieKey)

                withContext(Dispatchers.Main) {
                    resultUser.onSuccess { videoDetail ->
                        _videoDetail.value = videoDetail
                    }
                    resultUser.onFailure {
                        _errorDetail.value = Unit
                        Log.d("asmi-error",it.toString())
                    }
                }
            } catch (e: Exception) {
                Log.d("asmi-error",e.toString())
            }
        }
    }

    fun getRecommendedVideoList(recommendationKey : String) {
        networkScope.launch {
            try {
                val resultUser = streamVerseService.getRecommendedVideoList(recommendationKey)

                withContext(Dispatchers.Main) {
                    resultUser.onSuccess { recentTrendList ->
                        _recommendedVideoList.value = recentTrendList
                    }
                    resultUser.onFailure {
                        _errorDetail.value = Unit
                    }
                }
            } catch (e: Exception) {
                // Handle the error scenario
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        networkScope.cancel() // Cancel the coroutine scope when the ViewModel is cleared
    }
}