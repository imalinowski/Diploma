package com.example.edge_ui.internal.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.edge_ui.R
import com.example.edge_ui.databinding.FragmentMatrixMultiplyBinding
import com.example.edge_ui.internal.ext.getComponent
import com.example.edge_ui.internal.ext.setVisibilityAnimated
import com.example.edge_ui.internal.presentation.EdgeUIEffects
import com.example.edge_ui.internal.presentation.EdgeUIEffects.ShowToast
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ClickedGenerate.ClickGenerateMatrixA
import com.example.edge_ui.internal.presentation.EdgeUIEvents.ClickedGenerate.ClickGenerateMatrixB
import com.example.edge_ui.internal.presentation.EdgeUIEvents.DomainEvents.AddNewMatrixTask
import com.example.edge_ui.internal.presentation.EdgeUIEvents.MatrixSizeChanged
import com.example.edge_ui.internal.presentation.EdgeUIEvents.PeersCounterClicked
import com.example.edge_ui.internal.presentation.EdgeUIState
import com.example.edge_ui.internal.view.model.EdgeUiTaskInfoState
import com.example.navigation.LogsNavigation
import javax.inject.Inject

class MatrixMultiplyFragment : Fragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: EdgeUIViewModel by activityViewModels { factory }

    private lateinit var binding: FragmentMatrixMultiplyBinding

    @Inject
    lateinit var logsNavigation: LogsNavigation

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMatrixMultiplyBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        logs.setOnClickListener {
            openLogs()
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
        taskInfo.setVisibilityAnimated(localTaskInfo != null || remoteTaskInfo != null)
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
                Toast.makeText(context, event.text, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openLogs() {
        parentFragmentManager.commit {
            replace(R.id.app_fragment_container, logsNavigation.createLogsFragment())
            addToBackStack("LogsFragment")
        }
    }

    companion object {
        fun newInstance() = MatrixMultiplyFragment()
    }
}