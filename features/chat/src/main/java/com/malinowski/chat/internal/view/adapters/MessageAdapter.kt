package com.malinowski.chat.internal.view.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wifi_direct.api.Message
import com.malinowski.chat.databinding.RecyclerViewMessageItemBinding

class MessageAdapter :
    ListAdapter<Message, MessageAdapter.ViewHolder>(InterestingItemDiffUtilCallback()) {

    class ViewHolder(val binding: RecyclerViewMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RecyclerViewMessageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            title.text = item.author
            message.text = item.text
            time.isVisible = item.time != null
            time.text = item.time
            container.layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                .apply {
                    setMargins(15)
                    gravity = if (item.fromRemote) Gravity.START else Gravity.END
                }
        }
    }

    class InterestingItemDiffUtilCallback : DiffUtil.ItemCallback<Message>() {

        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return with(oldItem) { text + author + time } == with(newItem) { text + author + time }
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

    }

}