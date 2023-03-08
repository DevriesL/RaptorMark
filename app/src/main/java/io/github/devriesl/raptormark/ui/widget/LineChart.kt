package io.github.devriesl.raptormark.ui.widget

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import java.lang.Float.min

@Composable
fun LineChart(
    linesData: List<Pair<Color, List<Pair<Int, Int>>>>,
    maxYValue: Int,
    modifier: Modifier = Modifier,
    yLabelTextSize: TextUnit = 12.sp,
    xLabelTextSize: TextUnit = 12.sp,
    strokeWidth: Dp = 2.dp,
    xLabelPaddingTop: Dp = 12.dp
) {
    val lineBound = remember { mutableStateOf(Float.MAX_VALUE) }
    val xLabelSpaceHeightPx = with(LocalDensity.current) {
        xLabelTextSize.toPx() + xLabelPaddingTop.toPx()
    }
    Canvas(
        modifier = modifier
            .drawBehind {
                drawYAxisWithLabels(
                    maxValue = maxYValue,
                    labelTextSize = yLabelTextSize,
                    density = this@drawBehind,
                    strokeWidth = strokeWidth,
                    bottomPadding = xLabelSpaceHeightPx.toDp()
                )
            }
            .padding(horizontal = 16.dp)

    ) {
        linesData.forEach { (color, lineData) ->
            // cut some height for X label place
            val lineSize = Size(size.width, size.height - xLabelSpaceHeightPx)
            lineBound.value = min(lineSize.width.div(lineData.count().times(1.2F)), lineBound.value)
            val scaleFactor = lineSize.height.div(maxYValue)
            val strokeWidthPx = 2.dp.toPx()
            val path = Path().apply {
                moveTo(0f, lineSize.height)
            }

            lineData.forEachIndexed { index, data ->
                val centerOffset =
                    dataToOffSet(index, lineBound.value, lineSize, data.second, scaleFactor)
                if (index == 0 || index == lineData.lastIndex) {
                    drawXLabel(
                        data = data.first,
                        centerOffset = centerOffset + Offset(0f,  xLabelSpaceHeightPx),
                        labelTextSize = xLabelTextSize,
                        density = this@Canvas
                    )
                }
                if (lineData.size > 1) {
                    when (index) {
                        0 -> path.moveTo(centerOffset.x, centerOffset.y)
                        else -> path.lineTo(centerOffset.x, centerOffset.y)
                    }
                }
            }
            if (lineData.size > 1) {
                val pathEffect = PathEffect.cornerPathEffect(strokeWidthPx)
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = strokeWidthPx, pathEffect = pathEffect),
                )
            }
        }
    }
}

internal fun DrawScope.drawYAxisWithLabels(
    maxValue: Int,
    labelTextSize: TextUnit,
    density: Density,
    strokeWidth: Dp = 2.dp,
    // x label text space
    bottomPadding: Dp = 0.dp
) {
    val graphYAxisEndPoint = (size.height - with(density) { bottomPadding.toPx() }).div(4)
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(40f, 20f), 0f)
    val labelScaleFactor = maxValue.div(4)
    val textPxSize = with(density) { labelTextSize.toPx() }
    val strokeWidthPx = with(density) { strokeWidth.toPx() }
    repeat(5) { index ->
        val yAxisEndPoint = graphYAxisEndPoint.times(index)
        val yLabelText = "${(labelScaleFactor.times(4.minus(index)) / 10000)} GB/s"

        drawIntoCanvas {
            it.nativeCanvas.apply {
                val textPaint = Paint().apply {
                    textSize = textPxSize
                    textAlign = Paint.Align.CENTER
                }
                val rect = android.graphics.Rect().apply {
                    textPaint.getTextBounds(yLabelText, 0, yLabelText.length, this)
                }
                drawText(
                    yLabelText,
                    0f,
                     yAxisEndPoint - strokeWidthPx / 2 + rect.height() / 2 - rect.bottom,
                    textPaint
                )
            }
        }
        drawLine(
            start = Offset(x = 0f, y = yAxisEndPoint - strokeWidthPx),
            end = Offset(x = size.width, y = yAxisEndPoint - strokeWidthPx),
            color = Color(0xFF969696),
            pathEffect = pathEffect,
            alpha = 0.1F,
            strokeWidth = strokeWidthPx
        )

    }
}

internal fun DrawScope.drawXLabel(
    data: Int,
    centerOffset: Offset,
    labelTextSize: TextUnit,
    density: Density
) {
    val xLabelText = when {
        data >= 1024 * 1024 -> "${data / 1024 / 1024} MB"
        data >= 1024 -> "${data / 1024} KB"
        else -> "$data B"
    }
    drawIntoCanvas {
        it.nativeCanvas.apply {
            drawText(
                xLabelText,
                centerOffset.x,
                size.height,
                Paint().apply {
                    textSize = with(density) { labelTextSize.toPx() }
                    textAlign = Paint.Align.CENTER
                }
            )
        }
    }
}

internal fun dataToOffSet(
    index: Int,
    bound: Float,
    size: Size,
    data: Int,
    yScaleFactor: Float
): Offset {
    val boundFactor = 1.2F
    val startX = index.times(bound.times(boundFactor))
    val endX = index.plus(1).times(bound.times(boundFactor))
    val y = size.height.minus(data.times(yScaleFactor))
    return Offset(((startX.plus(endX)).div(2F)), y)
}
