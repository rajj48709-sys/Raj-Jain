package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import java.util.Random

@Composable
fun StylizedQrCode(
    payload: String,
    modifier: Modifier = Modifier,
    qrColor: Color = Color(0xFF1E3A8A) // Smart bank deep Navy Blue
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val minSize = minOf(width, height)
        val squareCount = 21 // Version 1 QR code spec is 21x21 modules
        val moduleSize = minSize / squareCount

        // Clean Canvas with elegant solid patterns
        val random = Random(payload.hashCode().toLong())

        // 1. Draw 3 Corner Finder Anchors (Top-Left, Top-Right, Bottom-Left)
        // Top Left [0, 0]
        val finderSize = moduleSize * 7
        fun drawFinder(x: Float, y: Float) {
            // Outer solid square
            drawRect(
                color = qrColor,
                topLeft = Offset(x, y),
                size = Size(finderSize, finderSize)
            )
            // Inner hollow white mask
            val hollowMargin = moduleSize
            val hollowSize = moduleSize * 5
            drawRect(
                color = Color.White,
                topLeft = Offset(x + hollowMargin, y + hollowMargin),
                size = Size(hollowSize, hollowSize)
            )
            // Center solid square
            val centerMargin = moduleSize * 2
            val centerSize = moduleSize * 3
            drawRect(
                color = qrColor,
                topLeft = Offset(x + centerMargin, y + centerMargin),
                size = Size(centerSize, centerSize)
            )
        }

        drawFinder(0f, 0f) // Top Left
        drawFinder(minSize - finderSize, 0f) // Top Right
        drawFinder(0f, minSize - finderSize) // Bottom Left

        // 2. Fill in random/deterministic matrix payload modules
        for (row in 0 until squareCount) {
            for (col in 0 until squareCount) {
                // Skip finder areas
                val isTopLeftFinder = row < 8 && col < 8
                val isTopRightFinder = row < 8 && col >= (squareCount - 8)
                val isBottomLeftFinder = row >= (squareCount - 8) && col < 8
                val isTimingPattern = row == 6 || col == 6

                if (isTopLeftFinder || isTopRightFinder || isBottomLeftFinder) {
                    continue
                }

                if (isTimingPattern) {
                    if ((row == 6 && col % 2 == 0) || (col == 6 && row % 2 == 0)) {
                        drawRect(
                            color = qrColor,
                            topLeft = Offset(col * moduleSize, row * moduleSize),
                            size = Size(moduleSize, moduleSize)
                        )
                    }
                    continue
                }

                // Deterministic module generation based on our seed
                if (random.nextBoolean()) {
                    drawRect(
                        color = qrColor,
                        topLeft = Offset(col * moduleSize, row * moduleSize),
                        size = Size(moduleSize * 0.9f, moduleSize * 0.9f) // Slight rounding spacing look
                    )
                }
            }
        }

        // Draw a central branding microcard placeholder for Raj Computer POS
        val centerCardSize = moduleSize * 4
        val centerOffset = (minSize - centerCardSize) / 2
        drawRect(
            color = Color.White,
            topLeft = Offset(centerOffset, centerOffset),
            size = Size(centerCardSize, centerCardSize)
        )
        drawRect(
            color = Color(0xFFEF4444), // Crimson safety accent
            topLeft = Offset(centerOffset + 2f, centerOffset + 2f),
            size = Size(centerCardSize - 4f, centerCardSize - 4f),
            style = Stroke(width = moduleSize * 0.3f)
        )
    }
}
