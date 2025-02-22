package ru.sicampus.bootcamp2025.ui.entry.signup

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
import ru.sicampus.bootcamp2025.domain.usecases.IsUserExistUseCase
import ru.sicampus.bootcamp2025.domain.usecases.RegisterUseCase

class SignUpViewModel(
    private val registerUseCase: RegisterUseCase,
    private val isUserExistUseCase: IsUserExistUseCase,
    private val application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<State>(State.Waiting)
    private val _action = Channel<Action>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val state = _state.asStateFlow()
    val action = _action.receiveAsFlow()

    fun onProcessClick(login: String, password: String, name: String, lastname: String) {

        viewModelScope.launch {
            _state.emit(State.Loading)
            isUserExistUseCase.invoke(login).fold(
                onSuccess = { answer ->
                    if (answer) {
                        goToLogin()
                    } else {
                        registerUseCase.invoke(login, password, name, lastname).fold(
                            onSuccess = { openApp() },
                            onFailure = { error ->
                                _state.emit(State.Error(error.message.toString()))
                            }
                        )
                    }
                },
                onFailure = { error -> _state.emit(State.Error(error.message.toString())) }
            )
        }
    }

    private fun openApp() {
        viewModelScope.launch {
            _action.send(Action.OpenApp)
        }
    }

    private fun goToLogin() {
        viewModelScope.launch {
            _action.send(Action.GoToLogin)
        }
    }

    sealed interface State {
        data object Waiting : State
        data object Loading : State
        data class Error(val errorMessage: String) : State
    }

    sealed interface Action {
        data object OpenApp : Action
        data object GoToLogin: Action
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

                return SignUpViewModel(
                    registerUseCase = RegisterUseCase(repository),
                    isUserExistUseCase = IsUserExistUseCase(repository),
                    application = application
                ) as T
            }
        }
    }
}