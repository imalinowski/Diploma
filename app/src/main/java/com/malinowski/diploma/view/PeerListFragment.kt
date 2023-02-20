package com.malinowski.diploma.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.malinowski.diploma.databinding.FragmentPeerListBinding

class PeerListFragment : Fragment() {

    private lateinit var binding: FragmentPeerListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPeerListBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        fun newInstance() = PeerListFragment()
    }
}