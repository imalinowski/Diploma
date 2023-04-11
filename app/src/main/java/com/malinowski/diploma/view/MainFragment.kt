package com.malinowski.diploma.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.malinowski.diploma.R
import com.malinowski.diploma.databinding.FragmentMainBinding

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
                R.id.page_1 -> {
                    childFragmentManager.commit(allowStateLoss = true) {
                        replace(R.id.navigation_fragment_container, PeerListFragment.newInstance())
                        addToBackStack(null)
                    }
                }
                R.id.page_2 -> {
                    childFragmentManager.commit(allowStateLoss = true) {
                        replace(R.id.navigation_fragment_container, LogFragment.newInstance())
                        addToBackStack(null)
                    }
                }
            }
            true
        }
        binding.bottomNavigation.selectedItemId = R.id.page_1
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}