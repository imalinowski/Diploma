package com.malinowski.logs.internal.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.malinowski.logs.databinding.FragmentLogBinding
import com.malinowski.logs.internal.ext.getComponent
import com.malinowski.logs.internal.presentation.LogEvents.ClearLogs
import com.malinowski.logs.internal.presentation.LogEvents.SaveLogs
import com.malinowski.logs.internal.presentation.LogEvents.SearchForDevices
import com.malinowski.logs.internal.presentation.LogUiState
import com.malinowski.logs.internal.view_model.LogViewModel
import javax.inject.Inject

class LogFragment : Fragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: LogViewModel by activityViewModels { factory }

    private lateinit var binding: FragmentLogBinding

    private val logView: TextView by lazy { binding.logsText }
    private val searchDevicesBtn: Button by lazy { binding.searchDevicesBtn }
    private val clearLogs: Button by lazy { binding.clearLogs }
    private val loadLogs: Button by lazy { binding.loadLogs }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.collect(lifecycleScope, ::render)

        searchDevicesBtn.setOnClickListener {
            viewModel.dispatch(SearchForDevices)
        }
        clearLogs.setOnClickListener {
            viewModel.dispatch(ClearLogs)
        }
        loadLogs.setOnClickListener {
            viewModel.dispatch(SaveLogs)
        }
    }

    private fun render(state: LogUiState) {
        logView.text = state.logText
    }

    companion object {
        fun newInstance() = LogFragment()
    }
}