package net.readian.parcel.core.designsystem.component

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun PullToRefreshContent(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    refreshing: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    var refreshRequested by remember { mutableStateOf(false) }

    val refreshingState by remember(refreshing, refreshRequested) {
        derivedStateOf { refreshing && refreshRequested }
    }

    val pullRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = refreshingState,
        state = pullRefreshState,
        modifier = modifier,
        onRefresh = {
            refreshRequested = true
            onRefresh()
        },
        content = content,
    )
}
