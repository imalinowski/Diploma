package com.malinowski.diploma.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.malinowski.diploma.R
import com.malinowski.diploma.databinding.ActivityMainBinding
import com.malinowski.diploma.model.getComponent
import com.malinowski.diploma.viewmodel.WifiDirectViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel: WifiDirectViewModel by viewModels { factory }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getComponent().inject(this)

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.page_1 -> {
                    supportFragmentManager.commit(allowStateLoss = true) {
                        replace(R.id.fragment_container_view, PeerListFragment.newInstance())
                        addToBackStack(null)
                    }
                }
                R.id.page_2 -> {
                    supportFragmentManager.commit(allowStateLoss = true) {
                        replace(R.id.fragment_container_view, LogFragment.newInstance())
                        addToBackStack(null)
                    }
                }
            }
            true
        }

    }

    public override fun onResume() {
        super.onResume()
        viewModel.registerReceiver()
    }

    public override fun onPause() {
        super.onPause()
        viewModel.unregisterReceiver()
    }

}