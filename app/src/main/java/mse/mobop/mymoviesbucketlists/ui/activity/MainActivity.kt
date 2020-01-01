package mse.mobop.mymoviesbucketlists.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.utils.ARG_SIGN_IN_SUCCESSFULLY
import mse.mobop.mymoviesbucketlists.utils.ARG_THEME_CHANGED
import org.jetbrains.anko.contentView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private var user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val fromSignin = intent.getStringExtra(ARG_SIGN_IN_SUCCESSFULLY)
        if (fromSignin != null) {
            Snackbar.make(contentView!!,
                getString(R.string.welcome) + " " + user!!.displayName, Snackbar.LENGTH_SHORT)
                .show()
            intent.removeExtra(ARG_SIGN_IN_SUCCESSFULLY)
        }

        setSupportActionBar(toolbar)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.BucketlistFragment
            ), drawer_layout
        )

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.getHeaderView(0).username_textview.text = user!!.displayName
        navController = findNavController(R.id.nav_host_fragment)

        // Check if the start activity action comes from the SettingsFragment
        val fromSettings = intent.getStringExtra(ARG_THEME_CHANGED)
        if (fromSettings != null) {
            navController.navigate(R.id.action_BucketlistFragment_to_settingsFragment)
            intent.removeExtra(ARG_THEME_CHANGED)
        }
    }

    override fun onStart() {
        super.onStart()

        user?.let {
            val name = user!!.displayName
            val email = user!!.email
            val photoUrl = user!!.photoUrl

            // Check if user's email is verified
            val emailVerified = user!!.isEmailVerified

            Log.e("name", name!!)
            Log.e("email", email!!)
            Log.e("photoUrl", photoUrl.toString())
            Log.e("emailVerified", emailVerified.toString())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        Log.e("itemId", menuItem.itemId.toString())
        return when(menuItem.itemId) {
            R.id.nav_signout -> {
                Log.e("signout", "signing out...")
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, SigninActivity::class.java))
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
                true
            }
            else -> {
                drawer_layout.closeDrawers()
                menuItem.onNavDestinationSelected(navController)
            }
        }
    }
}
