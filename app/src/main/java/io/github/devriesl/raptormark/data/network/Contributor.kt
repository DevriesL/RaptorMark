package io.github.devriesl.raptormark.data.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Contributor(
    @SerializedName("login") val userName: String,
    val id: Int,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("html_url") val userUrl: String,
    val type: String,
    val contributions: Int
) : Serializable