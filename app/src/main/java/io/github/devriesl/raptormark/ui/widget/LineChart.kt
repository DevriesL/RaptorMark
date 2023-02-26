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
import androidx.compose.ui.unit.dp
import java.lang.Float.min

@Composable
fun LineChart(
    linesData: List<Pair<Color, List<Pair<Int, Int>>>>,
    maxYValue: Int,
    modifier: Modifier = Modifier
) {
    val lineBound = remember { mutableStateOf(Float.MAX_VALUE) }

    Canvas(
        modifier = modifier
            .drawBehind {
                drawYAxisWithLabels(maxYValue)
            }
            .padding(horizontal = 16.dp)
    ) {
        linesData.forEach { (color, lineData) ->
            lineBound.value = min(size.width.div(lineData.count().times(1.2F)), lineBound.value)
            val scaleFactor = size.height.div(maxYValue)
            val radius = size.width.div(60)
            val strokeWidth = 2.dp.toPx()
            val path = Path().apply {
                moveTo(0f, size.height)
            }

            lineData.forEachIndexed { index, data ->
                val centerOffset =
                    dataToOffSet(index, lineBound.value, size, data.second, scaleFactor)
                if (index == 0 || index == lineData.lastIndex) {
                    drawXLabel(
                        data.first,
                        centerOffset,
                        radius
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
                val pathEffect = PathEffect.cornerPathEffect(strokeWidth)
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = strokeWidth, pathEffect = pathEffect),
                )
            }
        }
    }
}

internal fun DrawScope.drawYAxisWithLabels(
    maxValue: Int
) {
    val graphYAxisEndPoint = size.height.div(4)
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(40f, 20f), 0f)
    val labelScaleFactor = maxValue.div(4)

    repeat(5) { index ->
        val yAxisEndPoint = graphYAxisEndPoint.times(index)
        val yLabelText = "${(labelScaleFactor.times(4.minus(index)) / 10000)} GB/s"

        drawIntoCanvas {
            it.nativeCanvas.apply {
                drawText(
                    yLabelText,
                    0F.minus(25),
                    yAxisEndPoint.minus(10),
                    Paint().apply {
                        textSize = size.width.div(30)
                        textAlign = Paint.Align.CENTER
                    }
                )
            }
        }
        if (index != 0) {
            drawLine(
                start = Offset(x = 0f, y = yAxisEndPoint),
                end = Offset(x = size.width, y = yAxisEndPoint),
                color = Color(0xFF969696),
                pathEffect = pathEffect,
                alpha = 0.1F,
                strokeWidth = size.width.div(200)
            )
        }
    }
}

internal fun DrawScope.drawXLabel(
    data: Int,
    centerOffset: Offset,
    radius: Float
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
                size.height.plus(radius.times(4)),
                Paint().apply {
                    textSize = size.width.div(30)
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
