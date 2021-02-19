package io.github.devriesl.raptormark.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.devriesl.raptormark.data.InfoItem
import io.github.devriesl.raptormark.databinding.ListItemSettingInfoBinding
import io.github.devriesl.raptormark.viewmodels.SettingInfoViewModel

class SettingInfoAdapter(
    private val fragmentManager: FragmentManager,
    private val viewLifecycleOwner: LifecycleOwner
) : ListAdapter<InfoItem, SettingInfoAdapter.ViewHolder>(InfoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemSettingInfoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), fragmentManager, viewLifecycleOwner
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ListItemSettingInfoBinding,
        private val fragmentManager: FragmentManager,
        private val viewLifecycleOwner: LifecycleOwner
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InfoItem) {
            binding.apply {
                setClickListener { viewModel?.showDialog(fragmentManager) }
                viewModel = SettingInfoViewModel(item.infoRepo)
                lifecycleOwner = viewLifecycleOwner
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
