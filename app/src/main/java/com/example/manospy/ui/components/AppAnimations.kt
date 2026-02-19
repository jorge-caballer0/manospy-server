package com.example.manospy.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Colección de animaciones predefinidas para la app.
 */
object AppAnimations {
    // Duración estándar de animaciones (ms)
    const val DURATION_FAST = 200
    const val DURATION_NORMAL = 300
    const val DURATION_SLOW = 500
    const val DURATION_VERY_SLOW = 800
}

/**
 * AnimatedVisibility con entrada de fade suave.
 */
@Composable
fun FadeInAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    duration: Int = AppAnimations.DURATION_NORMAL,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(duration, easing = LinearEasing)),
        exit = fadeOut(animationSpec = tween(duration / 2, easing = LinearEasing)),
        modifier = modifier
    ) {
        content()
    }
}

/**
 * AnimatedVisibility con entrada de slide horizontal suave.
 */
@Composable
fun SlideInHorizontalAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    duration: Int = AppAnimations.DURATION_NORMAL,
    fromStartEdge: Boolean = true,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            animationSpec = tween(duration, easing = LinearEasing),
            initialOffsetX = { if (fromStartEdge) -it else it }
        ) + fadeIn(animationSpec = tween(duration, easing = LinearEasing)),
        exit = slideOutHorizontally(
            animationSpec = tween(duration / 2, easing = LinearEasing),
            targetOffsetX = { if (fromStartEdge) -it else it }
        ) + fadeOut(animationSpec = tween(duration / 2, easing = LinearEasing)),
        modifier = modifier
    ) {
        content()
    }
}

/**
 * AnimatedVisibility con entrada de slide vertical suave.
 */
@Composable
fun SlideInVerticalAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    duration: Int = AppAnimations.DURATION_NORMAL,
    fromTopEdge: Boolean = true,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(duration, easing = LinearEasing),
            initialOffsetY = { if (fromTopEdge) -it else it }
        ) + fadeIn(animationSpec = tween(duration, easing = LinearEasing)),
        exit = slideOutVertically(
            animationSpec = tween(duration / 2, easing = LinearEasing),
            targetOffsetY = { if (fromTopEdge) -it else it }
        ) + fadeOut(animationSpec = tween(duration / 2, easing = LinearEasing)),
        modifier = modifier
    ) {
        content()
    }
}
