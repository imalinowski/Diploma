package com.example.edge_ui.internal.ext

import android.app.Activity
import androidx.fragment.app.Fragment
import com.example.edge_ui.api.EdgeUIComponentProvider
import com.example.edge_ui.api.di.EdgeUIComponent

fun Activity.getComponent(): EdgeUIComponent = (applicationContext as EdgeUIComponentProvider).provideEdgeUIComponent()

fun Fragment.getComponent(): EdgeUIComponent = requireActivity().getComponent()