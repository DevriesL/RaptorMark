package io.github.devriesl.raptormark.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.devriesl.raptormark.data.InfoItem
import io.github.devriesl.raptormark.databinding.ListItemSettingInfoBinding
import io.github.devriesl.raptormark.viewmodels.SettingInfoViewModel

class SettingInfoAdapter : ListAdapter<InfoItem, SettingInfoAdapter.ViewHolder>(InfoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemSettingInfoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ListItemSettingInfoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InfoItem) {
            binding.apply {
                viewModel = SettingInfoViewModel(item.infoRepo)
                executePendingBindings()
            }
        }
    }
}

private class InfoDiffCallback : DiffUtil.ItemCallback<InfoItem>() {

    override fun areItemsTheSame(oldItem: InfoItem, newItem: InfoItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: InfoItem, newItem: InfoItem): Boolean {
        return oldItem == newItem
    }
}
