package ru.sicampus.bootcamp2025.ui.mainscreen.allusers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.sicampus.bootcamp2025.data.ProfileRepositoryImpl
import ru.sicampus.bootcamp2025.data.sources.locale.CredentialsLocalDataSource
import ru.sicampus.bootcamp2025.data.sources.network.ProfileNetworkDataSource
import ru.sicampus.bootcamp2025.domain.entities.ProfileEntity
import ru.sicampus.bootcamp2025.domain.usecases.GetAllFreeProfilesUseCase
import ru.sicampus.bootcamp2025.domain.usecases.GetAllProfilesUseCase

class AllUsersViewModel(
    private val useCaseFree: GetAllFreeProfilesUseCase,
    private val useCasePaged: GetAllProfilesUseCase,
    private val application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    init {
        updateState()
    }

    val listState = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
            maxSize = 70
        )
    ) {
        UsersPagingSource(useCasePaged::invoke)
    }.flow
        .cachedIn(viewModelScope)

    private fun updateState() {
        viewModelScope.launch {
            _state.emit(State.Loading)
            _state.emit(useCaseFree.invoke().fold(
                onSuccess = { centerList ->
                    State.Show(centerList)
                },
                onFailure = { error ->
                    State.Error(error.message.toString())
                }
            ))
        }
    }

    sealed interface State {
        data object Loading : State
        data class Error(val error: String) : State
        data class Show(val content: List<ProfileEntity>) : State
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!

                val repository = ProfileRepositoryImpl(
                    networkDataSource = ProfileNetworkDataSource,
                    credentialsLocalDataSource = CredentialsLocalDataSource.getInstance()
                )

                return AllUsersViewModel(
                    useCaseFree = GetAllFreeProfilesUseCase(repository),
                    useCasePaged = GetAllProfilesUseCase(repository),
                    application = application
                ) as T
            }
        }
    }
}