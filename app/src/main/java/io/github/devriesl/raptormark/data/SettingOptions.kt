package io.github.devriesl.raptormark.data

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import io.github.devriesl.raptormark.BuildConfig
import io.github.devriesl.raptormark.Constants.DEFAULT_IO_DEPTH_VALUE
import io.github.devriesl.raptormark.Constants.DEFAULT_IO_ENGINE_VALUE
import io.github.devriesl.raptormark.Constants.DEFAULT_IO_SIZE_VALUE
import io.github.devriesl.raptormark.Constants.DEFAULT_NUM_THREADS_VALUE
import io.github.devriesl.raptormark.Constants.DEFAULT_RAND_BLOCK_SIZE_VALUE
import io.github.devriesl.raptormark.Constants.DEFAULT_RUNTIME_LIMIT_VALUE
import io.github.devriesl.raptormark.Constants.DEFAULT_SEQ_BLOCK_SIZE_VALUE
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.ui.setting.SingleChoiceDialog
import io.github.devriesl.raptormark.ui.setting.TargetPathDialog
import io.github.devriesl.raptormark.ui.setting.TextInputDialog
import org.json.JSONObject
import java.io.File
import java.net.URI

enum class SettingOptions(
    @StringRes val title: Int,
    @StringRes val desc: Int,
    val settingData: ISettingData
) {
    TARGET_PATH(R.string.target_path_title, R.string.target_path_desc, object : ISettingData {
        override fun getSettingData(settingSharedPrefs: SettingSharedPrefs): String {
            val uri = URI(settingSharedPrefs.getTestDirPath())
            val path: String = uri.path
            return try {
                uri.path.substring(path.indexOf('/', 0), path.indexOf('/', 1))
            } catch (ex: Exception) {
                path
            }
        }

        override fun onDialogContent(
            settingSharedPrefs: SettingSharedPrefs,
            itemIndex: Int,
            closeDialog: (Int, String?) -> Unit
        ): @Composable () -> Unit {
            val defaultValue = settingSharedPrefs.getDefaultDirPath()
            val currentValue = settingSharedPrefs.getTestDirPath()
            val customValue = if (defaultValue == currentValue) {
                String()
            } else {
                currentValue
            }
            return {
                TargetPathDialog(
                    title = TARGET_PATH.title,
                    customValue = customValue,
                    itemIndex = itemIndex,
                    closeDialog = closeDialog
                )
            }
        }

        override fun setDialogResult(settingSharedPrefs: SettingSharedPrefs, result: String) {
            if (File(result).isDirectory) {
                settingSharedPrefs.setTestDirPath(result)
            } else {
                settingSharedPrefs.setTestDirPath(String())
            }
        }
    }),
    IO_DEPTH(R.string.io_depth_title, R.string.io_depth_desc, object : ISettingData {
        override fun getSettingData(settingSharedPrefs: SettingSharedPrefs): String {
            return settingSharedPrefs.getConfig(IO_DEPTH.name, DEFAULT_IO_DEPTH_VALUE)
        }

        override fun onDialogContent(
            settingSharedPrefs: SettingSharedPrefs,
            itemIndex: Int,
            closeDialog: (Int, String?) -> Unit
        ): @Composable () -> Unit {
            val currentValue = settingSharedPrefs.getConfig(IO_DEPTH.name, DEFAULT_IO_DEPTH_VALUE)
            return {
                TextInputDialog(
                    title = IO_DEPTH.title,
                    defaultValue = DEFAULT_IO_DEPTH_VALUE,
                    currentValue = currentValue,
                    keyboardType = KeyboardType.Number,
                    itemIndex = itemIndex,
                    closeDialog = closeDialog
                )
            }
        }

        override fun setDialogResult(settingSharedPrefs: SettingSharedPrefs, result: String) {
            settingSharedPrefs.setConfig(IO_DEPTH.name, result)
        }
    }),
    RUNTIME_LIMIT(R.string.runtime_limit_title, R.string.runtime_limit_desc, object : ISettingData {
        override fun getSettingData(settingSharedPrefs: SettingSharedPrefs): String {
            return settingSharedPrefs.getConfig(RUNTIME_LIMIT.name, DEFAULT_RUNTIME_LIMIT_VALUE)
        }

        override fun onDialogContent(
            settingSharedPrefs: SettingSharedPrefs,
            itemIndex: Int,
            closeDialog: (Int, String?) -> Unit
        ): @Composable () -> Unit {
            val currentValue =
                settingSharedPrefs.getConfig(RUNTIME_LIMIT.name, DEFAULT_RUNTIME_LIMIT_VALUE)
            return {
                TextInputDialog(
                    title = RUNTIME_LIMIT.title,
                    defaultValue = DEFAULT_RUNTIME_LIMIT_VALUE,
                    currentValue = currentValue,
                    keyboardType = KeyboardType.Number,
                    itemIndex = itemIndex,
                    closeDialog = closeDialog
                )
            }
        }

        override fun setDialogResult(settingSharedPrefs: SettingSharedPrefs, result: String) {
            settingSharedPrefs.setConfig(RUNTIME_LIMIT.name, result)
        }
    }),
    SEQ_BLOCK_SIZE(
        R.string.seq_block_size_title,
        R.string.seq_block_size_desc,
        object : ISettingData {
            override fun getSettingData(settingSharedPrefs: SettingSharedPrefs): String {
                return settingSharedPrefs.getConfig(
                    SEQ_BLOCK_SIZE.name,
                    DEFAULT_SEQ_BLOCK_SIZE_VALUE
                )
            }

            override fun onDialogContent(
                settingSharedPrefs: SettingSharedPrefs,
                itemIndex: Int,
                closeDialog: (Int, String?) -> Unit
            ): @Composable () -> Unit {
                val currentValue =
                    settingSharedPrefs.getConfig(SEQ_BLOCK_SIZE.name, DEFAULT_SEQ_BLOCK_SIZE_VALUE)
                return {
                    TextInputDialog(
                        title = SEQ_BLOCK_SIZE.title,
                        defaultValue = DEFAULT_SEQ_BLOCK_SIZE_VALUE,
                        currentValue = currentValue,
                        keyboardType = KeyboardType.Number,
                        itemIndex = itemIndex,
                        closeDialog = closeDialog
                    )
                }
            }

            override fun setDialogResult(settingSharedPrefs: SettingSharedPrefs, result: String) {
                settingSharedPrefs.setConfig(SEQ_BLOCK_SIZE.name, result)
            }
        }),
    RAND_BLOCK_SIZE(
        R.string.rand_block_size_title,
        R.string.rand_block_size_desc,
        object : ISettingData {
            override fun getSettingData(settingSharedPrefs: SettingSharedPrefs): String {
                return settingSharedPrefs.getConfig(
                    RAND_BLOCK_SIZE.name,
                    DEFAULT_RAND_BLOCK_SIZE_VALUE
                )
            }

            override fun onDialogContent(
                settingSharedPrefs: SettingSharedPrefs,
                itemIndex: Int,
                closeDialog: (Int, String?) -> Unit
            ): @Composable () -> Unit {
                val currentValue = settingSharedPrefs.getConfig(
                    RAND_BLOCK_SIZE.name,
                    DEFAULT_RAND_BLOCK_SIZE_VALUE
                )
                return {
                    TextInputDialog(
                        title = RAND_BLOCK_SIZE.title,
                        defaultValue = DEFAULT_RAND_BLOCK_SIZE_VALUE,
                        currentValue = currentValue,
                        keyboardType = KeyboardType.Number,
                        itemIndex = itemIndex,
                        closeDialog = closeDialog
                    )
                }
            }

            override fun setDialogResult(settingSharedPrefs: SettingSharedPrefs, result: String) {
                settingSharedPrefs.setConfig(RAND_BLOCK_SIZE.name, result)
            }
        }),
    IO_SIZE(R.string.io_size_title, R.string.io_size_desc, object : ISettingData {
        override fun getSettingData(settingSharedPrefs: SettingSharedPrefs): String {
            return settingSharedPrefs.getConfig(IO_SIZE.name, DEFAULT_IO_SIZE_VALUE)
        }

        override fun onDialogContent(
            settingSharedPrefs: SettingSharedPrefs,
            itemIndex: Int,
            closeDialog: (Int, String?) -> Unit
        ): @Composable () -> Unit {
            val currentValue = settingSharedPrefs.getConfig(IO_SIZE.name, DEFAULT_IO_SIZE_VALUE)
            return {
                TextInputDialog(
                    title = IO_SIZE.title,
                    defaultValue = DEFAULT_IO_SIZE_VALUE,
                    currentValue = currentValue,
                    keyboardType = KeyboardType.Number,
                    itemIndex = itemIndex,
                    closeDialog = closeDialog
                )
            }
        }

        override fun setDialogResult(settingSharedPrefs: SettingSharedPrefs, result: String) {
            settingSharedPrefs.setConfig(IO_SIZE.name, result)
        }
    }),
    IO_ENGINE(R.string.engine_config_title, R.string.engine_config_desc, object : ISettingData {
        override fun getSettingData(settingSharedPrefs: SettingSharedPrefs): String {
            return settingSharedPrefs.getConfig(IO_ENGINE.name, DEFAULT_IO_ENGINE_VALUE)
        }

        override fun onDialogContent(
            settingSharedPrefs: SettingSharedPrefs,
            itemIndex: Int,
            closeDialog: (Int, String?) -> Unit
        ): @Composable () -> Unit {
            val engineList: ArrayList<String> = ArrayList()
            val jsonObject = JSONObject(NativeDataSource.native_ListEngines())
            val jsonArray = jsonObject.getJSONArray("engines")
            for (i in 0 until jsonArray.length()) {
                val engineObject: JSONObject = jsonArray.getJSONObject(i)
                val engineItem = engineObject.getString("name")
                val engineAvailable = engineObject.getBoolean("available")
                if (engineAvailable) {
                    engineList.add(engineItem)
                }
            }

            return {
                SingleChoiceDialog(
                    title = IO_ENGINE.title,
                    choiceList = engineList,
                    itemIndex = itemIndex,
                    closeDialog = closeDialog
                )
            }
        }

        override fun setDialogResult(settingSharedPrefs: SettingSharedPrefs, result: String) {
            settingSharedPrefs.setConfig(IO_ENGINE.name, result)
        }
    }),
    NUM_THREADS(R.string.num_threads_title, R.string.num_threads_desc, object : ISettingData {
        override fun getSettingData(settingSharedPrefs: SettingSharedPrefs): String {
            return settingSharedPrefs.getConfig(NUM_THREADS.name, DEFAULT_NUM_THREADS_VALUE)
        }

        override fun onDialogContent(
            settingSharedPrefs: SettingSharedPrefs,
            itemIndex: Int,
            closeDialog: (Int, String?) -> Unit
        ): @Composable () -> Unit {
            val currentValue =
                settingSharedPrefs.getConfig(NUM_THREADS.name, DEFAULT_NUM_THREADS_VALUE)
            return {
                TextInputDialog(
                    title = NUM_THREADS.title,
                    defaultValue = DEFAULT_NUM_THREADS_VALUE,
                    currentValue = currentValue,
                    keyboardType = KeyboardType.Number,
                    itemIndex = itemIndex,
                    closeDialog = closeDialog
                )
            }
        }

        override fun setDialogResult(settingSharedPrefs: SettingSharedPrefs, result: String) {
            settingSharedPrefs.setConfig(NUM_THREADS.name, result)
        }
    }),
    ABOUT_INFO(R.string.about_title, R.string.about_desc, object : ISettingData {
        override fun getSettingData(settingSharedPrefs: SettingSharedPrefs): String {
            return BuildConfig.VERSION_NAME
        }

        override fun onDialogContent(
            settingSharedPrefs: SettingSharedPrefs,
            itemIndex: Int,
            closeDialog: (Int, String?) -> Unit
        ): @Composable () -> Unit {
            return { }
        }

        override fun setDialogResult(settingSharedPrefs: SettingSharedPrefs, result: String) {
        }
    })
}
