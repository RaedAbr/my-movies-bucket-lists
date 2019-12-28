package mse.mobop.mymoviesbucketlists.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_signup.*
import mse.mobop.mymoviesbucketlists.utils.ARG_SIGN_IN_SUCCESSFULLY
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.firestore.UserFirestore

class SignupActivity: AppCompatActivity() {

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val text = link_signin.text
        val ss = SpannableString(text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@SignupActivity, SigninActivity::class.java))
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
        }
        val displayedText = "Sign in"
        val startIndex = text.indexOf(displayedText)
        val endIndex = text.indexOf(displayedText) + displayedText.length
        ss.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        link_signin.text = ss
        link_signin.movementMethod = LinkMovementMethod.getInstance()

        signup_button.setOnClickListener {
            val username = username_textview.text.toString().trim().toLowerCase()
            val email = email_textview.text.toString().trim()
            val password = password_textview.text.toString().trim()
            val confirmPassword = confirm_password_textview.text.toString().trim()

            // Alphanumeric string that may include _ and – having a length of 3 to 16 characters
            val regex = "^[a-z0-9_.]{3,16}$".toRegex()
            if (!regex.matches(username)) {
                Toast.makeText(this, "Username must be alphanumeric of lenght 3 to 16, starting with a character", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(email)) {
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                return@setOnClickListener
            }

            if (!TextUtils.equals(password, confirmPassword)) {
                Toast.makeText(this, "Wrong password confirmation!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            toggleProgressBar()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Signed up successfully", Toast.LENGTH_SHORT).show()
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()

                        FirebaseAuth.getInstance().currentUser
                            ?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("User", "User profile updated.")
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
                            }
                    } else {
                        Log.e("Signup", it.exception!!.message!!)
                        Toast.makeText(this, it.exception!!.message!!, Toast.LENGTH_SHORT).show()
                        toggleProgressBar()
                    }
                }
        }
    }

    private fun toggleProgressBar() {
        if (progress_circular.visibility == View.GONE) {
            progress_circular.visibility = View.VISIBLE
            signup_button.visibility = View.GONE
            link_signin.visibility = View.GONE
            username_textview.isEnabled = false
            email_textview.isEnabled = false
            password_textview.isEnabled = false
            confirm_password_textview.isEnabled = false
        } else {
            progress_circular.visibility = View.GONE
            signup_button.visibility = View.VISIBLE
            link_signin.visibility = View.VISIBLE
            username_textview.isEnabled = true
            email_textview.isEnabled = true
            password_textview.isEnabled = true
            confirm_password_textview.isEnabled = true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, SigninActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }
}
