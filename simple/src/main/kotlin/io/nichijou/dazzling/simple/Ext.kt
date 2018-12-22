package io.nichijou.dazzling.simple

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.view.View
import android.widget.Button
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import io.nichijou.color.isColorDark
import io.nichijou.color.lighten

internal fun Context.colorRes(@ColorRes resId: Int) = ContextCompat.getColor(this, resId)

internal fun View.setBackgroundCompat(@Nullable drawable: Drawable?) {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
    this.background = drawable
  } else {
    this.setBackgroundDrawable(drawable)
  }
}

@ColorInt
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

internal fun AppCompatActivity.setStatusBarColorCompat(@ColorInt color: Int) {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    this.window.statusBarColor = color
  }
}

internal fun AppCompatActivity.setLightStatusBarCompat(isLight: Boolean) {
  val view = this.window.decorView
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    val flags = view.systemUiVisibility
    view.systemUiVisibility = if (isLight) {
      flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } else {
      flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }
  }
}

internal fun Drawable.tint(sl: ColorStateList?): Drawable {
  if (sl == null) return this
  var d: Drawable = this
  d = DrawableCompat.wrap(d.mutate())
  DrawableCompat.setTintList(d, sl)
  return d
}
