package com.bumble.appyx.interactions.core.ui.property.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.zIndex
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpFloat
import com.bumble.appyx.interactions.core.ui.property.Interpolatable

class ZIndex(
    value: Float,
    easing: Easing? = null
) : AnimatedProperty<Float, AnimationVector1D>(
    animatable = Animatable(value, Float.VectorConverter),
    easing = easing
), Interpolatable<ZIndex> {

    override val modifier: Modifier
        get() = Modifier.composed {
            val value by animatable.asState()
            this.zIndex(value)
        }

    override suspend fun lerpTo(start: ZIndex, end: ZIndex, fraction: Float) {
        snapTo(lerpFloat(
            start = start.value,
            end = end.value,
            progress = easingTransform(end.easing, fraction)
        ))
    }
}
