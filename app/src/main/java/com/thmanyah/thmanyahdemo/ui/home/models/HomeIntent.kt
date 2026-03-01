package com.thmanyah.thmanyahdemo.ui.home.models

sealed interface HomeIntent {
    data object LoadInitial : HomeIntent
    data object LoadNextPage : HomeIntent
    data object DismissLoadMoreError : HomeIntent
}
