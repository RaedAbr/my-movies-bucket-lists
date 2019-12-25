package mse.mobop.mymoviesbucketlists.utils

// Bundle arguments between fragments
const val ARG_SIGN_IN_SUCCESSFULLY = "mse.mobop.ui.activity.signin"

// Activities reauest codes
const val RC_GOOGLE_SIGN_IN: Int = 1

enum class BucketlistAction {
    ADD, EDIT
}

// Cloud Firestore collections names
const val BUCKETLIST_COLLECTION = "bucketlists"
const val USER_COLLECTION = "users"

// Other constants
const val ITEM = 0
const val LOADING = 1
const val BASE_URL_IMG = "https://image.tmdb.org/t/p/w154"
const val BASE_URL_IMG_POSTER = "https://image.tmdb.org/t/p/w500"