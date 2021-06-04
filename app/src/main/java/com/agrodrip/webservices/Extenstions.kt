package com.agrodrip.webservices

import android.R
import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.snackbar.Snackbar


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

}

fun Context.toast(message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun ProgressBar.show() {
    visibility = View.VISIBLE
}

fun ProgressBar.hide() {
    visibility = View.GONE
}

fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)
}


fun View.snackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.setActionTextColor(this.resources.getColor(R.color.holo_red_light))
        .show()


    fun RecyclerView.smoothSnapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_START) {
        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun getVerticalSnapPreference(): Int {
                return snapMode
            }

            override fun getHorizontalSnapPreference(): Int {
                return snapMode
            }
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }
}
