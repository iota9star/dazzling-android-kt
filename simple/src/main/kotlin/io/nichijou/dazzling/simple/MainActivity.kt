package io.nichijou.dazzling.simple

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import io.nichijou.dazzling.Dazzling
import io.nichijou.dazzling.isColorDark
import io.nichijou.dazzling.isColorLight
import io.nichijou.dazzling.randomColor
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
        random_bg.setOnClickListener {
            Dazzling.showNow(supportFragmentManager) {
                backgroundColor = randomColor()
                onColorChecked(this@MainActivity::setColor)
                onOKPressed { c ->
                    it as Button
                    it.tint(c, c.isColorDark())
                }
            }
        }
    }

    private fun setColor(c: Int) {
        toolbar.setBackgroundColor(c)
        toolbar.setTitleTextColor(if (c.isColorLight()) Color.BLACK else Color.WHITE)
        setStatusBarColorCompat(c)
        setLightStatusBarCompat(c.isColorLight())
    }
}