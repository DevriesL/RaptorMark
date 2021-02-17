package io.github.devriesl.raptormark.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.devriesl.raptormark.data.SettingItem
import io.github.devriesl.raptormark.databinding.ListItemSettingInfoBinding
import io.github.devriesl.raptormark.viewmodels.SettingInfoViewModel

class SettingInfoAdapter : ListAdapter<SettingItem, SettingInfoAdapter.ViewHolder>(InfoDiffCallback()) {

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
        fun bind(item: SettingItem) {
            binding.apply {
                viewModel = SettingInfoViewModel(item.settingInfo)
                executePendingBindings()
            }
        }
    }
}

private class InfoDiffCallback : DiffUtil.ItemCallback<SettingItem>() {

    override fun areItemsTheSame(oldItem: SettingItem, newItem: SettingItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SettingItem, newItem: SettingItem): Boolean {
        return oldItem == newItem
    }
}
