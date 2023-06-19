package com.ab.streamverse.util

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.widget.TextView
import com.ab.streamverse.BuildConfig
import com.ab.streamverse.StreamVerseApplication

object Utils {

    fun List<TextView>.expandView(){
        this.forEach { textView ->
            textView.setOnClickListener {
                textView.maxLines = Int.MAX_VALUE
                textView.ellipsize = null
            }
        }
    }
    fun View.showVisibility(condition: Boolean) {
        this.visibility = if (condition) View.VISIBLE else View.GONE
    }

    fun isBuildVariantDebug(): Boolean {
        return BuildConfig.BUILD_TYPE == Constants.BUILD_TYPE_DEBUG
    }

    internal fun checkInternetConnection(): Boolean {
        val connectivityManager = StreamVerseApplication.appContext()
            ?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isConnected = connectivityManager.activeNetworkInfo?.isConnected
        return isConnected ?: false
    }
}