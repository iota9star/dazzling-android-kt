package io.nichijou.dazzling.simple

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import io.nichijou.color.isColorDark
import io.nichijou.color.isColorLight
import io.nichijou.color.randomColor
import io.nichijou.dazzling.Dazzling
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
  private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)
    def.setOnClickListener {
      Dazzling.showNow(supportFragmentManager) {
        onColorChecked(this@MainActivity::setColor)
        onOKPressed { c ->
          it as Button
          it.tint(c, c.isColorDark())
        }
      }
    }
    disable_alpha.setOnClickListener {
      Dazzling.showNow(supportFragmentManager) {
        isEnableAlpha = false
        onColorChecked(this@MainActivity::setColor)
        onOKPressed { c ->
          it as Button
          it.tint(c, c.isColorDark())
        }
      }
    }
    disable_bar.setOnClickListener {
      Dazzling.showNow(supportFragmentManager) {
        isEnableColorBar = false
        onColorChecked(this@MainActivity::setColor)
        onOKPressed { c ->
          it as Button
          it.tint(c, c.isColorDark())
        }
      }
    }
    preset.setOnClickListener {
      Dazzling.showNow(supportFragmentManager) {
        presetColors = mutableListOf(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.BLACK, Color.WHITE)
        onColorChecked(this@MainActivity::setColor)
        onOKPressed { c ->
          it as Button
          it.tint(c, c.isColorDark())
        }
      }
    }
    selected.setOnClickListener {
      Dazzling.showNow(supportFragmentManager) {
        presetColors = mutableListOf(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.BLACK, Color.WHITE)
        preselectedColor = Color.YELLOW
        onColorChecked(this@MainActivity::setColor)
        onOKPressed { c ->
          it as Button
          it.tint(c, c.isColorDark())
        }
      }
    }
    step_factor.setOnClickListener {
      Dazzling.showNow(supportFragmentManager) {
        stepFactor = 1.3f
        onColorChecked(this@MainActivity::setColor)
        onOKPressed { c ->
          it as Button
          it.tint(c, c.isColorDark())
        }
      }
    }
    random_size.setOnClickListener {
      Dazzling.showNow(supportFragmentManager) {
        randomSize = 16
        onColorChecked(this@MainActivity::setColor)
        onOKPressed { c ->
          it as Button
          it.tint(c, c.isColorDark())
        }
      }
    }
    change_bg.setOnClickListener {
      Dazzling.showNow(supportFragmentManager) {
        backgroundColor = randomColor()
        onColorChecked(this@MainActivity::setColor)
        onOKPressed { c ->
          it as Button
          it.tint(c, c.isColorDark())
        }
      }
    }
    round_corner.setOnClickListener {
      val radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, this@MainActivity.resources.displayMetrics) + 0.5f
      Dazzling.showNow(supportFragmentManager) {
        bgCornerRadii = floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f)
        onColorChecked(this@MainActivity::setColor)
        onOKPressed { c ->
          it as Button
          it.tint(c, c.isColorDark())
        }
      }
    }
    val dp16 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, this@MainActivity.resources.displayMetrics) + 0.5f
    change_margin.setOnClickListener {
      Dazzling.showNow(supportFragmentManager) {
        bgCornerRadii = floatArrayOf(dp16, dp16, dp16, dp16, dp16, dp16, dp16, dp16)
        paletteMargin = intArrayOf(dp16.toInt(), dp16.toInt(), dp16.toInt(), dp16.toInt())
        onColorChecked(this@MainActivity::setColor)
        onOKPressed { c ->
          it as Button
          it.tint(c, c.isColorDark())
        }
      }
    }
    java.setOnClickListener {
      Dazzling.builder()
        .isEnableAlpha(true)
        .isEnableColorBar(true)
        .setBackgroundColor(Color.parseColor("#292330"))
        .setPreselectedColor(Color.YELLOW)
        .setRandomSize(24)
        .setStepFactor(.1f)
        .setBgCornerRadii(floatArrayOf(dp16, dp16, dp16, dp16, dp16, dp16, dp16, dp16))
        .setPaletteMargin(intArrayOf(dp16.toInt(), dp16.toInt(), dp16.toInt(), dp16.toInt()))
        .setPresetColors(mutableListOf(Color.YELLOW, Color.WHITE, Color.BLACK, Color.MAGENTA, Color.CYAN, Color.BLUE))
        .setOnColorChecked(object : Dazzling.Builder.OnColorChecked {
          override fun onChecked(value: Int) {
            setColor(value)
          }
        })
        .setOnOKPressed(object : Dazzling.Builder.OnOKPressed {
          override fun onPressed(value: Int) {
            it as Button
            it.tint(value, value.isColorDark())
          }
        })
        .showNow(supportFragmentManager)
    }
  }

  private fun setColor(c: Int) {
    toolbar.setBackgroundColor(c)
    toolbar.setTitleTextColor(if (c.isColorLight()) Color.BLACK else Color.WHITE)
    setStatusBarColorCompat(c)
    setLightStatusBarCompat(c.isColorLight())
  }
}
