package com.thmanyah.thmanyahdemo.ui.home.models

import com.thmanyah.thmanyahdemo.ui.models.ThmanyahError
import com.thmanyah.thmanyahdemo.ui.models.home.HomeSectionUiModel

sealed class HomeResult {
    data object Loading : HomeResult()
    data class LoadMore(val sections: List<HomeSectionUiModel>) : HomeResult()
    data class Success(
        val sections: List<HomeSectionUiModel>,
        val page: Int,
        val isLastPage: Boolean
    ) : HomeResult()
    data class Error(val error: ThmanyahError) : HomeResult()
    data class LoadMoreError(val error: ThmanyahError) : HomeResult()
    data object ClearLoadMoreError : HomeResult()
}
