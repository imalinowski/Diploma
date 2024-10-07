package com.malinowski.chat.internal.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.malinowski.chat.R
import com.malinowski.chat.databinding.FragmentMainBinding

class MainFragment private constructor() : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.peers -> {
                    childFragmentManager.commit(allowStateLoss = true) {
                        replace(R.id.navigation_fragment_container, PeerListFragment.newInstance())
                        addToBackStack(null)
                    }
                }

                R.id.logs -> {
                    childFragmentManager.commit(allowStateLoss = true) {
                        replace(R.id.navigation_fragment_container, LogFragment.newInstance())
                        addToBackStack(null)
                    }
                }
            }
            true
        }
        binding.bottomNavigation.selectedItemId = R.id.peers
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}