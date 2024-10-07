package com.malinowski.chat.internal.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.malinowski.chat.databinding.FragmentLogBinding
import com.malinowski.chat.internal.ext.getComponent
import com.malinowski.chat.internal.model.ChatUiState
import com.malinowski.chat.internal.presentation.ChatEvents
import com.malinowski.chat.internal.presentation.ChatEvents.ChatUIEvents.ClearLogs
import com.malinowski.chat.internal.presentation.ChatEvents.ChatUIEvents.SaveLogs
import com.malinowski.chat.internal.presentation.ChatEvents.ChatUIEvents.SearchForDevices
import com.malinowski.chat.internal.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class LogFragment : Fragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ChatViewModel by activityViewModels { factory }

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

    private fun render(state: ChatUiState) {
        logView.text = state.logText
    }

    companion object {
        fun newInstance() = LogFragment()
    }
}