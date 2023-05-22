@file:OptIn(ExperimentalFoundationApi::class)

package com.paperscan.usecase.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paperscan.usecase.ANIMATION_FADE_IN_OUT
import com.paperscan.usecase.GRID_COLUMNS
import com.paperscan.usecase.TILE_CELL_ANIMATION
import com.paperscan.usecase.modelViews.PaperScanViewModel
import com.paperscan.usecase.models.Tile
import com.paperscan.usecase.ui.theme.SelectedTile
import com.paperscan.usecase.ui.theme.UnSelectedTile
import com.paperscan.usecase.ui.theme.gridPadding
import com.paperscan.usecase.ui.theme.tileHeight
import com.paperscan.usecase.ui.theme.tileIdPadding
import com.paperscan.usecase.ui.theme.tileIdSize
import com.paperscan.usecase.ui.theme.tilePaddingBottom
import com.paperscan.usecase.ui.theme.tilePaddingTop
import com.paperscan.usecase.ui.theme.tileWidth


/***
 * View Grid Layout Implementation
 * ***/
@Composable
fun tileGridLayout(viewModel: PaperScanViewModel) {
    /***
     *1. Screen containing 10 rectangle tiles arranged in a grid pattern consisting of 2 columns.
     * ***/
    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMNS),
        modifier = Modifier
            .fillMaxSize()
            .padding(gridPadding),
        content = {
            items(viewModel.tilesList.size) { index ->
                val tile = viewModel.tilesList[index]
                tileCell(viewModel = viewModel, tile)
            }
        }
    )
}

/***
 * View Cell Implementation
 * ***/
@Composable
fun tileCell(viewModel: PaperScanViewModel, tile: Tile) {

    var calculatedOffset = Offset.Zero
    if (viewModel.handleTiles(tile)) {
        calculatedOffset =
            viewModel.selectedtilesList.firstOrNull()?.let { viewModel.MoveTiles(it, tile) }!!
    }

    /**
     * Moving tiles Animation.
     * */
    val targetOffset: Offset by animateOffsetAsState(
        targetValue = if (viewModel.handleTiles(tile)) calculatedOffset else Offset.Zero,
        animationSpec = tween(durationMillis = TILE_CELL_ANIMATION, easing = FastOutSlowInEasing)
    )

    val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(animationSpec = TweenSpec(ANIMATION_FADE_IN_OUT, 0)),
        exit = fadeOut(animationSpec = TweenSpec(ANIMATION_FADE_IN_OUT, 0, FastOutLinearInEasing))
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .onGloballyPositioned {
                    tile.positionOffset =
                        Offset(x = it.positionInRoot().x, y = it.positionInRoot().y)
                }
                .offset(targetOffset.x.dp, targetOffset.y.dp)
                .padding(top = tilePaddingTop, bottom = tilePaddingBottom)
                /***
                 *2. Tiles can be selected by tapping on them (indicated by darker background color)
                 * ***/
                .clickable {
                    if (viewModel.isTileSelected(tile)) {
                        viewModel.deselectTile(tile)
                    } else {
                        viewModel.selectTile(tile)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                /***
                 * 1. Each tile is 100x150 px
                 * ***/
                modifier = Modifier
                    .width(tileWidth)
                    .height(tileHeight)
                    .background(if (viewModel.isTileSelected(tile)) SelectedTile else UnSelectedTile)
            ) {
                Text(
                    text = tile.item.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = tileIdSize,
                    modifier = Modifier.padding(tileIdPadding),
                )
            }
        }

    }

}

/***
 * Preview tileGridLayout
 * ***/
@Preview(showBackground = true)
@Composable
fun tileGridLayoutPreview() {
    tileGridLayout(viewModel = PaperScanViewModel())
}