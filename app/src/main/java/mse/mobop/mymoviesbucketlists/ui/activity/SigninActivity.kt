package mse.mobop.mymoviesbucketlists.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.activity_signin.email_textview
import kotlinx.android.synthetic.main.activity_signin.password_textview
import mse.mobop.mymoviesbucketlists.utils.ARG_SIGN_IN_SUCCESSFULLY
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.utils.RC_GOOGLE_SIGN_IN
import mse.mobop.mymoviesbucketlists.firestore.UserFirestore
import mse.mobop.mymoviesbucketlists.utils.SIGN_IN_PREF
import mse.mobop.mymoviesbucketlists.utils.SING_IN_EMAIL
import java.util.*

class SigninActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        val signUpText = link_signup.text
        val spannable = SpannableString(signUpText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@SigninActivity, SignupActivity::class.java))
                finish()
            }
        }
        spannable.setSpan(clickableSpan, 0, signUpText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        link_signup.text = spannable
        link_signup.movementMethod = LinkMovementMethod.getInstance()

        // Get auto complete emails list from shared preferences
        val preferences = getSharedPreferences(SIGN_IN_PREF, MODE_PRIVATE)
        val emailsHistoryList = preferences.getStringSet(SING_IN_EMAIL, setOf<String>())!!
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            emailsHistoryList.toList())
        email_textview.setAdapter(adapter)


        signin_button.setOnClickListener { view ->
            val email = email_textview.text.toString().trim()
            val password = password_textview.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                return@setOnClickListener
            }

            toggleProgressBar()

            // Save email in the shared preferences
            var emailsHistory = preferences.getStringSet(SING_IN_EMAIL, setOf<String>())!!
            emailsHistory = emailsHistory.plus(email)
            preferences.edit {
                putStringSet(SING_IN_EMAIL, emailsHistory)
                apply()
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        goToMainActivity()
                    } else {
                        toggleProgressBar()
                        Log.e("Signin", it.exception!!.message!!)
                        Snackbar.make(view, it.exception!!.message!!, Snackbar.LENGTH_SHORT).show()
                    }
                }
        }

        google_signin_button.setOnClickListener {
            toggleProgressBar()
            // Configure Google Sign In
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInClient.signOut()
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent,
                RC_GOOGLE_SIGN_IN
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                toggleProgressBar()
                when (e.statusCode) {
                    CommonStatusCodes.NETWORK_ERROR ->
                        Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show()
                    else -> {
                        Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct!!.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val currentUser = FirebaseAuth.getInstance().currentUser!!
                    val username = currentUser.displayName!!.trim().toLowerCase(Locale.getDefault())
                        .replace("\\s".toRegex(), "_")
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()

                    FirebaseAuth.getInstance().currentUser
                        ?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("User", "User profile updated.")
                                goToMainActivity()
                            } else {
                                toggleProgressBar()
                                Toast.makeText(this, getString(R.string.google_sign_in_failed), Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                }
            }
    }

    private fun goToMainActivity() {
        UserFirestore.addCurrentUserIfFirstTime {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(
                ARG_SIGN_IN_SUCCESSFULLY,
                ARG_SIGN_IN_SUCCESSFULLY
            )
            startActivity(intent)
            finish()
        }
    }

    private fun toggleProgressBar() {
        if (progress_circular.visibility == View.GONE) {
            progress_circular.visibility = View.VISIBLE
            google_signin_button.visibility = View.GONE
            signin_button.visibility = View.GONE
            link_signup_layout.visibility = View.GONE
            email_textview.isEnabled = false
            password_textview.isEnabled = false
        } else {
            progress_circular.visibility = View.GONE
            google_signin_button.visibility = View.VISIBLE
            signin_button.visibility = View.VISIBLE
            link_signup_layout.visibility = View.VISIBLE
            email_textview.isEnabled = true
            password_textview.isEnabled = true
        }
    }
}