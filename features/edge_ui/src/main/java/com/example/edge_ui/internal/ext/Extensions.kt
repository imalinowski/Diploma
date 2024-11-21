package com.example.edge_ui.internal.ext

import android.app.Activity
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import com.example.edge_ui.api.EdgeUIComponentProvider
import com.example.edge_ui.api.di.EdgeUIComponent

private const val DEFAULT_FADE_DURATION = 200L

fun Activity.getComponent(): EdgeUIComponent = (applicationContext as EdgeUIComponentProvider).provideEdgeUIComponent()

fun Fragment.getComponent(): EdgeUIComponent = requireActivity().getComponent()

fun View.setVisibilityAnimated(
    visible: Boolean,
    duration: Long = DEFAULT_FADE_DURATION
) {
    animate().apply {
        if (visible) {
            alpha(1f)
        } else {
            alpha(0f)
        }
        this.duration = duration
        interpolator = LinearInterpolator()
    }.withEndAction { setVisibility(visible) }
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun View.setVisibility(visible: Boolean) {
    if (visible) makeVisible() else makeGone()
}