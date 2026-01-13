package com.digia.digiaui.framework.widgets


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.digia.digiaui.framework.RenderPayload
import com.digia.digiaui.framework.VirtualWidgetRegistry
import com.digia.digiaui.framework.base.VirtualLeafNode
import com.digia.digiaui.framework.base.VirtualNode
import com.digia.digiaui.framework.evalColor
import com.digia.digiaui.framework.models.CommonProps
import com.digia.digiaui.framework.models.ExprOr
import com.digia.digiaui.framework.models.Props
import com.digia.digiaui.framework.models.VWNodeData
import com.digia.digiaui.framework.utils.JsonLike

/**
 * Virtual CircularProgressBar widget
 */
class VWCircularProgressBar(
    props: Props,
    commonProps: CommonProps?,
    parentProps: Props?,
    parent: VirtualNode?,
    refName: String?
) : VirtualLeafNode<Props>(
    props = props,
    commonProps = commonProps,
    parentProps = parentProps,
    parent = parent,
    refName = refName
) {

    @Composable
    override fun Render(payload: RenderPayload) {

        val progressValue =
            payload.eval<Double>(props.get("progressValue"))

        val type =
            props.getString("type") ?: "indeterminate"

        val size =
            props.getDouble("size")?.dp ?: 40.dp

        val thickness =
            props.getDouble("thickness")?.dp ?: 4.dp

        val indicatorColor =
            payload.evalColor(props.get("indicatorColor")) ?: Color.Blue

        val bgColor =
            payload.evalColor(props.get("bgColor")) ?: Color.Transparent

        val animate = props.getBool("animation") ?: false

        when (type) {
            "indeterminate" -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(size),
                    color = indicatorColor,
                    strokeWidth = thickness,
                    trackColor = bgColor
                )
            }

            else -> {
                // Determine target progress
                val targetProgress =
                    ((progressValue ?: 0.0) / 100.0)
                        .coerceIn(0.0, 1.0)
                        .toFloat()

                // Animate progress only if animation = true
                val progress = if (animate) {
                    animateFloatAsState(
                        targetValue = targetProgress,
                        label = "circularProgress"
                    ).value
                } else {
                    targetProgress
                }

                CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(size),
                color = indicatorColor,
                strokeWidth = thickness,
                trackColor = bgColor,
                strokeCap = StrokeCap.Round,
                )
            }
        }
    }
}


fun circularProgressBarBuilder(
    data: VWNodeData,
    parent: VirtualNode?,
    registry: VirtualWidgetRegistry
): VirtualNode {
    return VWCircularProgressBar(
        refName = data.refName,
        commonProps = data.commonProps,
        parent = parent,
        parentProps = data.parentProps ?: Props.empty(),
        props = data.props,
    )
}