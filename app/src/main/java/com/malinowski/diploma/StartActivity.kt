package com.malinowski.diploma

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.edge_ui.api.EdgeUIFacade
import com.malinowski.chat.api.ChatFacade
import com.malinowski.diploma.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtonListeners()
    }

    private fun setButtonListeners() = with(binding) {
        buttonToChat.setOnClickListener {
            startActivity(
                ChatFacade.getChatActivityIntent(this@StartActivity)
            )
        }
        buttonToEdge.setOnClickListener {
            startActivity(
                EdgeUIFacade.getEdgeActivityIntent(this@StartActivity)
            )
        }
    }
}