package ru.sicampus.bootcamp2025.ui.mainscreen.allcenters

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
import ru.sicampus.bootcamp2025.data.CenterRepositoryImpl
import ru.sicampus.bootcamp2025.data.sources.locale.CredentialsLocalDataSource
import ru.sicampus.bootcamp2025.data.sources.network.CenterNetworkDataSource
import ru.sicampus.bootcamp2025.domain.entities.CenterMapEntity
import ru.sicampus.bootcamp2025.domain.usecases.GetCentersUseCase
import ru.sicampus.bootcamp2025.domain.usecases.GetPagedNearestAvailableCentersUseCase


class AllCentersViewModel(
    private val useCaseAll: GetCentersUseCase,
    private val useCasePaged: GetPagedNearestAvailableCentersUseCase,
    private val application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    val listState = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
            maxSize = 50
        )
    ) {
        CenterListPagingSource(useCasePaged::invoke)
    }.flow
        .cachedIn(viewModelScope)

    fun onMapSelected() {
        updateState()
    }

    private fun updateState() {
        viewModelScope.launch {
            _state.emit(State.Loading)
            _state.emit(useCaseAll.invoke().fold(
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
        data class Show(val content: List<CenterMapEntity>) : State
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!

                val centerRepository = CenterRepositoryImpl(
                    networkDataSource = CenterNetworkDataSource,
                    credentialsLocalDataSource = CredentialsLocalDataSource.getInstance()
                )

                return AllCentersViewModel(
                    useCaseAll = GetCentersUseCase(centerRepository),
                    useCasePaged = GetPagedNearestAvailableCentersUseCase(centerRepository),
                    application = application
                ) as T
            }
        }
    }
}