package ru.sicampus.bootcamp2025.ui.mainscreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.sicampus.bootcamp2025.data.RolesRepositoryImpl
import ru.sicampus.bootcamp2025.data.UserRepositoryImpl
import ru.sicampus.bootcamp2025.data.sources.locale.CredentialsLocalDataSource
import ru.sicampus.bootcamp2025.data.sources.locale.UserLocalDataSource
import ru.sicampus.bootcamp2025.data.sources.network.RoleNetworkDataSource
import ru.sicampus.bootcamp2025.data.sources.network.UserNetworkDataSource
import ru.sicampus.bootcamp2025.domain.usecases.GetLocalUserUseCase
import ru.sicampus.bootcamp2025.domain.usecases.IsRoleHaveAdminPermissionsUseCase

class AppConfigViewModel(
    private val application: Application,
    private val isRoleHaveAdminPermissionsUseCase: IsRoleHaveAdminPermissionsUseCase,
    private val getLocalUserUseCase: GetLocalUserUseCase
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    init {
        updateState()
    }

    private fun updateState() {
        viewModelScope.launch {
            _state.emit(State.Loading)
            _state.emit(
                isRoleHaveAdminPermissionsUseCase.invoke(
                    getLocalUserUseCase.invoke()!!.roleId
                ).fold(
                    onSuccess = { response -> State.Answer(response) },
                    onFailure = { error -> State.Error(error.message.toString()) }
                )
            )
        }
    }


    sealed interface State {
        data object Loading : State
        data class Error(val error: String) : State
        data class Answer(val answer: Boolean) : State
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                val credentialsLocalDataSource = CredentialsLocalDataSource.getInstance()
                val repository = UserRepositoryImpl(
                    userLocalDataSource = UserLocalDataSource,
                    credentialsLocalDataSource = credentialsLocalDataSource,
                    userNetworkDataSource = UserNetworkDataSource
                )
                return AppConfigViewModel(
                    getLocalUserUseCase = GetLocalUserUseCase(
                        repository
                    ),
                    isRoleHaveAdminPermissionsUseCase = IsRoleHaveAdminPermissionsUseCase(
                        RolesRepositoryImpl(RoleNetworkDataSource, credentialsLocalDataSource)
                    ),
                    application = application
                ) as T
            }
        }
    }
}