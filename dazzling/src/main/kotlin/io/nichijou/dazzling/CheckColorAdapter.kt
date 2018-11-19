package io.nichijou.dazzling

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView

internal class CheckColorAdapter : RecyclerView.Adapter<CheckColorAdapter.ViewHolder>() {

    private var colors: MutableList<Int> = mutableListOf()
    private var mSelectedColor = 0
    private var hasSelected = false

    private var onColorChecked: ((color: Int) -> Unit)? = null

    fun setOnColorChecked(onColorChecked: ((color: Int) -> Unit)?) {
        this.onColorChecked = onColorChecked
    }

    internal fun newColors(newColors: MutableList<Int>, @ColorInt selectedColor: Int = 0) {
        colors = newColors
        mSelectedColor = selectedColor
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = colors.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_color_check_view, parent, false) as CheckColorView)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(colors[position])

    internal inner class ViewHolder(private val view: CheckColorView) : RecyclerView.ViewHolder(view) {

        fun bind(color: Int) {
            view.setOnClickListener {
                mSelectedColor = color
                hasSelected = false
                onColorChecked?.invoke(color)
                notifyDataSetChanged()
            }
            val checked = mSelectedColor == color
            view.setColorAndCheckState(color, checked && !hasSelected)
            if (checked) {
                hasSelected = true
            }
        }
    }
}