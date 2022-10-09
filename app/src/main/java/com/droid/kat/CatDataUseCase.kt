package com.droid.kat

import javax.inject.Inject

class CatDataUseCase @Inject constructor(private val katAPIService: KatAPIService) {

    suspend fun fetchCatData(totalItems: Int, pageNumber: Int): Result<List<CatData>> {
        return try {
            val result = katAPIService.fetchCatData(pageNumber = pageNumber, pageLimit = totalItems)
            Result.Success(result)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }
}