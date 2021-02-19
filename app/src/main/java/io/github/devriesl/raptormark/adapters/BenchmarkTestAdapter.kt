package io.github.devriesl.raptormark.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.devriesl.raptormark.data.TestItem
import io.github.devriesl.raptormark.databinding.ListItemBenchmarkTestBinding
import io.github.devriesl.raptormark.viewmodels.BenchmarkTestViewModel

class BenchmarkTestAdapter :
    ListAdapter<TestItem, BenchmarkTestAdapter.ViewHolder>(TestDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemBenchmarkTestBinding.inflate(
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
        private val binding: ListItemBenchmarkTestBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TestItem) {
            binding.apply {
                viewModel = BenchmarkTestViewModel(item.testRepo)
                executePendingBindings()
            }
        }
    }

    private class TestDiffCallback : DiffUtil.ItemCallback<TestItem>() {

        override fun areItemsTheSame(oldItem: TestItem, newItem: TestItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TestItem, newItem: TestItem): Boolean {
            return oldItem == newItem
        }
    }
}
