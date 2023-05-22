package com.paperscan.usecase.models

import androidx.compose.ui.geometry.Offset

/***
 * Tile data class
 */
data class Tile(
    var item: Int,
    var selectedPosition: Int,
    var positionOffset: Offset =  Offset(Float.NaN, Float.NaN)
)
