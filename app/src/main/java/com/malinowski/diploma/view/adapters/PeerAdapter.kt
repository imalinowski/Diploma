package com.malinowski.diploma.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.malinowski.diploma.databinding.RecyclerViewPeerItemBinding
import com.malinowski.diploma.model.WifiDirectPeer

class PeerAdapter(
    val clickCallback: (WifiDirectPeer) -> Unit
) : ListAdapter<WifiDirectPeer, PeerAdapter.ViewHolder>(InterestingItemDiffUtilCallback()) {

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

    class InterestingItemDiffUtilCallback : DiffUtil.ItemCallback<WifiDirectPeer>() {

        override fun areItemsTheSame(oldItem: WifiDirectPeer, newItem: WifiDirectPeer): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: WifiDirectPeer, newItem: WifiDirectPeer): Boolean {
            return oldItem == newItem
        }

    }

}