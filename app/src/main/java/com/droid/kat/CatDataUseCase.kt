package com.droid.kat

import javax.inject.Inject

class CatDataUseCase @Inject constructor(private val katAPIService: KatAPIService) {

    suspend fun fetchCatData(): Result<List<CatData>> {
        return try {
            val result = katAPIService.fetchCatData(pageNumber = 1, pageLimit = 20)
            Result.Success(result)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }
}