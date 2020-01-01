package mse.mobop.mymoviesbucketlists.ui.fragment

import android.content.Intent.getIntent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.utils.ARG_THEME_CHANGED
import mse.mobop.mymoviesbucketlists.utils.CURRENT_THEME
import mse.mobop.mymoviesbucketlists.utils.THEME_PREF


class SettingsFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.setTheme(androidx.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bandle = SettingsFragmentArgs.fromBundle(arguments!!)
        activity?.let {
            it as AppCompatActivity
            it.supportActionBar?.let {actionBar ->
                actionBar.title = getString(bandle.fragmentTitle)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            }
        }

        val sharedPref = activity!!.getSharedPreferences(THEME_PREF, AppCompatActivity.MODE_PRIVATE)
        val currentTheme = sharedPref.getBoolean(CURRENT_THEME, false)

        val themeSwitcher = findPreference<SwitchPreferenceCompat>("app_theme")
        themeSwitcher?.isChecked = currentTheme

        themeSwitcher?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, new_value ->
                sharedPref.edit()
                    .putBoolean(CURRENT_THEME, new_value as Boolean)
                    .apply()

                val intent = activity?.intent
                intent?.putExtra(ARG_THEME_CHANGED, ARG_THEME_CHANGED)
                activity?.finish()
                startActivity(intent)
                activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                true
            }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}