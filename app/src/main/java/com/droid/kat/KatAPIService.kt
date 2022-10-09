package com.droid.kat

import retrofit2.http.GET
import retrofit2.http.Query

interface KatAPIService {

    @GET("v1/images/search")
    suspend fun fetchCatData(
        @Query("page") pageNumber: Int,
        @Query("has_breeds") hasBreeds: Boolean = true,
        @Query("limit") pageLimit: Int = 20,
    ): Unit
}