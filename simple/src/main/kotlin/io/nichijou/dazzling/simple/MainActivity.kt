package io.nichijou.dazzling.simple

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import io.nichijou.dazzling.Dazzling
import io.nichijou.dazzling.isColorDark
import io.nichijou.dazzling.isColorLight
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        no_alpha_color.setOnClickListener {
            Dazzling.showNow(supportFragmentManager) {
                enableAlpha = false
                backgroundColor = Color.WHITE
                randomSize = 16
                onColorChecked(this@MainActivity::setColor)
                onOKPressed { c ->
                    it as Button
                    it.tint(c, c.isColorDark())
                }
            }
        }
        alpha_color.setOnClickListener {
            Dazzling.showNow(supportFragmentManager) {
                enableAlpha = true
                backgroundColor = Color.WHITE
                randomSize = 16
                onColorChecked(this@MainActivity::setColor)
                onOKPressed { c ->
                    it as Button
                    it.tint(c, c.isColorDark())
                }
            }
        }
        alpha_color.performClick()
    }

    private fun setColor(c: Int) {
        toolbar.setBackgroundColor(c)
        toolbar.setTitleTextColor(if (c.isColorLight()) Color.BLACK else Color.WHITE)
        setStatusBarColorCompat(c)
        setLightStatusBarCompat(c.isColorLight())
    }
}