package io.nichijou.dazzling.internal

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import androidx.annotation.ColorInt
import io.nichijou.color.adjustAlpha
import io.nichijou.color.isColorLight
import io.nichijou.color.lighten
import io.nichijou.dazzling.R

internal class ColorView(context: Context, attrs: AttributeSet?) : View(context, attrs), Checkable {

    internal interface OnCheckedChangeListener {
        fun onCheckedChanged(view: ColorView, color: Int)
    }

    private var mOnCheckedChangeListener: OnCheckedChangeListener? = null
    private var mChecked = false
    private val mRingPaint = Paint()
    private val mPaint = Paint()
    private var mRadius = 0f
    private var mRingRadius = 0f
    private val mSelectedSpace: Float
    private val mBorderWidth: Float
    private var mColor: Int
    private var mTransBackground: Drawable? = null

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ColorView)
        mChecked = ta.getBoolean(R.styleable.ColorView_checked, false)
        mColor = ta.getColor(R.styleable.ColorView_color, 0)
        val defaultBorderWidth = this.context.dp2px(4f).toFloat()
        mBorderWidth = ta.getDimension(R.styleable.ColorView_borderWidth, defaultBorderWidth)
        mSelectedSpace = ta.getDimension(R.styleable.ColorView_selectedSpace, defaultBorderWidth / 2f)
        ta.recycle()
        mRingPaint.style = Paint.Style.STROKE
        mRingPaint.isAntiAlias = true
        mRingPaint.strokeWidth = mBorderWidth
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
        mPaint.color = mColor
        mPaint.setShadowLayer(mSelectedSpace, 0f, 0f, Color.LTGRAY.adjustAlpha(.36f))
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        this.setWillNotDraw(false)
    }

    override fun isChecked(): Boolean = mChecked

    override fun toggle() {
        if (!mChecked) {
            mChecked = true
            invalidate()
            mOnCheckedChangeListener?.onCheckedChanged(this, mColor)
        }
    }

    override fun setChecked(checked: Boolean) {
        if (mChecked != checked) {
            mChecked = checked
            mOnCheckedChangeListener?.onCheckedChanged(this, mColor)
            invalidate()
        }
    }

    fun setColor(color: Int) {
        mColor = color
        invalidate()
    }

    fun getColor() = mColor

    fun setColorAndCheckState(@ColorInt color: Int, checked: Boolean) {
        mChecked = checked
        this.mColor = color
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        mOnCheckedChangeListener = listener
    }

    override fun onDraw(canvas: Canvas) {
        if (mChecked) {
            mRadius = measuredWidth / 2f - mBorderWidth - mSelectedSpace
            mRingPaint.color = if (mColor.isColorLight()) mColor.lighten(.9f).adjustAlpha(.64f) else mColor.lighten(1.1f).adjustAlpha(.64f)
        } else {
            mRadius = measuredWidth / 2f - mSelectedSpace
            mRingPaint.color = Color.TRANSPARENT
        }
        mRingRadius = measuredWidth / 2f - mBorderWidth / 2
        mPaint.color = mColor
        mPaint.setShadowLayer(mSelectedSpace, 0f, 0f, Color.DKGRAY.adjustAlpha(.08f))
        if (Color.alpha(mColor) < 255) {
            if (mTransBackground == null) {
                val lt: Int
                val rb: Int
                if (mChecked) {
                    lt = (mBorderWidth + mSelectedSpace * 2).toInt()
                    rb = (measuredWidth - mSelectedSpace * 4).toInt()
                } else {
                    lt = mBorderWidth.toInt()
                    rb = (measuredWidth - mSelectedSpace * 2).toInt()
                }
                mTransBackground = context.drawableRes(R.drawable.bg_transparent)
                mTransBackground?.setBounds(lt, lt, rb, rb)
            }
            mTransBackground?.draw(canvas)
        }
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

    override fun onDetachedFromWindow() {
        mTransBackground = null
        mOnCheckedChangeListener = null
        super.onDetachedFromWindow()
    }

    override fun getAccessibilityClassName(): CharSequence = ColorView::class.java.name
}