package com.ab.streamverse.util

import android.view.View
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

class CircularPagerSnapHelper : PagerSnapHelper() {

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        val view = super.findSnapView(layoutManager)

        // If there is no snap view, return null
        if (view == null) {
            return null
        }

        val layoutManager = layoutManager ?: return null

        // Calculate the position of the snap view
        val snapPosition = layoutManager.getPosition(view)

        // If it's the first position, return the snap view
        if (snapPosition == 0) {
            return view
        }

        // If it's the last position, return the snap view
        if (snapPosition == layoutManager.itemCount - 1) {
            return view
        }

        // If it's neither the first nor the last position, return null
        return null
    }
}
