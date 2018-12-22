package io.nichijou.dazzling.internal

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.TintableBackgroundView
import androidx.core.view.ViewCompat
import io.nichijou.color.isColorDark
import io.nichijou.color.lighten
import io.nichijou.dazzling.R

internal fun Context.isTablet() = (this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
internal fun Context.isLandscape() = this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
internal fun Context.dp2px(dp: Float): Int = (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.resources.displayMetrics) + 0.5f).toInt()
internal fun Context.sp2px(sp: Float): Int = (sp * this.resources.displayMetrics.scaledDensity + 0.5f).toInt()
internal fun Context.colorRes(@ColorRes resId: Int) = ContextCompat.getColor(this, resId)
internal fun Context.drawableRes(@DrawableRes resId: Int) = ContextCompat.getDrawable(this, resId)


internal fun View.setBackgroundCompat(@Nullable drawable: Drawable?) {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
    this.background = drawable
  } else {
    this.setBackgroundDrawable(drawable)
  }
}

private fun defaultRippleColor(context: Context, useDarkRipple: Boolean): Int {
  return context.colorRes(if (useDarkRipple) com.google.android.material.R.color.ripple_material_light else com.google.android.material.R.color.ripple_material_dark
  )
}

internal fun Button.tint(@ColorInt color: Int, isDark: Boolean) {
  val darker = color.isColorDark()
  val disabled = context.colorRes(if (isDark) R.color.md_button_disabled_dark else R.color.md_button_disabled_light)
  val pressed = color.lighten(if (darker) 0.9f else 1.1f)
  val activated = color.lighten(if (darker) 1.1f else 0.9f)
  val rippleColor = defaultRippleColor(context, darker)
  val textColor = if (darker) Color.WHITE else Color.BLACK
  val sl = ColorStateList(
    arrayOf(
      intArrayOf(-android.R.attr.state_enabled),
      intArrayOf(android.R.attr.state_enabled),
      intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed),
      intArrayOf(android.R.attr.state_enabled, android.R.attr.state_activated),
      intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)
    ), intArrayOf(disabled, color, pressed, activated, activated))
  this.setTextColor(textColor)
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && this.background is RippleDrawable) {
    val rd = this.background as RippleDrawable
    rd.setColor(ColorStateList.valueOf(rippleColor))
  }
  this.setBackgroundCompat(this.background?.tint(sl))
}

internal fun EditText.tint(color: Int, isDark: Boolean) {
  this.setTextColor(color)
  this.setHintTextColor(if (isDark) color.lighten(1.2f) else color.lighten(.8f))
  val editTextColorStateList = ColorStateList(
    arrayOf(
      intArrayOf(-android.R.attr.state_enabled),
      intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_pressed, -android.R.attr.state_focused),
      intArrayOf()),
    intArrayOf(
      context.colorRes(if (isDark) R.color.md_text_disabled_dark else R.color.md_text_disabled_light),
      context.colorRes(if (isDark) R.color.md_control_normal_dark else R.color.md_control_normal_light),
      color)
  )
  if (this is TintableBackgroundView) {
    ViewCompat.setBackgroundTintList(this, editTextColorStateList)
  } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    this.backgroundTintList = editTextColorStateList
  }
  this.tintCursor(color)
}

private fun EditText.tintCursor(@ColorInt color: Int) {
  try {
    val fCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
    fCursorDrawableRes.isAccessible = true
    val mCursorDrawableRes = fCursorDrawableRes.getInt(this)
    fCursorDrawableRes.isAccessible = false
    val fEditor = TextView::class.java.getDeclaredField("mEditor")
    fEditor.isAccessible = true
    val editor = fEditor.get(this)
    fEditor.isAccessible = false
    val clazz = editor.javaClass
    val fCursorDrawable = clazz.getDeclaredField("mCursorDrawable")
    fCursorDrawable.isAccessible = true
    val drawables = arrayOfNulls<Drawable>(2)
    val tintDrawable = context.drawableRes(mCursorDrawableRes)?.tint(color)
    drawables[0] = tintDrawable
    drawables[1] = tintDrawable
    fCursorDrawable.set(editor, drawables)
    fCursorDrawable.isAccessible = false
  } catch (_: Exception) {
  }
}

internal fun Drawable.tint(@ColorInt color: Int): Drawable {
  var d: Drawable = this
  d = DrawableCompat.wrap(d.mutate())
  DrawableCompat.setTintMode(d, PorterDuff.Mode.SRC_IN)
  DrawableCompat.setTint(d, color)
  return d
}

internal fun Drawable.tint(sl: ColorStateList?): Drawable {
  if (sl == null) return this
  var d: Drawable = this
  d = DrawableCompat.wrap(d.mutate())
  DrawableCompat.setTintList(d, sl)
  return d
}
