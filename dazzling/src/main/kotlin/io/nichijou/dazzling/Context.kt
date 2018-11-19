package io.nichijou.dazzling

import android.content.Context
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

internal fun Context.dp2px(dp: Float): Int = (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.resources.displayMetrics) + 0.5f).toInt()
internal fun Context.colorRes(@ColorRes resId: Int) = ContextCompat.getColor(this, resId)
internal fun Context.drawableRes(@DrawableRes resId: Int) = ContextCompat.getDrawable(this, resId)