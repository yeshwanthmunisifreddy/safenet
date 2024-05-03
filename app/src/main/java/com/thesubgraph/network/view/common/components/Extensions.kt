package com.thesubgraph.network.view.common.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState

fun LazyStaggeredGridState.isScrolledToEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
