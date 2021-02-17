package io.github.devriesl.raptormark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.devriesl.raptormark.adapters.SettingInfoAdapter
import io.github.devriesl.raptormark.data.SettingItems
import io.github.devriesl.raptormark.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSettingBinding.inflate(inflater, container, false)
        val adapter = SettingInfoAdapter()

        binding.settingList.adapter = adapter
        adapter.submitList(SettingItems.settingList)

        return binding.root
    }
}