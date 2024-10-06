package com.malinowski.chat.internal.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.malinowski.chat.R
import com.malinowski.chat.databinding.FragmentChatBinding
import com.malinowski.chat.internal.ext.getComponent
import com.malinowski.chat.internal.model.ChatActions
import com.malinowski.chat.internal.model.ChatPeer
import com.malinowski.chat.internal.model.ChatUiState
import com.malinowski.chat.internal.view.adapters.MessageAdapter
import com.malinowski.chat.internal.viewmodel.ChatViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatFragment private constructor() : Fragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ChatViewModel by activityViewModels { factory }

    private lateinit var binding: FragmentChatBinding

    private lateinit var peer: ChatPeer

    private val adapter = MessageAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            peer = it.getParcelable(NAME)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chatName.text = peer.name
        binding.messageRecycler.apply {
            adapter = this@ChatFragment.adapter
            layoutManager = LinearLayoutManager(this@ChatFragment.requireContext()).apply {
                stackFromEnd = true
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collectLatest { actions(it) }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::update)
            }
        }

        binding.send.setOnClickListener {
            val message = binding.messageEdit.text.toString()
            viewModel.sendMessage(message)
            binding.messageEdit.setText("")
        }

    }

    private fun actions(action: ChatActions?) {
        when (action) {
            is ChatActions.ReceiveMessage -> {
                adapter.submitList(listOf(action.message))
            }

            else -> {}
        }
    }

    private fun update(state: ChatUiState) {
        adapter.submitList(state.messages)
        binding.connectionStatus.apply {
            text = if (state.wifiConnectionInfo.groupFormed) {
                if (state.wifiConnectionInfo.isGroupOwner) "Host" else "Client"
            } else {
                getString(R.string.disconnected)
            }
            setBackgroundResource(
                if (state.chatConnectionInfo)
                    R.drawable.connection_on_bg
                else
                    R.drawable.connection_off_bg
            )
            setPadding(20)
        }
    }

    companion object {
        const val NAME = "name"

        @JvmStatic
        fun newInstance(peer: ChatPeer) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(NAME, peer)
                }
            }
    }
}