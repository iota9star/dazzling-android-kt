package io.nichijou.dazzling

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.nichijou.dazzling.internal.ColorAdapter
import io.nichijou.dazzling.internal.ColorBar
import io.nichijou.dazzling.internal.ColorView
import io.nichijou.dazzling.internal.dp2px
import io.nichijou.dazzling.internal.isLandscape
import io.nichijou.dazzling.internal.isTablet
import io.nichijou.dazzling.internal.tint
import kotlinx.android.synthetic.main.dialog_palette.view.*


class Dazzling : BottomSheetDialogFragment() {

    private var mHexEditor: EditText? = null
    private var mPaletteWrapper: NestedScrollView? = null
    private var mPreColor: ColorView? = null
    private var mPreColorWrapper: LinearLayout? = null
    private var mOkBtn: Button? = null
    private var mRandom: ImageButton? = null
    private var mPreset: ImageButton? = null
    private var mTitle: TextView? = null
    private var mColorList: RecyclerView? = null
    private var mAlpha: ColorBar? = null
    private var mRed: ColorBar? = null
    private var mGreen: ColorBar? = null
    private var mBlue: ColorBar? = null
    private var mCurrentColor = 0
    private val mColorAdapter = ColorAdapter()

    private var mOnOKPressed: ((color: Int) -> Unit)? = null

    private var mValueChangeByDragging = false
    private var mIsInit = true

    var isEnableAlpha = true
    var isEnableColorBar = true
    var presetColors: MutableList<Int>? = null
    @ColorInt
    var preselectedColor = Color.argb(255, 127, 127, 127)
    var randomSize = 8
        set(value) {
            field = if (value < 0) 0 else value
        }
    @FloatRange(from = 0.01, to = 2.0)
    var stepFactor = .2f

    var backgroundColor = Color.WHITE
        set(value) {
            field = value
            setPaletteWidgetColor()
        }

    private fun setPaletteWidgetColor() {
        val isDark = backgroundColor.isColorDark()
        val color = if (isDark) Color.WHITE else Color.BLACK
        mTitle?.setTextColor(if (isDark) Color.WHITE else Color.BLACK)
        mPaletteWrapper?.apply {
            this.background = (background as GradientDrawable).apply {
                setColor(backgroundColor)
            }
        }
        mPreColorWrapper?.apply {
            background = (background as GradientDrawable).apply {
                setStroke(context.dp2px(2f), color)
            }
        }
        mHexEditor?.tint(color, isDark)
        mOkBtn?.tint(color, isDark)
        mRandom?.apply {
            if (drawable != null) {
                setImageDrawable(drawable?.tint(color))
            }
        }
        mPreset?.apply {
            if (drawable != null) {
                setImageDrawable(drawable?.tint(color))
            }
        }
    }

