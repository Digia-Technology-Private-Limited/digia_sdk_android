package com.digia.digiaui.framework.widgets

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.digia.digiaui.framework.RenderPayload
import com.digia.digiaui.framework.VirtualWidgetRegistry
import com.digia.digiaui.framework.base.VirtualLeafNode
import com.digia.digiaui.framework.base.VirtualNode
import com.digia.digiaui.framework.evalColor
import com.digia.digiaui.framework.models.CommonProps
import com.digia.digiaui.framework.models.Props
import com.digia.digiaui.framework.models.VWNodeData


class VWLinearProgressBar(
    props: Props,
    commonProps: CommonProps?,
    parentProps: Props?,
    parent: VirtualNode?,
    refName: String? = null
) : VirtualLeafNode<Props>(
    props = props,
    commonProps = commonProps,
    parentProps = parentProps,
    parent = parent,
    refName = refName
) {

    @Composable
    override fun Render(payload: RenderPayload) {
        val progressValue = payload.eval<Double>(props.get("progressValue"))
        val isReverse = payload.eval<Boolean>(props.get("isReverse")) ?: false
        val type = props.getString("type") ?: "indeterminate"

        val rotationModifier = if (isReverse) Modifier.rotate(180f) else Modifier

        Box(modifier = rotationModifier) {
            ProgressContent(
                progressValue = progressValue,
                type = type,
                payload = payload
            )
        }
    }

    @Composable
    private fun ProgressContent(
        progressValue: Double?,
        type: String,
        payload: RenderPayload
    ) {
        val width = props.getDouble("width")?.dp
        val defaultThickness = if (type == "indeterminate") 4.0 else 5.0
        val height = (props.getDouble("thickness") ?: defaultThickness).dp
        val radius = (props.getDouble("borderRadius") ?: 0.0).dp

        val indicatorColor = payload.evalColor(props.get("indicatorColor")) ?: Color.Blue
        val bgColor = payload.evalColor(props.get("bgColor")) ?: Color.Transparent

        val animationEnabled = props.getBool("animation") ?: false

        val modifier = Modifier
            .then(if (width != null) Modifier.width(width) else Modifier.fillMaxWidth())
            .height(height)
            .clip(RoundedCornerShape(radius))

        if (type == "indeterminate") {
            LinearProgressIndicator(
                modifier = modifier,
                color = indicatorColor,
                trackColor = bgColor,
                strokeCap = StrokeCap.Butt,
                gapSize = 0.dp
            )
        } else {
            val targetProgress = ((progressValue ?: 0.0) / 100f).toFloat().coerceIn(0f, 1f)

            val animatedProgress by animateFloatAsState(
                targetValue = targetProgress,
                animationSpec = if (animationEnabled) {
                    tween(durationMillis = 500)
                } else {
                    snap()
                },
                label = "LinearProgress"
            )

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = modifier,
                color = indicatorColor,
                trackColor = bgColor,
                gapSize = 0.dp,
                strokeCap = StrokeCap.Butt,
                drawStopIndicator = { false }
            )
        }
    }
}

/** Builder function for LinearProgressBar widget */
fun linearProgressBarBuilder(data: VWNodeData, parent: VirtualNode?, registry: VirtualWidgetRegistry): VirtualNode {
    return VWLinearProgressBar(
        refName = data.refName,
        commonProps = data.commonProps,
        parent = parent,
        parentProps = data.parentProps,
        props = data.props,
    )
}
