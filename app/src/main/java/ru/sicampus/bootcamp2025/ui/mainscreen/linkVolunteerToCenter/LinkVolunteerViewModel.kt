package ru.sicampus.bootcamp2025.ui.mainscreen.linkVolunteerToCenter

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
import ru.sicampus.bootcamp2025.domain.usecases.GetPagedNearestAvailableCentersUseCase
import ru.sicampus.bootcamp2025.domain.usecases.LinkVolunteerUseCase
import ru.sicampus.bootcamp2025.ui.mainscreen.allcenters.CenterListPagingSource

class LinkVolunteerViewModel(
    private val pushUseCase: LinkVolunteerUseCase,
    private val useCasePaged: GetPagedNearestAvailableCentersUseCase,
    private val application: Application
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    private var _profileId: Int? = null
    private val profileId: Int get() = _profileId!!

    fun onLink(centerId: Int) {
        viewModelScope.launch {
            _state.emit(State.Loading)
            pushUseCase.invoke(centerId, profileId).fold(
                onSuccess = { _state.emit(State.Success) },
                onFailure = { error ->
                    _state.emit(State.Error(error.message.toString()))
                }
            )
        }
    }

    val listState = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
            maxSize = 70
        )
    ) {
        CenterListPagingSource(useCasePaged::invoke)
    }.flow
        .cachedIn(viewModelScope)

    fun setProfileId(profileId: Int) {
        _profileId = profileId
    }


    sealed interface State {
        data object Loading : State
        data object Success : State
        data class Error(val text: String) : State
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                val credentialsLocalDataSource = CredentialsLocalDataSource.getInstance()
                val centerRepository = CenterRepositoryImpl(
                    networkDataSource = CenterNetworkDataSource,
                    credentialsLocalDataSource = credentialsLocalDataSource
                )

                return LinkVolunteerViewModel(
                    pushUseCase = LinkVolunteerUseCase(centerRepository),
                    useCasePaged = GetPagedNearestAvailableCentersUseCase(centerRepository),
                    application = application,
                ) as T
            }
        }
    }

}