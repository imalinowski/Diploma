package com.example.edge_ui.internal.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.edge_ui.R

internal class EdgeActivity : AppCompatActivity() {

    companion object {

        fun createIntent(context: Context): Intent {
            return Intent(context, EdgeActivity::class.java)
        }
    }

    private val viewModel: EdgeUIViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edge)
    }
}