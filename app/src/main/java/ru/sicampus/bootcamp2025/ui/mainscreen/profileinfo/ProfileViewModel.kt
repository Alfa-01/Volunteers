package ru.sicampus.bootcamp2025.ui.mainscreen.profileinfo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.sicampus.bootcamp2025.data.ProfileRepositoryImpl
import ru.sicampus.bootcamp2025.data.UserRepositoryImpl
import ru.sicampus.bootcamp2025.data.sources.locale.CredentialsLocalDataSource
import ru.sicampus.bootcamp2025.data.sources.locale.UserLocalDataSource
import ru.sicampus.bootcamp2025.data.sources.network.ProfileNetworkDataSource
import ru.sicampus.bootcamp2025.data.sources.network.UserNetworkDataSource
import ru.sicampus.bootcamp2025.domain.entities.ProfileEntity
import ru.sicampus.bootcamp2025.domain.entities.UserEntity
import ru.sicampus.bootcamp2025.domain.usecases.GetLocalUserUseCase
import ru.sicampus.bootcamp2025.domain.usecases.GetProfileByIdUseCase
import ru.sicampus.bootcamp2025.domain.usecases.LogoutUseCase
import ru.sicampus.bootcamp2025.domain.usecases.UpdateProfileUseCase

class ProfileViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val getLocalUserUseCase: GetLocalUserUseCase,
    private val getProfileByIdUseCase: GetProfileByIdUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val application: Application
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    private var _currentUser: UserEntity? = null
    private val currentUser: UserEntity get() = _currentUser!!

    init {
        viewModelScope.launch {

            _currentUser = getLocalUserUseCase.invoke()
            updateState(currentUser.profileId)
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            logoutUseCase.invoke()
            _state.emit(State.Logout)
        }
    }

    fun onSaveChanges(newProfile: ProfileEntity) {
        viewModelScope.launch {
            _state.emit(State.Loading)
            updateProfileUseCase.invoke(newProfile).fold(
                onSuccess = { updateState(currentUser.profileId) },
                onFailure = { error -> _state.emit(State.Error(error.message.toString())) }
            )
        }

    }

    fun onRefresh() {
        viewModelScope.launch {
            _state.emit(State.Loading)
            updateState(currentUser.profileId)
        }
    }

    private fun updateState(userId: Int) {
        viewModelScope.launch {
            _state.emit(State.Loading)
            getProfileByIdUseCase.invoke(userId).fold(
                onSuccess = { data -> _state.emit(State.Show(data)) },
                onFailure = { error -> _state.emit(State.Error(error.message.toString())) }
            )
        }
    }

    sealed interface State {
        data object Loading : State
        data class Show(val profile: ProfileEntity) : State
        data class Error(val text: String) : State
        data object Logout : State
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                val credentialsLocalDataSource = CredentialsLocalDataSource.getInstance()
                val userRepository = UserRepositoryImpl(
                    userLocalDataSource = UserLocalDataSource,
                    credentialsLocalDataSource = credentialsLocalDataSource,
                    userNetworkDataSource = UserNetworkDataSource
                )

                return ProfileViewModel(
                    logoutUseCase = LogoutUseCase(repository = userRepository),
                    getProfileByIdUseCase = GetProfileByIdUseCase(
                        repository = ProfileRepositoryImpl(
                            networkDataSource = ProfileNetworkDataSource,
                            credentialsLocalDataSource = credentialsLocalDataSource
                        ),
                    ),
                    getLocalUserUseCase = GetLocalUserUseCase(userRepository = userRepository),
                    updateProfileUseCase = UpdateProfileUseCase(
                        ProfileRepositoryImpl(
                            networkDataSource = ProfileNetworkDataSource,
                            credentialsLocalDataSource = credentialsLocalDataSource
                        )
                    ),
                    application = application
                ) as T
            }
        }
    }

}