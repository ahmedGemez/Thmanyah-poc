package com.thmanyah.thmanyahdemo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thmanyah.data.di.IODispatcher
import com.thmanyah.domain.usecases.GetHomeDataUseCase
import com.thmanyah.thmanyahdemo.ui.home.models.HomeIntent
import com.thmanyah.thmanyahdemo.ui.home.models.HomeResult
import com.thmanyah.thmanyahdemo.ui.home.models.HomeState
import com.thmanyah.thmanyahdemo.ui.models.home.HomeUiModel
import com.thmanyah.thmanyahdemo.ui.models.UiState
import com.thmanyah.thmanyahdemo.ui.utils.toThmanyahError
import com.thmanyah.thmanyahdemo.ui.utils.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val intentChannel = Channel<HomeIntent>(Channel.UNLIMITED)

    init {
        intentChannel.trySend(HomeIntent.LoadInitial)
        handleIntents()
    }

    private fun reduce(state: HomeState, result: HomeResult): HomeState = when (result) {
        HomeResult.Loading -> state.copy(
            contentState = UiState.Loading(),
            isLoadingMore = true
        )
        is HomeResult.LoadMore -> state.copy(
            contentState = UiState.LoadMore(HomeUiModel(sections = result.sections)),
            isLoadingMore = true
        )
        is HomeResult.Success -> state.copy(
            contentState = UiState.Success(HomeUiModel(sections = result.sections)),
            isLoadingMore = false,
            currentPage = result.page,
            isLastPage = result.isLastPage,
            currentSections = result.sections
        )
        is HomeResult.Error -> state.copy(
            contentState = UiState.Error(result.error),
            isLoadingMore = false
        )
        is HomeResult.LoadMoreError -> state.copy(
            contentState = UiState.Success(HomeUiModel(sections = state.currentSections)),
            isLoadingMore = false,
            loadMoreError = result.error
        )
        HomeResult.ClearLoadMoreError -> state.copy(loadMoreError = null)
    }


    fun onIntent(intent: HomeIntent) {
        intentChannel.trySend(intent)
    }

    private fun handleIntents() {
        viewModelScope.launch {
            intentChannel.receiveAsFlow().collect { intent ->
                when (intent) {
                    HomeIntent.LoadInitial -> loadPage(1)
                    HomeIntent.LoadNextPage -> {
                        val current = _state.value
                        if (!current.isLoadingMore && !current.isLastPage) {
                            loadPage(current.currentPage + 1)
                        }
                    }
                    HomeIntent.DismissLoadMoreError -> dispatch(HomeResult.ClearLoadMoreError)
                }
            }
        }
    }

    private fun dispatch(result: HomeResult) {
        _state.update { reduce(it, result) }
    }

    private fun loadPage(page: Int) {
        getHomeDataUseCase()
            .onStart {
                if (page == 1) {
                    dispatch(HomeResult.Loading)
                } else {
                    dispatch(HomeResult.LoadMore(_state.value.currentSections))
                }
            }
            .onEach { response ->
                val uiModel = response.toUiModel()
                val newSections = if (page == 1) {
                    uiModel.sections
                } else {
                    _state.value.currentSections + uiModel.sections
                }
                val isLastPage = page == response.pagination?.totalPages
                dispatch(HomeResult.Success(sections = newSections, page = page, isLastPage = isLastPage))
            }
            .catch { error ->
                val thmanyahError = error.toThmanyahError()
                if (page > 1) {
                    dispatch(HomeResult.LoadMoreError(thmanyahError))
                } else {
                    dispatch(HomeResult.Error(thmanyahError))
                }
            }
            .flowOn(ioDispatcher)
            .launchIn(viewModelScope)
    }
}