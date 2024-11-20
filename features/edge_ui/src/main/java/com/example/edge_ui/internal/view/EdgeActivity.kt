package com.example.edge_ui.internal.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.edge_ui.R
import com.example.edge_ui.databinding.ActivityEdgeBinding
import com.example.edge_ui.internal.ext.getComponent

class EdgeActivity : AppCompatActivity() {

    companion object {

        fun createIntent(context: Context): Intent {
            return Intent(context, EdgeActivity::class.java)
        }
    }

    private lateinit var binding: ActivityEdgeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEdgeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getComponent().inject(this)
        supportFragmentManager.commit {
            replace(R.id.app_fragment_container, MatrixMultiplyFragment.newInstance())
            addToBackStack(MatrixMultiplyFragment::class.simpleName)
        }
    }
}