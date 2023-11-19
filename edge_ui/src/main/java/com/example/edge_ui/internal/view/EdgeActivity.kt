package com.example.edge_ui.internal.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.edge_ui.R
import com.example.edge_ui.databinding.ActivityEdgeBinding
import com.example.edge_ui.internal.presentation.EdgeUIEvents.GenerateMatrixA
import com.example.edge_ui.internal.presentation.EdgeUIEvents.GenerateMatrixB
import com.example.edge_ui.internal.presentation.EdgeUIState
import kotlinx.coroutines.launch

internal class EdgeActivity : AppCompatActivity() {

    companion object {

        fun createIntent(context: Context): Intent {
            return Intent(context, EdgeActivity::class.java)
        }
    }

    private lateinit var binding: ActivityEdgeBinding
    private val viewModel: EdgeUIViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEdgeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            viewModel.state.collect(::render)
        }
        initUi()
    }

    private fun initUi() = with(binding) {
        matrixA.matrixGenerate.setOnClickListener {
            viewModel.dispatch(GenerateMatrixA)
        }
        matrixB.apply {
            matrixName.text = getString(R.string.matrix_b)
            matrixGenerate.text = getString(R.string.matrix_generate_b)
            matrixGenerate.setOnClickListener {
                viewModel.dispatch(GenerateMatrixB)
            }
        }
    }

    private fun render(state: EdgeUIState) = with(binding) {
        matrixA.matrix.text = state.uiMatrixA
        matrixB.matrix.text = state.uiMatrixB
        matrixResult.text = state.uiMatrixResult
    }
}