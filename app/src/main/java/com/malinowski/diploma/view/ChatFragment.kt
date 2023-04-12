package com.malinowski.diploma.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.malinowski.diploma.databinding.FragmentChatBinding
import com.malinowski.diploma.model.WifiDirectPeer
import com.malinowski.diploma.model.getComponent
import com.malinowski.diploma.view.adapters.MessageAdapter
import com.malinowski.diploma.view.adapters.PeerAdapter
import com.malinowski.diploma.viewmodel.WifiDirectViewModel
import javax.inject.Inject

class ChatFragment private constructor(): Fragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: WifiDirectViewModel by activityViewModels { factory }

    private lateinit var binding: FragmentChatBinding

    private lateinit var peer: WifiDirectPeer

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
            layoutManager = LinearLayoutManager(this@ChatFragment.requireContext())
        }
        adapter.submitList(viewModel.getMessages(peer))
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.connectCancel(peer.address)
    }

    companion object {
        const val NAME = "name"
        const val DEVICE_ADDRESS = "name"

        @JvmStatic
        fun newInstance(peer: WifiDirectPeer) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(NAME, peer)
                }
            }
    }
}