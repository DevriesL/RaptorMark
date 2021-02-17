package io.github.devriesl.raptormark

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import io.github.devriesl.raptormark.adapters.RaptorViewPagerAdapter
import io.github.devriesl.raptormark.adapters.BENCHMARK_PAGE_INDEX
import io.github.devriesl.raptormark.adapters.HISTORY_PAGE_INDEX
import io.github.devriesl.raptormark.adapters.SETTING_PAGE_INDEX
import io.github.devriesl.raptormark.databinding.ActivityRaptorBinding

@AndroidEntryPoint
class RaptorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRaptorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRaptorBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        val tabLayout = binding.tabs
        val viewPager = binding.viewPager

        viewPager.adapter = RaptorViewPagerAdapter(this)

        // Set the icon and text for each tab
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setIcon(getTabIcon(position))
            tab.text = getTabTitle(position)
        }.attach()
    }

    private fun getTabIcon(position: Int): Int {
        return when (position) {
            BENCHMARK_PAGE_INDEX -> R.drawable.ic_benchmark_tab
            HISTORY_PAGE_INDEX -> R.drawable.ic_history_tab
            SETTING_PAGE_INDEX -> R.drawable.ic_setting_tab
            else -> throw IndexOutOfBoundsException()
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            BENCHMARK_PAGE_INDEX -> getString(R.string.benchmark_page_title)
            HISTORY_PAGE_INDEX -> getString(R.string.history_page_title)
            SETTING_PAGE_INDEX -> getString(R.string.setting_page_title)
            else -> null
        }
    }
}