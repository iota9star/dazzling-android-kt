package io.nichijou.dazzling

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.FloatRange
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class Dazzling : BottomSheetDialogFragment() {

    private var mHexEditor: EditText? = null
    private var mPaletteWrapper: NestedScrollView? = null
    private var mPreColor: CheckColorView? = null
    private var mPreColorWrapper: LinearLayout? = null
    private var mOkBtn: Button? = null
    private var mRandom: ImageButton? = null
    private val mColorAdapter = CheckColorAdapter()

    private var mOnOKPressed: ((color: Int) -> Unit)? = null

    var enableAlpha = false
    var presetColor: MutableList<Int>? = null
    var selectedColor = 0
    var randomSize = 11
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.Dazzling_BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_palette, container, false).apply {
            mPreColor = findViewById(R.id.pre_color)
            mHexEditor = findViewById<EditText>(R.id.hex_editor).apply {
                addTextChangedListener(HexWatcher())
                clearFocus()
            }
            findViewById<RecyclerView>(R.id.color_list).apply {
                val layoutManager = FlexboxLayoutManager(context)
                layoutManager.flexDirection = FlexDirection.ROW
                layoutManager.justifyContent = JustifyContent.FLEX_START
                layoutManager.flexWrap = FlexWrap.WRAP
                this.layoutManager = layoutManager
                this.adapter = mColorAdapter
                mColorAdapter.newColors(presetColor ?: randomColors(randomSize), selectedColor)
            }
            mRandom = findViewById<ImageButton>(R.id.random).apply {
                setOnClickListener {
                    mHexEditor?.text = null
                    mPreColor?.color = Color.BLACK
                    mColorAdapter.newColors(randomColors(randomSize), selectedColor)
                }
            }
            findViewById<TextView>(R.id.title).setTextColor(if (backgroundColor.isColorLight()) Color.BLACK else Color.WHITE)
            mPaletteWrapper = findViewById(R.id.palette_wrapper)
            mPreColorWrapper = findViewById(R.id.pre_color_wrapper)
            mOkBtn = findViewById<Button>(R.id.ok_btn).apply {
                setOnClickListener {
                    mOnOKPressed?.invoke(selectedColor)
                    dismiss()
                }
            }
            setPaletteWidgetColor()
        }
    }

    internal inner class HexWatcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            var hex = s.toString()
            if (!hex.contains("#")) {
                hex = "#$hex"
                mHexEditor?.setText(hex)
                mHexEditor?.setSelection(hex.length)
                return
            }
            val hexLength: Int = if (enableAlpha) 9 else 7
            if (hex.length > hexLength) {
                val result = hex.substring(0, hexLength)
                mHexEditor?.setText(result)
                mHexEditor?.setSelection(result.length)
                return
            }
            if (hex.contains(Regex("[a-f]"))) {
                mHexEditor?.setText(hex.toUpperCase())
                mHexEditor?.setSelection(hex.length)
                return
            }
            if (Regex("#[A-F0-9]{6}").matches(hex)
                || Regex("#[A-F0-9]{8}").matches(hex)) {
                val color = Color.parseColor(hex)
                mPreColor?.color = color
                mColorAdapter.newColors(color.stepColor(stepFactor), color)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    fun onColorChecked(onColorChecked: (color: Int) -> Unit) {
        mColorAdapter.setOnColorChecked {
            selectedColor = it
            val hex = it.toHexColor()
            mHexEditor?.setText(if (enableAlpha) hex else hex.substring(2))
            onColorChecked.invoke(it)
        }
    }

    fun onOKPressed(onOK: (color: Int) -> Unit) {
        mOnOKPressed = onOK
    }

    companion object {

        private const val TAG = "dazzling_palette"

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