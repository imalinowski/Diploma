package com.example.edge_ui.internal.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.edge_ui.R
import com.example.edge_ui.databinding.ActivityEdgeBinding
import com.example.edge_ui.internal.presentation.EdgeUIEvents.AddNewMatrixTask
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ClickedGenerate.ClickGenerateMatrixA
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ClickedGenerate.ClickGenerateMatrixB
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatrixSizeChanged
import com.example.edge_ui.internal.presentation.EdgeUIEventsToUI
import com.example.edge_ui.internal.presentation.EdgeUIEventsToUI.ShowToast
import com.example.edge_ui.internal.presentation.EdgeUIState

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

        viewModel.collect(lifecycleScope, ::render, ::handleEvents)
        initUi(viewModel.state)
    }

    private fun initUi(state: EdgeUIState) = with(binding) {
        matrixSize.apply {
            setText(state.matrixSize.toString())
            doAfterTextChanged { text ->
                viewModel.dispatch(
                    MatrixSizeChanged(text)
                )
            }
        }
        initMatrices()
    }

    private fun initMatrices() = with(binding) {
        matrixA.matrixGenerate.setOnClickListener {
            viewModel.dispatch(ClickGenerateMatrixA)
        }
        matrixB.apply {
            matrixName.text = getString(R.string.matrix_b)
            matrixGenerate.text = getString(R.string.matrix_generate_b)
            matrixGenerate.setOnClickListener {
                viewModel.dispatch(ClickGenerateMatrixB)
            }
        }
        matrixMultiply.setOnClickListener {
            viewModel.dispatch(AddNewMatrixTask)
        }
    }

    private fun render(state: EdgeUIState) = with(binding) {
        matrixA.matrix.text = state.uiMatrixA
        matrixB.matrix.text = state.uiMatrixB
        matrixResult.text = state.uiMatrixResult

        taskInfo.isVisible = state.taskInfo != null
        taskInfoText.text = state.taskInfo?.info ?: ""
        taskInfoLoader.isVisible = state.taskInfo?.showProgress ?: false
    }

    private fun handleEvents(event: EdgeUIEventsToUI) {
        when (event) {
            is ShowToast -> {
                Toast.makeText(this, event.text, Toast.LENGTH_LONG).show()
            }
        }
    }
}