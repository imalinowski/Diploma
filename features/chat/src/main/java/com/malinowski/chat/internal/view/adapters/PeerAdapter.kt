package com.malinowski.chat.internal.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.malinowski.chat.databinding.RecyclerViewPeerItemBinding
import com.malinowski.chat.internal.model.ChatPeer

class PeerAdapter(
    val clickCallback: (ChatPeer) -> Unit
) : ListAdapter<ChatPeer, PeerAdapter.ViewHolder>(InterestingItemDiffUtilCallback()) {

    class ViewHolder(val binding: RecyclerViewPeerItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RecyclerViewPeerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val peer = getItem(position)
        with(holder.binding) {
            title.text = peer.name
            button.setOnClickListener {
                clickCallback(peer)
            }
        }

    }

    class InterestingItemDiffUtilCallback : DiffUtil.ItemCallback<ChatPeer>() {

        override fun areItemsTheSame(oldItem: ChatPeer, newItem: ChatPeer): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: ChatPeer, newItem: ChatPeer): Boolean {
            return oldItem == newItem
        }

    }

}