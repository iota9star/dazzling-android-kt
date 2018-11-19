package io.nichijou.dazzling

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import androidx.annotation.ColorInt

internal class CheckColorView(context: Context, attrs: AttributeSet?) : View(context, attrs), Checkable {

    interface OnCheckedChangeListener {
        fun onCheckedChanged(view: CheckColorView, color: Int)
    }

    private var onCheckedChangeListener: OnCheckedChangeListener? = null
    private var mChecked = false
    private val mRingPaint = Paint()
    private val mPaint = Paint()
    private var mRadius = 0f
    private var mRingRadius = 0f

    var selectedSpace: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    var borderWidth: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    @ColorInt
    var color: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    init {
        val ta = getContext().obtainStyledAttributes(attrs, R.styleable.CheckColorView)
        mChecked = ta.getBoolean(R.styleable.CheckColorView_checked, false)
        color = ta.getColor(R.styleable.CheckColorView_color, 0)
        val defaultBorderWidth = this.context.dp2px(4f).toFloat()
        borderWidth = ta.getDimension(R.styleable.CheckColorView_borderWidth, defaultBorderWidth)
        selectedSpace = ta.getDimension(R.styleable.CheckColorView_selectedSpace, defaultBorderWidth / 2f)
        ta.recycle()
        mRingPaint.style = Paint.Style.STROKE
        mRingPaint.isAntiAlias = true
        mRingPaint.strokeWidth = borderWidth
        mRingPaint.color = if (mChecked) color.adjustAlpha(.64f) else Color.TRANSPARENT
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
        mPaint.color = color
        mPaint.setShadowLayer(selectedSpace, 0f, 0f, Color.LTGRAY.adjustAlpha(.36f))
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        this.setWillNotDraw(false)
    }

    override fun isChecked(): Boolean = mChecked

    override fun toggle() {
        if (!mChecked) {
            mChecked = true
            invalidate()
            onCheckedChangeListener?.onCheckedChanged(this, color)
        }
    }

    override fun setChecked(checked: Boolean) {
        if (mChecked != checked) {
            mChecked = checked
            invalidate()
            onCheckedChangeListener?.onCheckedChanged(this, color)
        }
    }

    fun setColorAndCheckState(@ColorInt color: Int, checked: Boolean) {
        mChecked = checked
        this.color = color
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        onCheckedChangeListener = listener
    }

    override fun onDraw(canvas: Canvas) {
        if (mChecked) {
            mRadius = measuredWidth / 2f - borderWidth - selectedSpace
            mRingPaint.color = if (color.isColorLight()) color.brightenColor(.8f).adjustAlpha(.5f) else color.brightenColor(1.2f).adjustAlpha(.64f)
        } else {
            mRadius = measuredWidth / 2f - selectedSpace
            mRingPaint.color = Color.TRANSPARENT
        }
        mRingRadius = measuredWidth / 2f - borderWidth / 2
        mPaint.color = color
        mPaint.setShadowLayer(selectedSpace, 0f, 0f, Color.LTGRAY.adjustAlpha(.36f))
        canvas.drawCircle(
            measuredWidth / 2f,
            measuredWidth / 2f,
            mRadius,
            mPaint
        )
        canvas.drawCircle(
            measuredWidth / 2f,
            measuredHeight / 2f,
            mRingRadius,
            mRingPaint
        )
    }

    override fun getAccessibilityClassName(): CharSequence = CheckColorView::class.java.name
}