package ru.sicampus.bootcamp2025.ui.entry

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.sicampus.bootcamp2025.data.UserRepositoryImpl
import ru.sicampus.bootcamp2025.data.sources.locale.CredentialsLocalDataSource
import ru.sicampus.bootcamp2025.data.sources.locale.UserLocalDataSource
import ru.sicampus.bootcamp2025.data.sources.network.UserNetworkDataSource
import ru.sicampus.bootcamp2025.domain.usecases.AuthorizeUseCase
import ru.sicampus.bootcamp2025.domain.usecases.GetCurrentUserCredentialsUseCase

class AuthViewModel(
    private val authorizeUseCase: AuthorizeUseCase,
    private val getCurrentUserCredentialsUseCase: GetCurrentUserCredentialsUseCase,
    private val application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<State>(State.Loading)
    private val _action = Channel<Action>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val state = _state.asStateFlow()
    val action = _action.receiveAsFlow()

    init {
        viewModelScope.launch {
            _state.emit(State.Loading)
            updateState()
        }
    }

    private fun updateState() {
        viewModelScope.launch {
            _state.emit(State.Loading)
            getCurrentUserCredentialsUseCase.invoke().fold(
                onSuccess = { currentUser ->
                    _state.emit(State.DataReady)
                    authorizeUseCase.invoke(currentUser).fold(
                        onSuccess = { openApp() },
                        onFailure = { goToLogin() }
                    )
                },
                onFailure = {
                    _state.emit(State.DataReady)
                    goToSignUp()
                }
            )
        }
    }

    private fun openApp() {
        viewModelScope.launch {
            if (_state.value !is State.Loading)
                _action.send(Action.OpenApp)
        }
    }

    private fun goToLogin() {
        viewModelScope.launch {
            if (_state.value !is State.Loading)
                _action.send(Action.GoToLogin)
        }
    }

    private fun goToSignUp() {
        viewModelScope.launch {
            if (_state.value !is State.Loading)
                _action.send(Action.GotToSignUp)
        }
    }

    sealed interface State {
        data object Loading : State
        data object DataReady : State
        data object Error : State
    }

    sealed interface Action {
        data object OpenApp : Action
        data object GoToLogin : Action
        data object GotToSignUp : Action
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                val repository = UserRepositoryImpl(
                    userLocalDataSource = UserLocalDataSource,
                    credentialsLocalDataSource = CredentialsLocalDataSource.getInstance(),
                    userNetworkDataSource = UserNetworkDataSource
                )

                return AuthViewModel(
                    authorizeUseCase = AuthorizeUseCase(repository),
                    getCurrentUserCredentialsUseCase = GetCurrentUserCredentialsUseCase(repository),
                    application = application
                ) as T
            }
        }
    }
}