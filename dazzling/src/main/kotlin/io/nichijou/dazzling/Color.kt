package io.nichijou.dazzling

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.graphics.ColorUtils
import kotlin.random.Random

@ColorInt
fun Int.setAlpha(alpha: Int) = Color.argb(alpha, Color.red(this), Color.green(this), Color.blue(this))

@ColorInt
fun Int.setRed(red: Int) = Color.argb(Color.alpha(this), red, Color.green(this), Color.blue(this))

@ColorInt
fun Int.setGreen(green: Int) = Color.argb(Color.alpha(this), Color.red(this), green, Color.blue(this))

@ColorInt
fun Int.setBlue(blue: Int) = Color.argb(Color.alpha(this), Color.red(this), Color.green(this), blue)

@ColorInt
fun Int.stripAlpha() = Color.rgb(Color.red(this), Color.green(this), Color.blue(this))

@ColorInt
fun Int.adjustAlpha(factor: Float) = Color.argb(Math.round(Color.alpha(this) * factor), Color.red(this), Color.green(this), Color.blue(this))

@ColorInt
fun Int.saturationColor(@FloatRange(from = 0.0, to = 2.0) by: Float): Int {
    if (by == 1f) return this
    val hsv = FloatArray(3)
    Color.colorToHSV(this, hsv)
    hsv[1] = hsv[1] * by
    return Color.HSVToColor(hsv)
}

@ColorInt
fun Int.brightenColor(@FloatRange(from = 0.0, to = 2.0) by: Float): Int {
    if (by == 1f) return this
    val hsv = FloatArray(3)
    Color.colorToHSV(this, hsv)
    hsv[2] *= by
    return Color.HSVToColor(hsv)
}

@ColorInt
fun Int.blendWith(@ColorInt color: Int, ratio: Float): Int {
    val inverseRatio = 1f - ratio
    val a = Color.alpha(this) * inverseRatio + Color.alpha(color) * ratio
    val r = Color.red(this) * inverseRatio + Color.red(color) * ratio
    val g = Color.green(this) * inverseRatio + Color.green(color) * ratio
    val b = Color.blue(this) * inverseRatio + Color.blue(color) * ratio
    return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
}

@ColorInt
fun Int.titleColor(): Int {
    val minContrast = 3.0f
    val alpha: Int
    if (this.isColorLight()) {
        alpha = ColorUtils.calculateMinimumAlpha(Color.WHITE, this, minContrast)
        if (alpha == -1) return Color.BLACK
    } else {
        alpha = ColorUtils.calculateMinimumAlpha(Color.BLACK, this, minContrast)
        if (alpha == -1) return Color.WHITE
    }
    return ColorUtils.setAlphaComponent(Color.BLACK, alpha)
}

@ColorInt
fun Int.bodyColor(): Int {
    val minContrast = 4.5f
    val alpha: Int
    when {
        this.isColorLight() -> {
            alpha = ColorUtils.calculateMinimumAlpha(Color.WHITE, this, minContrast)
            if (alpha == -1) return Color.BLACK.brightenColor(1.2f)
        }
        else -> {
            alpha = ColorUtils.calculateMinimumAlpha(Color.BLACK, this, minContrast)
            if (alpha == -1) return Color.WHITE.brightenColor(0.84f)
        }
    }
    return ColorUtils.setAlphaComponent(Color.BLACK, alpha)
}

fun Int.isColorLight() = 1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255 < 0.5

fun Int.isColorDark() = !this.isColorLight()

fun Int.isColorLight(@ColorInt bgColor: Int) = if (Color.alpha(this) < 128) bgColor.isColorLight() else this.isColorLight()

fun Int.stepColor(@FloatRange(from = 0.01, to = 2.0) factor: Float = 0.2f): MutableList<Int> {
    val colors = mutableListOf<Int>()
    val alpha = this.colorAlpha()
    for (i in 0..200 step (factor * 100).toInt()) {
        val brightenColor = this.brightenColor(i / 100f)
        if (this == brightenColor) {
            colors.add(this)
        } else {
            colors.add(brightenColor.adjustAlpha(alpha / 255f))
        }
    }
    return colors
}

fun Int.colorAlpha() = Color.alpha(this)

fun Int.toHexColor() = Integer.toHexString(this).toUpperCase()

fun randomColor() = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))

fun randomColors(size: Int): MutableList<Int> {
    val colors = mutableListOf<Int>()
    for (i in 0 until size) {
        colors.add(randomColor())
    }
    return colors
}

internal fun IntRange.random() = Random.nextInt(start, endInclusive + 1)

fun randomColors(range: IntRange): IntArray {
    val size = range.random()
    val intArray = IntArray(size)
    for (i in 0 until size) {
        intArray[i] = randomColor()
    }
    return intArray
}
