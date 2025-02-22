package ru.sicampus.bootcamp2025.ui.mainscreen.centerinfo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.sicampus.bootcamp2025.data.CenterRepositoryImpl
import ru.sicampus.bootcamp2025.data.ProfileRepositoryImpl
import ru.sicampus.bootcamp2025.data.sources.locale.CredentialsLocalDataSource
import ru.sicampus.bootcamp2025.data.sources.network.CenterNetworkDataSource
import ru.sicampus.bootcamp2025.data.sources.network.ProfileNetworkDataSource
import ru.sicampus.bootcamp2025.domain.entities.FullCenterEntity
import ru.sicampus.bootcamp2025.domain.entities.ProfileEntity
import ru.sicampus.bootcamp2025.domain.usecases.GetCenterByIdUseCase
import ru.sicampus.bootcamp2025.domain.usecases.GetProfileByIdUseCase

class CenterInfoViewModel(
    private val getCenterByIdUseCase: GetCenterByIdUseCase,
    private val getProfileByIdUseCase: GetProfileByIdUseCase,
    private val application: Application
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<State>(State.Loading)
    private var _centerId: Int? = null
    private val centerId: Int get() = _centerId!!
    val state = _state.asStateFlow()

    fun setCenterId(centerId: Int) {
        _centerId = centerId
        updateState(this.centerId)
    }

    private fun updateState(centerId: Int) {
        viewModelScope.launch {
            _state.emit(State.Loading)
            val activeUsers = ArrayList<ProfileEntity>()

            getCenterByIdUseCase.invoke(centerId).fold(
                onSuccess = { data ->
                    data.active?.forEach { id ->
                        getProfileByIdUseCase(id).fold(
                            onSuccess = { profile -> activeUsers.add(profile) },
                            onFailure = { error -> _state.emit(State.Error(error.message.toString())) }
                        )
                    }
                    _state.emit(State.Show(Pair(data, activeUsers)))
                },
                onFailure = { error -> _state.emit(State.Error(error.message.toString())) }
            )
        }
    }

    sealed interface State {
        data object Loading : State
        data class Show(val centerItem: Pair<FullCenterEntity, List<ProfileEntity>>) : State
        data class Error(val text: String) : State
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                val credentialsLocalDataSource = CredentialsLocalDataSource.getInstance()

                return CenterInfoViewModel(
                    getProfileByIdUseCase = GetProfileByIdUseCase(ProfileRepositoryImpl(
                        networkDataSource = ProfileNetworkDataSource,
                        credentialsLocalDataSource = credentialsLocalDataSource
                    )),
                    getCenterByIdUseCase = GetCenterByIdUseCase(
                        centerRepository = CenterRepositoryImpl(
                            networkDataSource = CenterNetworkDataSource,
                            credentialsLocalDataSource = credentialsLocalDataSource
                        ),
                    ),
                    application = application,
                ) as T
            }
        }
    }

}