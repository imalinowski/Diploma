package com.malinowski.diploma.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.malinowski.diploma.databinding.ActivityChatBinding
import com.malinowski.diploma.model.getComponent

const val NAME = "name"

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getComponent().inject(this)

        name = savedInstanceState?.getString(NAME)
        binding.chatName.text = name ?: "Chat Name is Empty"
    }

}