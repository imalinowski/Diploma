package com.malinowski.diploma.view

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
import com.malinowski.diploma.databinding.FragmentPeerListBinding
import com.malinowski.diploma.model.getComponent
import com.malinowski.diploma.viewmodel.WifiDirectUIState
import com.malinowski.diploma.viewmodel.WifiDirectViewModel
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
    private val adapter = PeerAdapter()

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
                viewModel.uiState.collect(::update)
            }
        }
        peerRecyclerView.apply {
            adapter = this@PeerListFragment.adapter
            layoutManager = LinearLayoutManager(this@PeerListFragment.requireContext())
        }
    }

    private fun update(state: WifiDirectUIState) {
        binding.noPeersFound.isVisible = state.peers.isEmpty()
        adapter.submitList(state.peers)
    }

    companion object {
        fun newInstance() = PeerListFragment()
    }
}