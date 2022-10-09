package com.droid.kat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KatViewModel @Inject constructor(
    private val catDataUseCase: CatDataUseCase,
    private val exceptionMapper: ExceptionMapper
) : ViewModel() {

    val homeUiState = MutableStateFlow(HomeState())

    init {
        showLoadingState()
    }

    fun getCatData() {
        viewModelScope.launch {
            when (val result = catDataUseCase.fetchCatData()) {
                is Result.Success -> {
                    homeUiState.update { currentState ->
                        currentState.copy(catList = result.data)
                    }
                }
                is Result.Error -> {
                    homeUiState.update { currentState ->
                        currentState.copy(error = exceptionMapper.getExceptionMessage(result.exception))
                    }
                }
            }
        }
    }

    fun showLoadingState() {
        homeUiState.update { currentState ->
            info("UI State") { "show loading state" }
            currentState.copy(loading = true)
        }
    }

    fun clearLoadingState() {
        homeUiState.update { currentState ->
            info("UI State") { "clear loading state" }
            currentState.copy(loading = false)
        }
    }

    fun clearErrorState() {
        homeUiState.update { currentState ->
            info("UI State") { "clear error state" }
            currentState.copy(error = null)
        }
    }


    data class HomeState(
        val loading: Boolean = false,
        val error: String? = null,
        val catList: List<CatData>? = null
    )
}