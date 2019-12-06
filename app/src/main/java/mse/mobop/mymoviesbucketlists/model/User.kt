package mse.mobop.mymoviesbucketlists.model

import com.google.firebase.auth.FirebaseUser

data class User (
    var id: String? = null,
    var name: String = ""
) {
    constructor(user: FirebaseUser?) : this(user!!.uid, user.displayName!!)
}