package io.github.devriesl.raptormark.data.network

import retrofit2.http.GET

interface GitHubService {
    @GET("/repos/DevriesL/RaptorMark/contributors")
    suspend fun getContributors(): List<Contributor>
}
