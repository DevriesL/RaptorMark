package io.github.devriesl.raptormark.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.github.devriesl.raptormark.BenchmarkFragment
import io.github.devriesl.raptormark.HistoryFragment
import io.github.devriesl.raptormark.RaptorActivity
import io.github.devriesl.raptormark.SettingFragment

const val BENCHMARK_PAGE_INDEX = 0
const val HISTORY_PAGE_INDEX = 1
const val SETTING_PAGE_INDEX = 2

class RaptorViewPagerAdapter(raptorActivity: RaptorActivity) : FragmentStateAdapter(raptorActivity) {

    /**
     * Mapping of the ViewPager page indexes to their respective Fragments
     */
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        BENCHMARK_PAGE_INDEX to { BenchmarkFragment() },
        HISTORY_PAGE_INDEX to { HistoryFragment() },
        SETTING_PAGE_INDEX to { SettingFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}