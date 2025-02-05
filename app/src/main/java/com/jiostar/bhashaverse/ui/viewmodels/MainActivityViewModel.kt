package com.jiostar.bhashaverse.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jiostar.bhashaverse.domain.usecase.GetUsersUseCase
import com.jiostar.bhashaverse.ui.state.MainScreenState
import com.jiostar.bhashaverse.ui.state.MainScreenState.Companion.MainScreenStateDummyData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    private val _mainScreenState = MutableStateFlow<MainScreenState>(MainScreenStateDummyData)
    val mainScreenState: StateFlow<MainScreenState> = _mainScreenState

    init {
        fetchUsers()
    }

    fun fetchUsers() {

        viewModelScope.launch {
            _mainScreenState.update { it.copy(isLoading = true) }
            getUsersUseCase(
                onSuccess = { users ->
                    _mainScreenState.update { it.copy(isLoading = false) }
                },
                onFailure = { message ->
                    _mainScreenState.update { it.copy(isLoading = false, errorMessage = message) }
                }
            )
        }
    }
}

