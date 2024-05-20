package com.thesubgraph.network.viewmodel.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesubgraph.network.domain.usecase.PhotoUseCase
import com.thesubgraph.network.domain.model.Photo
import com.thesubgraph.network.view.common.PaginationState
import com.thesubgraph.network.view.common.ViewState
import com.thesubgraph.safenet.data.common.ValueResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val photoUseCase: PhotoUseCase,
) : ViewModel() {
    private val _viewState: MutableStateFlow<ViewState<Unit>> =
        MutableStateFlow(ViewState.Initial)
    val viewState by lazy { _viewState.asStateFlow() }
    private val _photos: MutableStateFlow<List<Photo>> = MutableStateFlow(listOf())
    val photos by lazy { _photos.asStateFlow() }
    private var pagination = PaginationState()

    init {
        getPhotos()
    }

    fun getPhotos(refresh: Boolean = false) {
        viewModelScope.launch {
            _viewState.value = ViewState.Loading
            if (refresh) {
                pagination = PaginationState()
                _photos.value = listOf()
            }
            if (pagination.reachedEnd) {
                return@launch
            }
            val currentPage = pagination.page
            val currentSize = _photos.value.size
            photoUseCase.execute(page = currentPage, pageSize = pagination.pageSize)
                .flowOn(Dispatchers.IO)
                .collectLatest { result ->
                    when (result) {
                        is ValueResult.Success -> {
                            val currentPhotos = _photos.value.take(currentSize)
                            val duplicatePhotos =
                                result.data.filter { photo -> currentPhotos.any { it.id == photo.id } }
                            val filteredPhotos =
                                result.data.filter { photo -> !duplicatePhotos.any { it.id == photo.id } }
                            val updatedPhotos = currentPhotos.map { oldPhoto ->
                                duplicatePhotos.firstOrNull { it.id == oldPhoto.id }
                                    ?: oldPhoto
                            }
                            _photos.value = updatedPhotos + filteredPhotos
                            _viewState.value = ViewState.Success(Unit)
                            pagination = if (result.data.isEmpty()) {
                                pagination.copy(reachedEnd = true)
                            } else {
                                pagination.copy(page = currentPage + 1)
                            }
                        }

                        is ValueResult.Failure -> {
                            _viewState.value = ViewState.Error(result.error)
                        }
                    }
                }
        }
    }
}