package mse.mobop.mymoviesbucketlists.utils

import com.google.firebase.auth.FirebaseAuth

// Intents extra arguments
const val ARG_SIGN_IN_SUCCESSFULLY = "mse.mobop.ui.activity.signin"
const val ARG_THEME_CHANGED = "mse.mobop.ui.fragment"

// Activities reauest codes
const val RC_GOOGLE_SIGN_IN: Int = 1

enum class BucketlistAction {
    ADD, EDIT
}

// Cloud Firestore collections names
const val BUCKETLIST_COLLECTION = "bucketlists"
const val USER_COLLECTION = "users"

// Other constants
const val BASE_URL_API = "https://api.themoviedb.org/3/"
const val BASE_URL_IMG = "https://image.tmdb.org/t/p/w154"
const val BASE_URL_IMG_POSTER = "https://image.tmdb.org/t/p/w500"
const val BASE_URL_IMG_BACKDROP = "https://image.tmdb.org/t/p/w780"

// For shared preferences
const val SIGN_IN_PREF = "mse.mobop.ui.activity.signin"
const val SING_IN_EMAIL = "mse.mobop.ui.activity.signin.email"
const val THEME_PREF = "app.theme"
val CURRENT_THEME
    get() = "app.theme.current." + FirebaseAuth.getInstance().currentUser!!.uid