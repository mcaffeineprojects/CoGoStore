package com.philexliveprojects.cogostore.ui.compose.wavebottombar

import androidx.annotation.FloatRange
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.ceil

/**
 * Dynamic wave shaped on the top rect. with customizable [period], [amplitude], and [position].
 * @property position is a current position within its period value
 * */
class WaveShape(
    val period: Dp,
    val amplitude: Dp,
    @FloatRange(0.0, 1.0)
    val position: Float,
    val cornerRadius: DpSize
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ) = Outline.Generic(
        Path().apply {
            val wavyPath = Path().apply {
                val period = if (period == Dp.Unspecified) {
                    size.width
                } else {
                    with(density) { period.toPx() }
                }
                val halfPeriod = period / 2
                val amplitude = with(density) { amplitude.toPx() }

                val start = with(density) { -period * position.coerceIn(0f, 1f) }

                moveTo(start, amplitude)

                repeat(ceil(size.width / halfPeriod + 2).toInt()) { i ->
                    relativeQuadraticTo(
                        dx1 = halfPeriod / 2,
                        dy1 = amplitude * (if (i % 2 == 0) 1 else -1),
                        dx2 = halfPeriod,
                        dy2 = 0f,
                    )
                }

                lineTo(size.width, size.height)
                lineTo(0f, size.height)
            }

            val boundsPath = Path().apply {
                val cornerRadius = with(density) { this@WaveShape.cornerRadius.toSize() }
                addRoundRect(
                    RoundRect(
                        Rect(Offset.Zero, size),
                        CornerRadius(cornerRadius.width, cornerRadius.height)
                    )
                )
            }
            op(wavyPath, boundsPath, PathOperation.Intersect)
        }
    )
}