package com.paperscan.usecase.modelViews

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperscan.usecase.utils.DELAY_ANIMATION_TRANSITION
import com.paperscan.usecase.utils.DELAY_FETCHING_TILES_BY_ROW
import com.paperscan.usecase.utils.MAX_TILES_COUNT
import com.paperscan.usecase.models.Tile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/***
 * ModelView Class
 ****/

const val LOG_TAG = "PaperScanViewModel"

class PaperScanViewModel : ViewModel() {

    var tilesList = mutableStateListOf<Tile>()
    var selectedtilesList = mutableStateListOf<Tile>()
    var buttonPressed = mutableStateOf(false)


    /***
     *  1. load number of (MAX_TILES_COUNT=10) tiles into a list
     ****/
    init {
        viewModelScope.launch {

            tilesList.clear()
            selectedtilesList.clear()

            (1..MAX_TILES_COUNT).forEach {
                tilesList.add(Tile(it, -1))
                delay(DELAY_FETCHING_TILES_BY_ROW)
            }
            Log.d(LOG_TAG, "LOADED LIST : ${tilesList.toList()}")
        }
    }


    /***
     *  2. Tiles can be selected by tapping on them (indicated by darker background color).
     *
     * Set the 1st selected Tile
     * Set SelectedPosition
     *
     *
     ****/
    fun selectTile(tile: Tile) {
        if (!selectedtilesList.contains(tile)) {
            selectedtilesList.add(tile)
        }

        selectedtilesList.forEach {
            tile.selectedPosition = tilesList.indexOf(it)
        }

        Log.d(LOG_TAG, "SELECTED LIST : ${selectedtilesList.toList()}")

    }

    /***
     * Tiles can be deselected and back to the default color by tapping on selected tiles)
     */
    fun deselectTile(tile: Tile) {
        selectedtilesList.remove(tile)
        Log.d(LOG_TAG, "UPDATED SELECTED LIST : ${selectedtilesList.toList()}")
    }

