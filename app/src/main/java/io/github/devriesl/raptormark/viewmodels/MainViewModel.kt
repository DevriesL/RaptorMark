package io.github.devriesl.raptormark.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.devriesl.raptormark.data.network.Contributor
import io.github.devriesl.raptormark.data.network.GitHubService
import io.github.devriesl.raptormark.data.network.LoadState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val gitHubService: GitHubService
) : ViewModel() {
    var contributorList by mutableStateOf(emptyList<Contributor>())
        private set

    var loadState by mutableStateOf(LoadState.NotLoad)
        private set

    fun updateContributors() {
        viewModelScope.launch(
            context = CoroutineExceptionHandler { _, _ ->
                loadState = LoadState.Failed
            }
        ) {
            loadState = LoadState.Loading
            contributorList = gitHubService.getContributors().sortedBy {
                it.contributions
            }
            loadState = LoadState.NotLoad
        }
    }
}