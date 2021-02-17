package io.github.devriesl.raptormark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.devriesl.raptormark.adapters.BenchmarkTestAdapter
import io.github.devriesl.raptormark.data.TestItem
import io.github.devriesl.raptormark.data.LatencyNativeTest
import io.github.devriesl.raptormark.data.RandRwNativeTest
import io.github.devriesl.raptormark.data.SeqRwNativeTest
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
            TestItem(SEQ_RW_TEST_ID, SeqRwNativeTest()),
            TestItem(RAND_RW_TEST_ID, RandRwNativeTest()),
            TestItem(LATENCY_TEST_ID, LatencyNativeTest()),
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
