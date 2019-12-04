package mse.mobop.mymoviesbucketlists.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_signin.*
import mse.mobop.mymoviesbucketlists.R

class SigninActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        val text = link_signup.text
        val ss = SpannableString(text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(widget.context, "Clicked", Toast.LENGTH_SHORT).show()
            }
        }
        val displayedText = "Create one"
        val startIndex = text.indexOf(displayedText)
        val endIndex = text.indexOf(displayedText) + displayedText.length
        ss.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        link_signup.text = ss

        link_signup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

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
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
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
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                toggleProgressBar()
                when (e.statusCode) {
                    CommonStatusCodes.NETWORK_ERROR ->
                        Toast.makeText(this, "No internet connection!", Toast.LENGTH_LONG).show()
                    else ->
                        Log.e("GoogleSignInResult", "Google sign in failed, error code: " + e.statusCode)
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct!!.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    toggleProgressBar()
                    Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun toggleProgressBar() {
        if (progress_circular.visibility == View.GONE) {
            progress_circular.visibility = View.VISIBLE
            google_signin_button.visibility = View.GONE
            signin_button.visibility = View.GONE
            link_signup.visibility = View.GONE
        } else {
            progress_circular.visibility = View.GONE
            google_signin_button.visibility = View.VISIBLE
            signin_button.visibility = View.VISIBLE
            link_signup.visibility = View.VISIBLE
        }
    }


    companion object {
        const val RC_SIGN_IN: Int = 1
    }
}