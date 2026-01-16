package com.fury.codestreak.data.remote

import com.fury.codestreak.data.model.CodeforcesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CodeforcesApi {
    @GET("user.info")
    suspend fun getUserInfo(
        @Query("handles") handle: String
    ): CodeforcesResponse
}