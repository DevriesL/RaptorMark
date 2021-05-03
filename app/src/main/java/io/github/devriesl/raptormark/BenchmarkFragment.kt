package io.github.devriesl.raptormark

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import io.github.devriesl.raptormark.Constants.LATENCY_TEST_ID
import io.github.devriesl.raptormark.Constants.RAND_RD_TEST_ID
import io.github.devriesl.raptormark.Constants.RAND_WR_TEST_ID
import io.github.devriesl.raptormark.Constants.SEQ_RD_TEST_ID
import io.github.devriesl.raptormark.Constants.SEQ_WR_TEST_ID
import io.github.devriesl.raptormark.adapters.BenchmarkTestAdapter
import io.github.devriesl.raptormark.data.*
import io.github.devriesl.raptormark.databinding.FragmentBenchmarkBinding
import io.github.devriesl.raptormark.di.StringProvider
import javax.inject.Inject

@AndroidEntryPoint
class BenchmarkFragment : Fragment() {
    @Inject
    lateinit var settingDataSource: SettingDataSource

    @Inject
    lateinit var stringProvider: StringProvider

    private var shouldStop = false;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBenchmarkBinding.inflate(inflater, container, false)
        val adapter = BenchmarkTestAdapter(viewLifecycleOwner)
        val testList: List<TestItem> = listOf(
            TestItem(SEQ_RD_TEST_ID, SeqRdTestRepo(stringProvider, settingDataSource)),
            TestItem(SEQ_WR_TEST_ID, SeqWrTestRepo(stringProvider, settingDataSource)),
            TestItem(RAND_RD_TEST_ID, RandRdTestRepo(stringProvider, settingDataSource)),
            TestItem(RAND_WR_TEST_ID, RandWrTestRepo(stringProvider, settingDataSource)),
        )

        val advTestList: List<TestItem> = listOf(
            TestItem(LATENCY_TEST_ID, LatencyTestRepo(stringProvider, settingDataSource)),
        )

        binding.benchmarkList.adapter = adapter
        adapter.submitList(testList)

        binding.startButton.setOnClickListener {
            binding.startButton.visibility = View.GONE
            binding.stopButton.visibility = View.VISIBLE
            NativeDataSource.postNativeThread {
                testList.forEach {
                    try {
                        if (shouldStop) return@forEach

                        it.testRepo.runTest()
                    } catch (ex: Exception) {
                        Log.e(it.id, "Error running test", ex)
                        return@forEach
                    }
                }
                activity?.runOnUiThread {
                    binding.startButton.visibility = View.VISIBLE
                    binding.stopButton.visibility = View.GONE
                }
                shouldStop = false
            }
        }

        binding.stopButton.setOnClickListener { shouldStop = true }

        return binding.root
    }
}
