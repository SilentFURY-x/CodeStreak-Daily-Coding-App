package com.fury.codestreak.data.model

import com.google.gson.annotations.SerializedName

// The wrapper response from Codeforces
data class CodeforcesResponse(
    val status: String,
    val result: List<CodeforcesUser>?
)

// The actual user data we want
data class CodeforcesUser(
    val handle: String,
    val rating: Int?,
    val rank: String?,
    val maxRating: Int?,
    @SerializedName("titlePhoto") val avatar: String // URL to their image
)