    private val mOnThumbInDraggingListener = object : ColorBar.OnThumbInDraggingListener {
        override fun onChange(view: ColorBar, value: Int) {
            mValueChangeByDragging = true
            when (view.id) {
                R.id.alpha -> mHexEditor?.setText(mCurrentColor.setAlpha(value).toHexColor())
                R.id.red -> mHexEditor?.setText(mCurrentColor.setRed(value).toHexColor())
                R.id.green -> mHexEditor?.setText(mCurrentColor.setGreen(value).toHexColor())
                R.id.blue -> mHexEditor?.setText(mCurrentColor.setBlue(value).toHexColor())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.Dazzling_BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_palette, container, false).apply {
            mCurrentColor = if (isEnableAlpha) preselectedColor else preselectedColor.stripAlpha()
            if (isEnableColorBar) {
                if (isEnableAlpha) {
                    LayoutInflater.from(context).inflate(R.layout.argb, argb, true)
                    mAlpha = findViewById<ColorBar>(R.id.alpha).apply {
                        setOnValueChangeListener(mOnThumbInDraggingListener)
                    }
                } else {
                    LayoutInflater.from(context).inflate(R.layout.rgb, argb, true)
                }
                mRed = findViewById<ColorBar>(R.id.red).apply {
                    setOnValueChangeListener(mOnThumbInDraggingListener)
                }
                mGreen = findViewById<ColorBar>(R.id.green).apply {
                    setOnValueChangeListener(mOnThumbInDraggingListener)
                }
                mBlue = findViewById<ColorBar>(R.id.blue).apply {
                    setOnValueChangeListener(mOnThumbInDraggingListener)
                }
            } else {
                argb.visibility = View.GONE
            }
            mTitle = findViewById(R.id.title)
            mPreColor = findViewById(R.id.pre_color)
            mColorList = findViewById<RecyclerView>(R.id.color_list).apply {
                this.layoutManager = FlexboxLayoutManager(context).apply {
                    this.flexDirection = FlexDirection.ROW
                    this.justifyContent = JustifyContent.FLEX_START
                    this.flexWrap = FlexWrap.WRAP
                }
                this.adapter = mColorAdapter
            }
            mRandom = findViewById<ImageButton>(R.id.random).apply {
                setOnClickListener {
                    mValueChangeByDragging = false
                    setDefault(false)
                }
            }
            if (!presetColors.isNullOrEmpty()) {
                mPreset = findViewById<ImageButton>(R.id.preset).apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        mValueChangeByDragging = false
                        setDefault(true)
                    }
                }
            }
            mPaletteWrapper = findViewById(R.id.palette_wrapper)
            mPreColorWrapper = findViewById(R.id.pre_color_wrapper)
            mOkBtn = findViewById<Button>(R.id.ok_btn).apply {
                setOnClickListener {
                    mOnOKPressed?.invoke(mCurrentColor)
                    dismiss()
                }
            }
            mHexEditor = findViewById<EditText>(R.id.hex_editor).apply {
                addTextChangedListener(HexWatcher())
                clearFocus()
            }
            setPaletteWidgetColor()
            mHexEditor?.setText(if (mCurrentColor == Color.TRANSPARENT) "#00000000" else mCurrentColor.toHexColor())
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                if (context.isLandscape() || context.isTablet()) {
                    window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet).let {
                    val behavior = BottomSheetBehavior.from(it)
                    behavior.peekHeight = it.measuredHeight
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })
    }

    internal inner class HexWatcher : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            val hex = s.toString()
            val newHex = resolveHexEditorValue(hex)
            if (hex != newHex) {
                mHexEditor?.setText(newHex)
                mHexEditor?.setSelection(newHex.length)
                return
            }
            if (Regex("#[A-F0-9]{6}").matches(hex)
                || Regex("#[A-F0-9]{8}").matches(hex)) {
                updateColor(Color.parseColor(hex))
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    private fun resolveHexEditorValue(value: String): String {
        var hex = value
        if (!hex.contains("#")) {
            hex = "#$hex"
        }
        val hexLength: Int = if (isEnableAlpha) 9 else 7
        if (hex.length > hexLength) {
            hex = if (Regex("#[A-F0-9]{8}").matches(hex)) "#" + hex.substring(hex.length - hexLength + 1, hex.length)
            else hex.substring(0, hexLength)
        }
        if (hex.contains(Regex("[a-f]"))) {
            hex = hex.toUpperCase()
        }
        return hex
    }

    private fun updateColor(color: Int) {
        mCurrentColor = color
        if (isEnableColorBar) {
            if (mValueChangeByDragging) {
                bindColorBar(color)
            } else {
                bindColorBarWithAnimate(color)
            }
        }
        mPreColor?.setColor(color)
        if (mIsInit) {
            mIsInit = false
            mColorAdapter.newColors(presetColors ?: randomColors(randomSize), preselectedColor)
        } else {
            mColorAdapter.newColors(color.stepColor(stepFactor), color)
        }
    }

    private fun setDefault(isPreset: Boolean) {
        mCurrentColor = preselectedColor
        mHexEditor?.setText(if (mCurrentColor == Color.TRANSPARENT) "#00000000" else mCurrentColor.toHexColor())
        mPreColor?.setColor(mCurrentColor)
        if (isEnableColorBar) {
            if (mValueChangeByDragging) {
                bindColorBar(mCurrentColor)
            } else {
                bindColorBarWithAnimate(mCurrentColor)
            }
        }
        mColorAdapter.newColors(if (isPreset) presetColors!! else randomColors(randomSize), mCurrentColor)
    }

    private fun bindColorBarWithAnimate(color: Int) {
        if (isEnableAlpha) mAlpha?.setValueWithAnimate(Color.alpha(color))
        mRed?.setValueWithAnimate(Color.red(color))
        mGreen?.setValueWithAnimate(Color.green(color))
        mBlue?.setValueWithAnimate(Color.blue(color))
    }

    private fun bindColorBar(color: Int) {
        if (isEnableAlpha) mAlpha?.setValue(Color.alpha(color))
        mRed?.setValue(Color.red(color))
        mGreen?.setValue(Color.green(color))
        mBlue?.setValue(Color.blue(color))
    }

    fun onColorChecked(onColorChecked: (color: Int) -> Unit) {
        mColorAdapter.setOnColorChecked {
            mValueChangeByDragging = false
            mCurrentColor = it
            val hex = it.toHexColor()
            mHexEditor?.setText(if (isEnableAlpha) hex else hex.substring(2))
            onColorChecked.invoke(it)
        }
    }

    fun onOKPressed(onOKPressed: (color: Int) -> Unit) {
        mOnOKPressed = onOKPressed
    }

    companion object {

        const val TAG = "dazzling_palette"

        fun show(fragmentManager: FragmentManager, tag: String = TAG, block: Dazzling.() -> Unit): Dazzling {
            val dazzling = Dazzling()
            dazzling.block()
            dazzling.show(fragmentManager, tag)
            return dazzling
        }

        fun show(transaction: FragmentTransaction, tag: String = TAG, block: Dazzling.() -> Unit): Dazzling {
            val dazzling = Dazzling()
            dazzling.block()
            dazzling.show(transaction, tag)
            return dazzling
        }

        fun showNow(fragmentManager: FragmentManager, tag: String = TAG, block: Dazzling.() -> Unit): Dazzling {
            val dazzling = Dazzling()
            dazzling.block()
            dazzling.showNow(fragmentManager, tag)
            return dazzling
        }
    }
}