    /***
     * Pressing on the merge button
     */
    fun mergeTiles() {

        viewModelScope.launch {

            buttonPressed.value = true

            val copyselectedTilesList = selectedtilesList.toList()
            var savelatestTile: Tile?


            val defaultTileSize = tilesList.size

            delay(DELAY_ANIMATION_TRANSITION)

            /***
             * 4. All selected tiles but the first one are removed from the grid.
             * ***/
            for (i in selectedtilesList.size - 1 downTo 1) {
                val tile = copyselectedTilesList[i]

                savelatestTile = tilesList.get(defaultTileSize - 1)

                tilesList.remove(tile)
                Log.d(LOG_TAG, "REMOVED TILE : ${tile}")

                Log.d(
                    LOG_TAG,
                    "MERGED TILES : ${copyselectedTilesList.toList()} INTO TILE NUMBER : ${
                        copyselectedTilesList.get(
                            0
                        ).item
                    }"
                )

                Log.d(
                    LOG_TAG,
                    "LAST TILE : ${savelatestTile}}"
                )

                delay(DELAY_ANIMATION_TRANSITION)

                buttonPressed.value = false


                /***
                 *
                 * 4. At the same time new tiles are added at the end of the grid with
                fade-in animation to keep the overall number of tiles equal to 10.
                 * ***/


                val newTilesNumber = defaultTileSize - tilesList.size



                for (i in 0 until newTilesNumber) {
                    val maxOfTiles = tilesList.maxByOrNull { it.item }

                    maxOfTiles?.let {

                        if (savelatestTile.item != maxOfTiles.item) {

                            tilesList.add(Tile(item = savelatestTile.item + 1, -1))
                        } else {
                            tilesList.add(Tile(item = maxOfTiles.item + 1, -1))
                        }
                    }

                    Log.d(LOG_TAG, "MAX TIMES ${maxOfTiles}")
                    delay(DELAY_ANIMATION_TRANSITION)
                }

                Log.d(LOG_TAG, "ADDED NEW ${newTilesNumber} TILES INTO : ${tilesList.toList()}")

                /***
                 * At the same time the first selected tile is deselected (color is animated back to the default
                one).
                 ****/

                selectedtilesList.clear()

                Log.d(LOG_TAG, "OLD SELECTED LIST IS CLEARED : ${selectedtilesList.toList()}")

            }
        }
    }

    /***
     * Check if the tile is Selected
     */
    fun isTileSelected(tile: Tile): Boolean {
        return selectedtilesList.contains(tile)
    }

    /***
     * get first Selected Tile
     */
    fun firstSelectedTile(): Tile? {
        return selectedtilesList.firstOrNull()
    }

    /***
     * Check if the tile is not first in the selected tiles list
     */
    fun isNotFirst(tile: Tile): Boolean {
        return selectedtilesList.indexOf(tile) > 0
    }

    /***
     * Check if there is tiles, tile is selected and is not first
     */
    fun handleTiles(tile: Tile): Boolean {
        if (buttonPressed.value && isTileSelected(tile) && isNotFirst(tile) && firstSelectedTile() != null
        ) {
            return true
        } else {
            return false
        }
    }


    /***
     * The rest tiles are compacted right to left and bottom to top to get rid of
    the holes appeared due to removing tiles.

     *The compaction must be animated (moving tiles).
    Take care that the tiles are moved by keeping their order, i.e. if a tile left to given one is
    removed the tile moves left,

     * if the rightmost tile in a row is removed the leftmost tile in the row below that moved into the position;
    tile movements are accumulated – i.e. if 2 tiles before a given one are removed the tile need to move 2 places to the left / previous row.
     * ***/

    fun MoveTiles(firstSelectedTile: Tile, tile: Tile): Offset {
        var newTilesOffset = Offset.Unspecified

        Log.d(
            LOG_TAG,
            "**************************************************************************************"
        )

        Log.d(
            LOG_TAG,
            "FIRST SELECTED TILE ITEM : ${firstSelectedTile.item}, POSITON : ${firstSelectedTile.selectedPosition} "
        )

        Log.d(LOG_TAG, "TILE ITEM : ${tile.item}, POSITON : ${tile.selectedPosition} ")

        Log.d(LOG_TAG, "TILE TO MERGE: ${tile.item} INTO ${firstSelectedTile.item} ")

        if (firstSelectedTile.item < tile.item) {
            Log.d(LOG_TAG, "CASE 1: ")

            if (firstSelectedTile.selectedPosition < tile.selectedPosition) {
                Log.d(
                    LOG_TAG,
                    "FIRST SELECTED TILE POSITION : ${firstSelectedTile.selectedPosition} IS BEFORE TILE POSITION : ${tile.selectedPosition}"
                )
                newTilesOffset = Offset(
                    -(tile.positionOffset.x - firstSelectedTile.positionOffset.x),
                    -(tile.positionOffset.y - firstSelectedTile.positionOffset.y)
                )
            } else if (tile.selectedPosition < firstSelectedTile.selectedPosition) {
                Log.d(
                    LOG_TAG,
                    "FIRST SELECTED TILE POSITION: ${firstSelectedTile.selectedPosition} IS AFTER TILE POSITION : ${tile.selectedPosition}"
                )
                newTilesOffset = Offset(
                    (firstSelectedTile.positionOffset.x - tile.positionOffset.x),
                    -(tile.positionOffset.y + firstSelectedTile.positionOffset.y)
                )
            }
        } else {
            Log.d(LOG_TAG, "CASE 2: ")

            if (firstSelectedTile.selectedPosition < tile.selectedPosition) {
                Log.d(
                    LOG_TAG,
                    "FIRST SELECTED TILE POSITION : ${firstSelectedTile.selectedPosition} IS BEFORE TILE POSITION : ${tile.selectedPosition}"
                )
                newTilesOffset = Offset(
                    -(tile.positionOffset.x - firstSelectedTile.positionOffset.x),
                    -(tile.positionOffset.y - firstSelectedTile.positionOffset.y)
                )
            } else if (tile.selectedPosition < firstSelectedTile.selectedPosition) {
                Log.d(
                    LOG_TAG,
                    "FIRST SELECTED TILE POSITION : ${firstSelectedTile.selectedPosition} IS AFTER TILE POSITION : ${tile.selectedPosition}"
                )
                newTilesOffset = Offset(
                    (firstSelectedTile.positionOffset.x - tile.positionOffset.x),
                    -(firstSelectedTile.positionOffset.y - tile.positionOffset.y)
                )
            }
        }

        Log.d(
            LOG_TAG,
            "TILE ${tile.item} TO BE MOVED BY ${newTilesOffset} INTO TILE ${firstSelectedTile.item}"
        )

        Log.d(
            LOG_TAG,
            "**************************************************************************************"
        )

        return newTilesOffset
    }
}

