package com.bumble.appyx.transitionmodel.spotlight.interpolator

import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SpotlightFader<NavTarget : Any>(
    private val scope: CoroutineScope
) : Interpolator<NavTarget, SpotlightModel.State<NavTarget>> {

    class Props(
        var alpha: Alpha
    ) : Interpolatable<Props>, HasModifier {

        override suspend fun lerpTo(start: Props, end: Props, fraction: Float) {
            alpha.lerpTo(start.alpha, end.alpha, fraction)
        }

        override val modifier: Modifier
            get() = Modifier
                .then(alpha.modifier)
    }

    private val visible = Props(
        alpha = Alpha(1f)
    )

    private val hidden = Props(
        alpha = Alpha(0f)
    )

    private val interpolated: MutableMap<String, Props> = mutableMapOf()

    private fun <NavTarget : Any> SpotlightModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> =
        standard.map {
            MatchedProps(it, visible)
        } + (created + destroyed).map {
            MatchedProps(it, hidden)
        }

    override fun map(segment: TransitionModel.Segment<SpotlightModel.State<NavTarget>>): List<FrameModel<NavTarget>> {
        val (fromState, targetState) = segment.navTransition
        val fromProps = fromState.toProps()
        val targetProps = targetState.toProps()

        // TODO: use a map instead of find
        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!
            val elementProps = interpolated.getOrPut(t0.element.id) { t0.props }
            // TODO
            scope.launch {
                elementProps.lerpTo(t0.props, t1.props, segment.progress)
            }

            FrameModel(
                navElement = t1.element,
                modifier = elementProps.modifier,
                progress = segment.progress
            )
        }
    }
}
