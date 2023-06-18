package com.ab.streamverse.util

import android.view.View

object Utils {


    fun View.showVisibility(condition: Boolean) {
        this.visibility = if (condition) View.VISIBLE else View.GONE
    }
}