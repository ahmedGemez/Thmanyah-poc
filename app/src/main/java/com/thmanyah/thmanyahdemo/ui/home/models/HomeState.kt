package com.thmanyah.thmanyahdemo.ui.home.models

import com.thmanyah.thmanyahdemo.ui.models.ThmanyahError
import com.thmanyah.thmanyahdemo.ui.models.UiState
import com.thmanyah.thmanyahdemo.ui.models.home.HomeSectionUiModel
import com.thmanyah.thmanyahdemo.ui.models.home.HomeUiModel

data class HomeState(
    val contentState: UiState<HomeUiModel> = UiState.Init(),
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 1,
    val isLastPage: Boolean = false,
    val currentSections: List<HomeSectionUiModel> = emptyList(),
    val loadMoreError: ThmanyahError? = null
)
