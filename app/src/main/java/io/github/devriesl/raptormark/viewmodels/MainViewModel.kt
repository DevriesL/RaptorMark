package io.github.devriesl.raptormark.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.devriesl.raptormark.data.network.Contributor
import io.github.devriesl.raptormark.data.network.GitHubService
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val gitHubService: GitHubService
) : ViewModel() {
    var contributorList by mutableStateOf(emptyList<Contributor>())

    fun updateContributors() {
        viewModelScope.launch {
            contributorList = gitHubService.getContributors().sortedBy {
                it.contributions
            }
        }
    }
}