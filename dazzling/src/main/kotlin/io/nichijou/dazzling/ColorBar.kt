package io.nichijou.dazzling

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator

internal class ColorBar(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    interface OnThumbInDraggingListener {
        fun onChange(view: ColorBar, value: Int)
    }

    private var mOnThumbDraggingListener: OnThumbInDraggingListener? = null
    private val COLOR_DELTA = 255
    private val SANS = 1
    private val SERIF = 2
    private val MONOSPACE = 3
    private val mPaint = Paint()
    private val mColor: Int
    private val mTextSize: Float
    private val mTextColor: Int
    private val mTypeface: Typeface
    private val mThumbColor: Int
    private val mThumbRadius: Float
    private val mTrackColor: Int
    private val mTrackSize: Float
    private var mValue = 0
    private var mStartX = 0f
    private var mStopX = 0f
    private var mCenterY = 0f
    private var mValueCenterX = 0f
    private var mContentWidth = 0f
    private var mTouchOffset = 0f
    private var mOnValueChanged = false
    private var mIsInit = true

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ColorBar)
        mTrackSize = ta.getDimension(R.styleable.ColorBar_trackSize, this.context.dp2px(18f).toFloat())
        mTrackColor = ta.getColor(R.styleable.ColorBar_trackColor, Color.WHITE.brightenColor(.9f))
        mTextSize = ta.getDimension(R.styleable.ColorBar_android_textSize, this.context.sp2px(12f).toFloat())
        mTextColor = ta.getColor(R.styleable.ColorBar_android_textColor, Color.GRAY)
        mTypeface = getRawTypeface(ta.getInt(R.styleable.ColorBar_android_typeface, MONOSPACE))
        mThumbRadius = ta.getDimension(R.styleable.ColorBar_thumbRadius, this.context.dp2px(18f).toFloat())
        mThumbColor = ta.getColor(R.styleable.ColorBar_thumbColor, Color.WHITE)
        mColor = ta.getColor(R.styleable.ColorBar_dominant, Color.TRANSPARENT)
        mValue = ta.getInt(R.styleable.ColorBar_value, 0)
        if (mValue > 255) mValue = 255
        if (mValue < 0) mValue = 0
        ta.recycle()
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        setWillNotDraw(false)
    }

    fun setOnValueChangeListener(listener: OnThumbInDraggingListener) {
        mOnThumbDraggingListener = listener
    }

    private var mValueChangeAnimator: ValueAnimator? = null

    fun setValueWithAnimate(value: Int) {
        if (mValueChangeAnimator != null && mValueChangeAnimator!!.isRunning) {
            mValueChangeAnimator!!.cancel()
            mValueChangeAnimator = null
        }
        mValueChangeAnimator = ValueAnimator.ofInt(0, value).apply {
            duration = 1024
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                mValue = it.animatedValue as Int
                invalidate()
            }
            start()
        }
    }

    override fun onDetachedFromWindow() {
        if (mValueChangeAnimator != null && mValueChangeAnimator!!.isRunning) {
            mValueChangeAnimator!!.cancel()
            mValueChangeAnimator = null
        }
        super.onDetachedFromWindow()
    }

    fun setValue(value: Int) {
        mValue = value
        invalidate()
    }

    fun getValue() = mValue

    private fun getRawTypeface(value: Int): Typeface {
        return when (value) {
            SANS -> Typeface.SANS_SERIF
            SERIF -> Typeface.SERIF
            MONOSPACE -> Typeface.MONOSPACE
            else -> Typeface.DEFAULT
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mStartX = paddingLeft + mThumbRadius
        mStopX = measuredWidth - paddingRight - mThumbRadius
        mCenterY = measuredHeight / 2f
        mContentWidth = measuredWidth - paddingLeft - paddingRight - mThumbRadius * 2
    }

    override fun onDraw(canvas: Canvas) {
        if (mIsInit) {
            mIsInit = false
            setValueWithAnimate(mValue)
            return
        }
        val factor = mValue * 1f / COLOR_DELTA
        println("color: $mColor,factor: $factor,value: $mValue")
        mValueCenterX = mContentWidth * factor + mStartX
        drawBackgroundTrack(canvas, mPaint, mStartX, mCenterY, mStopX, mCenterY)
        drawValueTrack(canvas, mPaint, mStartX, mCenterY, mValueCenterX, mCenterY, factor)
        drawThumbValue(canvas, mPaint, mValueCenterX, mCenterY, mThumbRadius, mValue.toString())
    }

    private fun drawBackgroundTrack(canvas: Canvas, paint: Paint, startX: Float, startY: Float, stopX: Float, stopY: Float) {
        paint.reset()
        paint.isAntiAlias = true
        paint.color = mTrackColor
        paint.strokeWidth = mTrackSize
        paint.strokeCap = Paint.Cap.ROUND
        paint.setShadowLayer(mTrackSize / 6, 0f, 0f, Color.DKGRAY.adjustAlpha(.08f))
        canvas.drawLine(startX, startY, stopX, stopY, paint)
    }

    private fun drawValueTrack(canvas: Canvas, paint: Paint, startX: Float, startY: Float, stopX: Float, stopY: Float, factor: Float) {
        paint.reset()
        paint.isAntiAlias = true
        paint.strokeWidth = mTrackSize
        paint.strokeCap = Paint.Cap.ROUND
        paint.shader = LinearGradient(startX - mTrackSize, startY, stopX, stopY, Color.TRANSPARENT, if (mColor == Color.TRANSPARENT) mColor else mColor.brightenColor(factor), Shader.TileMode.CLAMP)
        canvas.drawLine(startX, startY, stopX, stopY, paint)
    }

    private fun drawThumbValue(canvas: Canvas, paint: Paint, centerX: Float, centerY: Float, radius: Float, value: String) {
        drawThumb(canvas, paint, centerX, centerY, radius)
        drawValue(canvas, paint, centerX, centerY, radius, value)
    }

    private fun drawThumb(canvas: Canvas, paint: Paint, centerX: Float, centerY: Float, radius: Float) {
        paint.reset()
        paint.isAntiAlias = true
        paint.color = mThumbColor
        paint.style = Paint.Style.FILL
        paint.setShadowLayer(radius / 6, 0f, 2f, Color.DKGRAY.adjustAlpha(.08f))
        canvas.drawCircle(centerX, centerY, radius, paint)
    }

    private fun drawValue(canvas: Canvas, paint: Paint, centerX: Float, centerY: Float, radius: Float, value: String) {
        paint.reset()
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        paint.color = mTextColor
        paint.typeface = mTypeface
        paint.textSize = mTextSize
        paint.isFakeBoldText = true
        paint.textAlign = Paint.Align.LEFT
        val bounds = Rect()
        paint.getTextBounds(value, 0, value.length, bounds)
        val fontMetrics = paint.fontMetricsInt
        val baseline = centerY - radius + (2 * radius - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top
        canvas.drawText(value, centerX - bounds.width() / 2, baseline, mPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
                mOnValueChanged = true
                mValueCenterX = event.x
                if (mValueCenterX < mStartX) {
                    mValueCenterX = mStartX
                    mOnValueChanged = false
                }
                if (mValueCenterX > mStopX) {
                    mValueCenterX = mStopX
                    mOnValueChanged = false
                }
                mTouchOffset = mValueCenterX - event.x
                if (mOnValueChanged) {
                    mValue = ((mValueCenterX - mStartX) * COLOR_DELTA / mContentWidth).toInt()
                    mOnThumbDraggingListener?.onChange(this, mValue)
                    invalidate()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                mOnValueChanged = true
                mValueCenterX = event.x + mTouchOffset
                if (mValueCenterX < mStartX) {
                    mValueCenterX = mStartX
                    mOnValueChanged = false
                }
                if (mValueCenterX > mStopX) {
                    mValueCenterX = mStopX
                    mOnValueChanged = false
                }
                if (mOnValueChanged) {
                    mValue = ((mValueCenterX - mStartX) * COLOR_DELTA / mContentWidth).toInt()
                    mOnThumbDraggingListener?.onChange(this, mValue)
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
                mOnValueChanged = false
            }
        }
        return true
    }

    private fun isTrackTouched(event: MotionEvent): Boolean {
        return event.x >= paddingLeft
            && event.x <= measuredWidth - paddingRight
            && event.y >= paddingTop
            && event.y <= measuredHeight - paddingBottom
    }

    override fun onSaveInstanceState(): Parcelable? {
        return Bundle().apply {
            putParcelable("save_instance", super.onSaveInstanceState())
            putInt("value", mValue)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            mValue = state.getInt("value")
            super.onRestoreInstanceState(state.getParcelable("save_instance"))
            setValueWithAnimate(mValue)
            return
        }
        super.onRestoreInstanceState(state)
    }

    override fun getAccessibilityClassName(): CharSequence = ColorBar::class.java.name
}