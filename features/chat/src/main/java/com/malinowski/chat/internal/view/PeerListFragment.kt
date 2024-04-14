package com.malinowski.chat.internal.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.malinowski.chat.databinding.FragmentPeerListBinding
import com.malinowski.chat.internal.ext.getComponent
import com.malinowski.chat.internal.model.WifiDirectUiState
import com.malinowski.chat.internal.view.adapters.PeerAdapter
import com.malinowski.chat.internal.viewmodel.WifiDirectViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class PeerListFragment : Fragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel: WifiDirectViewModel by activityViewModels { factory }

    private lateinit var binding: FragmentPeerListBinding

    private val peerRecyclerView: RecyclerView by lazy {
        binding.peerRecycler
    }
    private val adapter = PeerAdapter { device ->
        viewModel.connectDevice(device)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPeerListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::update)
            }
        }
        peerRecyclerView.apply {
            adapter = this@PeerListFragment.adapter
            layoutManager = LinearLayoutManager(this@PeerListFragment.requireContext())
        }

        binding.swiperefresh.setOnRefreshListener {
            if (viewModel.checkPermissions(requireContext())) {
                viewModel.searchForDevices()
            }
        }
    }

    private fun update(state: WifiDirectUiState) {
        binding.swiperefresh.isRefreshing = false
        binding.noPeersFound.isVisible = state.peers.isEmpty()
        adapter.submitList(state.peers)
    }

    companion object {
        fun newInstance() = PeerListFragment()
    }
}