package com.digia.digiaui.framework.widgets.textfield

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.FocusedIndicatorThickness
import androidx.compose.material3.TextFieldDefaults.UnfocusedIndicatorThickness
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp

@Composable
fun TextFieldContainer(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource,
    modifier: Modifier = Modifier,
    colors: DigiaTextFieldColors? = null,
    shape: Shape = TextFieldDefaults.shape,
    focusedIndicatorLineThickness: Dp = FocusedIndicatorThickness,
    unfocusedIndicatorLineThickness: Dp = UnfocusedIndicatorThickness,
) {
    val focused = interactionSource.collectIsFocusedAsState().value
    val containerColor =
        animateColorAsState(
            targetValue = colors?.containerColor(enabled, isError, focused)?:Color.Green,
            animationSpec = tween(durationMillis = 150),
        )
    Box(
        modifier
            .textFieldBackground(containerColor::value, shape)
            .indicatorLine(
                enabled = enabled,
                isError = isError,
                interactionSource = interactionSource,
                colors = colors,
                focusedIndicatorLineThickness = focusedIndicatorLineThickness,
                unfocusedIndicatorLineThickness = unfocusedIndicatorLineThickness,
            )
    )
}



internal fun Modifier.textFieldBackground(
    color: ColorProducer,
    shape: Shape,
): Modifier =
    this.drawWithCache {
        val outline = shape.createOutline(size, layoutDirection, this)
        onDrawBehind { drawOutline(outline, color = color()) }
    }

fun Modifier.indicatorLine(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource,
    colors: DigiaTextFieldColors? = null,
    focusedIndicatorLineThickness: Dp = FocusedIndicatorThickness,
    unfocusedIndicatorLineThickness: Dp = UnfocusedIndicatorThickness
) =
    composed(
        inspectorInfo =
            debugInspectorInfo {
                name = "indicatorLine"
                properties["enabled"] = enabled
                properties["isError"] = isError
                properties["interactionSource"] = interactionSource
                properties["colors"] = colors
                properties["focusedIndicatorLineThickness"] = focusedIndicatorLineThickness
                properties["unfocusedIndicatorLineThickness"] = unfocusedIndicatorLineThickness
            }
    ) {
        val focused = interactionSource.collectIsFocusedAsState().value
        val stroke =
            animateBorderStrokeAsState(
                enabled,
                isError,
                focused,
                colors,
                focusedIndicatorLineThickness,
                unfocusedIndicatorLineThickness
            )
        Modifier.drawIndicatorLine(stroke)
    }


internal fun Modifier.drawIndicatorLine(indicatorBorder: State<BorderStroke>): Modifier {
    return drawWithContent {
        drawContent()
        val strokeWidth = indicatorBorder.value.width.toPx()
        val y = size.height - strokeWidth / 2
        drawLine(indicatorBorder.value.brush, Offset(0f, y), Offset(size.width, y), strokeWidth)
    }
}


@Composable
internal fun animateBorderStrokeAsState(
    enabled: Boolean,
    isError: Boolean,
    focused: Boolean,
    colors: DigiaTextFieldColors?,
    focusedBorderThickness: Dp,
    unfocusedBorderThickness: Dp
): State<BorderStroke> {
    val targetColor = colors?.indicatorColor(enabled, isError, focused)?:Color.Green
    val indicatorColor =
        if (enabled) {
            animateColorAsState(targetColor, tween(durationMillis = 150))
        } else {
            rememberUpdatedState(targetColor)
        }

    val thickness =
        if (enabled) {
            val targetThickness = if (focused) focusedBorderThickness else unfocusedBorderThickness
            animateDpAsState(targetThickness, tween(durationMillis = 150))
        } else {
            rememberUpdatedState(unfocusedBorderThickness)
        }

    return rememberUpdatedState(BorderStroke(thickness.value, indicatorColor.value))
}
