package io.nichijou.dazzling.internal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import io.nichijou.dazzling.R

internal class ColorAdapter : RecyclerView.Adapter<ColorAdapter.ViewHolder>() {

  private var mColors: List<Int> = mutableListOf()
  private var mSelectedColor = 0
  private var mHasSelected = false

  private var mOnColorChecked: ((color: Int) -> Unit)? = null

  fun setOnColorChecked(onColorChecked: ((color: Int) -> Unit)?) {
    this.mOnColorChecked = onColorChecked
  }

  internal fun newColors(newColors: List<Int>, @ColorInt selectedColor: Int = 0) {
    mColors = newColors
    mSelectedColor = selectedColor
    notifyDataSetChanged()
  }

  override fun getItemCount(): Int = mColors.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_color_check_view, parent, false) as ColorView)

  override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(mColors[position])

  internal inner class ViewHolder(private val view: ColorView) : RecyclerView.ViewHolder(view) {

    fun bind(color: Int) {
      view.setOnClickListener {
        mSelectedColor = color
        mHasSelected = false
        mOnColorChecked?.invoke(color)
      }
      val checked = mSelectedColor == color
      view.setColorAndCheckState(color, checked && !mHasSelected)
      if (checked) {
        mHasSelected = true
      }
    }
  }
}
