package mse.mobop.mymoviesbucketlists.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import mse.mobop.mymoviesbucketlists.ARG_SIGN_IN_SUCCESSFULLY
import mse.mobop.mymoviesbucketlists.R
import org.jetbrains.anko.contentView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private var user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        if (user == null) {
//            startActivity(Intent(this, SigninActivity::class.java))
//            finish()
//            return
//        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow,
                R.id.nav_tools,
                R.id.nav_share,
                R.id.nav_send
            ), drawerLayout
        )

        navView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)
        navView.getHeaderView(0).findViewById<TextView>(R.id.username_textview).text = user!!.displayName
        navController = findNavController(R.id.nav_host_fragment)
//        navView.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()

        val fromSignin = intent.getStringExtra(ARG_SIGN_IN_SUCCESSFULLY)
        if (fromSignin != null) {
            Snackbar.make(contentView!!, "Welcome " + user!!.displayName, Snackbar.LENGTH_SHORT).show()
        }

        user?.let {
            // Name, email address, and profile photo Url
            val name = user!!.displayName
            val email = user!!.email
            val photoUrl = user!!.photoUrl

            // Check if user's email is verified
            val emailVerified = user!!.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
//            val uid = user.uid

            Log.e("name", name!!)
            Log.e("email", email!!)
            Log.e("photoUrl", photoUrl.toString())
            Log.e("emailVerified", emailVerified.toString())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment)
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
                drawerLayout.closeDrawers()
                menuItem.onNavDestinationSelected(navController)
//                navView.setupWithNavController(navController)
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        Log.e("result", requestCode.toString())
//        if (requestCode == RC_SIGN_IN_SUCCESSFULLY) {
//            Snackbar.make(contentView!!, "Welcome " + user!!.displayName, Snackbar.LENGTH_SHORT).show()
//        }
//    }
}
