package com.thmanyah.thmanyahdemo.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.thmanyah.thmanyahdemo.ui.common.HorizontalSquareList
import com.thmanyah.thmanyahdemo.ui.common.HorizontalTwoLinesGridList
import com.thmanyah.thmanyahdemo.ui.common.QueueHorizontalList
import com.thmanyah.thmanyahdemo.ui.common.ShowHorizontalBigSquareList
import com.thmanyah.thmanyahdemo.ui.common.WelcomeBar
import com.thmanyah.thmanyahdemo.ui.home.models.HomeIntent
import com.thmanyah.thmanyahdemo.ui.models.ThmanyahError
import com.thmanyah.thmanyahdemo.ui.models.UiState
import com.thmanyah.thmanyahdemo.ui.models.home.HomeSectionUiModel
import com.thmanyah.thmanyahdemo.ui.models.home.HomeUiModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (val contentState = state.contentState) {
            is UiState.Init -> {
                Text(
                    text = "Welcome to Thmanyah",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is UiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is UiState.Success, is UiState.LoadMore -> {
                val data = when (contentState) {
                    is UiState.Success -> contentState.data
                    is UiState.LoadMore -> contentState.data
                    else -> return@Box
                }
                Column {
                    WelcomeBar(navController = navController)
                    HomeScreenList(
                        data = data,
                        isLoadingMore = state.isLoadingMore,
                        loadMoreError = state.loadMoreError,
                        onLoadNextPage = { viewModel.onIntent(HomeIntent.LoadNextPage) },
                        onDismissLoadMoreError = { viewModel.onIntent(HomeIntent.DismissLoadMoreError) }
                    )
                }
            }

            is UiState.Error -> {
                val error = contentState.error
                Text(
                    text = error.messageRes?.let { stringResource(id = it) } ?: error.message
                        ?: "An error occurred",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is UiState.Empty -> {
                Text(
                    text = "No data available",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}


@Composable
fun HomeScreenList(
    data: HomeUiModel,
    isLoadingMore: Boolean,
    loadMoreError: ThmanyahError?,
    onLoadNextPage: () -> Unit,
    onDismissLoadMoreError: () -> Unit
) {
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val loadMoreErrorMessage = loadMoreError?.let { err ->
        err.messageRes?.let { stringResource(id = it) } ?: err.message ?: "An error occurred"
    }

    // Show toast when load-more fails; keep list visible and clear error after showing
    LaunchedEffect(loadMoreError) {
        loadMoreErrorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            onDismissLoadMoreError()
        }
    }

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            lastVisibleItemIndex != null &&
                    lastVisibleItemIndex >= totalItemsCount - 3 &&
                    !isLoadingMore
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onLoadNextPage()
        }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(data.sections) { section ->
            when (section) {
                is HomeSectionUiModel.Square -> {
                    HorizontalSquareList(section.items, section.name)
                }

                is HomeSectionUiModel.TwoLinesGrid -> {
                    HorizontalTwoLinesGridList(section.items, section.name)
                }

                is HomeSectionUiModel.BigSquare -> {
                    ShowHorizontalBigSquareList(section.items, section.name)
                }

                is HomeSectionUiModel.Queue -> {
                    QueueHorizontalList(section.items, section.name)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

