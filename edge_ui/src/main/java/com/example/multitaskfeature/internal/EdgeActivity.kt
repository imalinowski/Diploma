package com.example.multitaskfeature.internal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.multitaskfeature.R
import androidx.activity.viewModels

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