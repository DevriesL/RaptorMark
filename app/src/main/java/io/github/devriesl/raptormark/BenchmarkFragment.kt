package io.github.devriesl.raptormark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.devriesl.raptormark.adapters.BenchmarkTestAdapter
import io.github.devriesl.raptormark.data.TestItem
import io.github.devriesl.raptormark.data.LatencyTestRepo
import io.github.devriesl.raptormark.data.RandRwTestRepo
import io.github.devriesl.raptormark.data.SeqRwTestRepo
import io.github.devriesl.raptormark.databinding.FragmentBenchmarkBinding

class BenchmarkFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBenchmarkBinding.inflate(inflater, container, false)
        val adapter = BenchmarkTestAdapter()
        val testList: List<TestItem> = listOf(
            TestItem(SEQ_RW_TEST_ID, SeqRwTestRepo()),
            TestItem(RAND_RW_TEST_ID, RandRwTestRepo()),
            TestItem(LATENCY_TEST_ID, LatencyTestRepo()),
        )

        binding.benchmarkList.adapter = adapter
        adapter.submitList(testList)

        return binding.root
    }

    companion object {
        const val SEQ_RW_TEST_ID = "seq_rw_test"
        const val RAND_RW_TEST_ID = "rand_rw_test"
        const val LATENCY_TEST_ID = "latency_test"
    }
}
