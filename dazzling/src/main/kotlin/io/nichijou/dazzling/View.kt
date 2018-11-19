package io.nichijou.dazzling

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Nullable
import androidx.core.view.TintableBackgroundView
import androidx.core.view.ViewCompat

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
    val pressed = color.brightenColor(if (darker) 0.9f else 1.1f)
    val activated = color.brightenColor(if (darker) 1.1f else 0.9f)
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
    this.setHintTextColor(if (isDark) color.brightenColor(1.2f) else color.brightenColor(.8f))
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