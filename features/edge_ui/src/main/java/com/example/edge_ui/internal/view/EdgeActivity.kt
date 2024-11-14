package com.example.edge_ui.internal.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.edge_ui.R
import com.example.edge_ui.api.EdgeUIComponentProvider
import com.example.edge_ui.databinding.ActivityEdgeBinding
import com.example.edge_ui.internal.presentation.EdgeUIEffects
import com.example.edge_ui.internal.presentation.EdgeUIEffects.ShowToast
import com.example.edge_ui.internal.presentation.EdgeUIEvents
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ClickedGenerate.ClickGenerateMatrixA
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ClickedGenerate.ClickGenerateMatrixB
import com.example.edge_ui.internal.presentation.EdgeUIEvents.DomainEvents.AddNewMatrixTask
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatrixSizeChanged
import com.example.edge_ui.internal.presentation.EdgeUIEvents.PeersCounterClicked
import com.example.edge_ui.internal.presentation.EdgeUIState
import com.example.edge_ui.internal.presentation.command_handlers.EdgeUICommands
import com.example.edge_ui.internal.view.model.EdgeUiTaskInfoState
import javax.inject.Inject

class EdgeActivity : AppCompatActivity() {

    companion object {

        fun createIntent(context: Context): Intent {
            return Intent(context, EdgeActivity::class.java)
        }
    }

    private lateinit var binding: ActivityEdgeBinding

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: EdgeUIViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEdgeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as EdgeUIComponentProvider).provideEdgeUIComponent().inject(this)

        viewModel.collect(lifecycleScope, ::render, ::handleEffects)
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
        peersCounter.setOnClickListener {
            viewModel.dispatch(PeersCounterClicked)
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
        peersCounter.text = getString(R.string.peers_online, state.peersCounter)
        matrixA.matrix.text = state.uiMatrixA
        matrixB.matrix.text = state.uiMatrixB
        matrixResult.text = state.uiMatrixResult
        processTaskInfo(state.localTaskInfo, state.remoteTaskInfo)
    }

    private fun processTaskInfo(
        localTaskInfo: EdgeUiTaskInfoState?,
        remoteTaskInfo: EdgeUiTaskInfoState?,
    ) = with(binding) {
        taskInfo.isVisible = localTaskInfo != null || remoteTaskInfo != null
        if (localTaskInfo != null) {
            taskInfoText.text = localTaskInfo.info
            taskInfoLoader.isVisible = localTaskInfo.showProgress
        } else if (remoteTaskInfo != null) {
            taskInfoText.text = remoteTaskInfo.info
            taskInfoLoader.isVisible = remoteTaskInfo.showProgress
        }
    }

    private fun handleEffects(event: EdgeUIEffects) {
        when (event) {
            is ShowToast -> {
                Toast.makeText(this, event.text, Toast.LENGTH_LONG).show()
            }
        }
    }
}