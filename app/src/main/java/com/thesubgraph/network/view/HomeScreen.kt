package com.thesubgraph.network.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.thesubgraph.network.ui.theme.Grey500
import com.thesubgraph.network.ui.theme.TextStyle_Size18_Weight400
import com.thesubgraph.network.domain.model.Photo
import com.thesubgraph.network.view.common.Router
import com.thesubgraph.network.view.common.ViewState
import com.thesubgraph.network.view.common.components.DefaultAppBar
import com.thesubgraph.network.view.common.components.ErrorContent
import com.thesubgraph.network.view.common.components.FullPageCircularLoader
import com.thesubgraph.network.view.common.components.isScrolledToEnd
import com.thesubgraph.network.view.common.components.showToast
import com.thesubgraph.network.viewmodel.screens.HomeViewModel

@Composable
fun HomeScreen(route: Router, viewModel: HomeViewModel) {
    val photos = viewModel.photos.collectAsState()
    val viewState = viewModel.viewState.collectAsState()

    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
            .background(Color.White),
        topBar = {
            DefaultAppBar(
                title = {
                    Text(
                        text = "WallWrap",
                        style = TextStyle_Size18_Weight400,
                        color = Grey500
                    )
                }
            )
        }
    ) { paddingValues ->
        ScreenContent(
            paddingValues = paddingValues,
            photos = photos,
            viewState = viewState,
            onFetchMore = { refresh ->
                viewModel.getPhotos(refresh)
            })
    }
}

@Composable
private fun ScreenContent(
    paddingValues: PaddingValues,
    photos: State<List<Photo>>,
    viewState: State<ViewState<Unit>>,
    onFetchMore: (refresh: Boolean) -> Unit
) {
    ShowErrorToast(viewState = viewState, photos = photos)
    LoadingAndError(
        viewState = viewState,
        photos = photos,
        paddingValues = paddingValues,
        onFetchMore = onFetchMore
    )
    Content(
        viewState = viewState,
        photos = photos,
        onFetchMore = onFetchMore,
        paddingValues = paddingValues
    )
}

@Composable
private fun LoadingAndError(
    viewState: State<ViewState<Unit>>,
    photos: State<List<Photo>>,
    paddingValues: PaddingValues,
    onFetchMore: (refresh: Boolean) -> Unit
) {
    when (val state = viewState.value) {
        ViewState.Loading -> {
            if (photos.value.isEmpty()) {
                FullPageCircularLoader()
            }
        }

        is ViewState.Error -> {
            if (photos.value.isEmpty()) {
                ErrorContent(
                    paddingValues = paddingValues,
                    errorMessage = state.error.message,
                    onRetryClick = {
                        onFetchMore(true)
                    })
            }
        }

        else -> Unit
    }
}

@Composable
private fun ShowErrorToast(
    viewState: State<ViewState<Unit>>,
    photos: State<List<Photo>>
) {
    val context = LocalContext.current
    LaunchedEffect(viewState.value) {
        when (val state = viewState.value) {
            is ViewState.Error -> {
                if (photos.value.isNotEmpty()) {
                    context.showToast(state.error.fullMessage)
                }
            }

            else -> Unit
        }
    }
}

@Composable
private fun Content(
    viewState: State<ViewState<Unit>>,
    photos: State<List<Photo>>,
    onFetchMore: (refresh: Boolean) -> Unit,
    paddingValues: PaddingValues
) {
    if (viewState.value !is ViewState.Error || photos.value.isNotEmpty()) {
        Column {
            PhotosView(
                modifier = Modifier.weight(1f),
                photos = photos,
                onFetchMore = onFetchMore,
                paddingValues = paddingValues
            )
            if (viewState.value is ViewState.Loading && photos.value.isNotEmpty())
                Box(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(64.dp)
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )
                }
        }
    }
}

@Composable
private fun PhotosView(
    modifier: Modifier = Modifier,
    photos: State<List<Photo>>,
    onFetchMore: (refresh: Boolean) -> Unit,
    paddingValues: PaddingValues
) {
    val listState = rememberLazyStaggeredGridState()
    val scrolledToEnd by remember { derivedStateOf { listState.isScrolledToEnd() } }

    LaunchedEffect(key1 = scrolledToEnd) {
        if (photos.value.isNotEmpty() && scrolledToEnd) {
            onFetchMore(false)
        }
    }
    LazyVerticalStaggeredGrid(
        modifier = modifier
            .padding(paddingValues)
            .fillMaxSize(),
        state = listState,
        columns = StaggeredGridCells.Fixed(count = 2),
        verticalItemSpacing = 5.dp,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        contentPadding = PaddingValues(start = 5.dp, end = 5.dp)
    ) {
        items(items = photos.value, key = { it.id }) { photo ->
            photo.imageUrls?.let { imageUrls ->
                val color = Color(android.graphics.Color.parseColor(photo.color))
                val aspectRatio = photo.width.toFloat() / photo.height.toFloat()

                SubcomposeAsyncImage(
                    model = imageUrls.thumb,
                    loading = {
                        Box(
                            modifier = Modifier
                                .background(color = color, shape = RoundedCornerShape(8.dp))
                                .aspectRatio(aspectRatio)
                                .fillMaxSize()
                        )
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .background(color = color, shape = RoundedCornerShape(8.dp))
                                .aspectRatio(aspectRatio)
                                .fillMaxSize()
                        )
                    },

                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                    modifier = Modifier.aspectRatio(aspectRatio).clip(RoundedCornerShape(8.dp)),
                )
            }
        }
    }
}

