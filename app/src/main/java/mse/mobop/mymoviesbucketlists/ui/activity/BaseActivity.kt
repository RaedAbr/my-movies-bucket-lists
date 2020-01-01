package mse.mobop.mymoviesbucketlists.ui.activity

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.utils.CURRENT_THEME
import mse.mobop.mymoviesbucketlists.utils.THEME_PREF

open class BaseActivity : AppCompatActivity() {
    private var currentTheme: Boolean = false
    private lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = getSharedPreferences(THEME_PREF, MODE_PRIVATE)
        currentTheme = sharedPref.getBoolean(CURRENT_THEME, false)

        setAppTheme(currentTheme)
    }

    override fun onResume() {
        super.onResume()
        val theme = sharedPref.getBoolean(CURRENT_THEME, false)
        if(currentTheme != theme)
            recreate()
    }

    private fun setAppTheme(currentTheme: Boolean) {
        setTheme(if (currentTheme) R.style.AppThemeDark_NoActionBar else R.style.AppTheme_NoActionBar)
    }
}