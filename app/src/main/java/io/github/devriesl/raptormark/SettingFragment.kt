package io.github.devriesl.raptormark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import io.github.devriesl.raptormark.adapters.SettingInfoAdapter
import io.github.devriesl.raptormark.data.InfoItem
import io.github.devriesl.raptormark.data.EngineInfoRepo
import io.github.devriesl.raptormark.data.SettingDataSource
import io.github.devriesl.raptormark.databinding.FragmentSettingBinding
import io.github.devriesl.raptormark.di.StringProvider
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment() {
    @Inject
    lateinit var settingDataSource: SettingDataSource

    @Inject
    lateinit var stringProvider: StringProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSettingBinding.inflate(inflater, container, false)
        val adapter = SettingInfoAdapter(childFragmentManager, viewLifecycleOwner)
        val infoList: List<InfoItem> = listOf(
            InfoItem(ENGINE_CONFIG_SETTING_ID, EngineInfoRepo(stringProvider, settingDataSource)),
        )

        binding.settingList.adapter = adapter
        adapter.submitList(infoList)

        return binding.root
    }

    companion object {
        const val ENGINE_CONFIG_SETTING_ID = "engine_config_setting"
    }
}
