package io.github.devriesl.raptormark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.devriesl.raptormark.adapters.SettingInfoAdapter
import io.github.devriesl.raptormark.data.SettingItem
import io.github.devriesl.raptormark.data.EngineSettingInfo
import io.github.devriesl.raptormark.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSettingBinding.inflate(inflater, container, false)
        val adapter = SettingInfoAdapter()
        val settingList: List<SettingItem> = listOf(
            SettingItem(ENGINE_CONFIG_SETTING_ID, EngineSettingInfo()),
        )

        binding.settingList.adapter = adapter
        adapter.submitList(settingList)

        return binding.root
    }

    companion object {
        const val ENGINE_CONFIG_SETTING_ID = "engine_config_setting"
    }
}
