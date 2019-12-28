package mse.mobop.mymoviesbucketlists.model

import com.google.firebase.auth.FirebaseUser

data class User (
    var id: String? = null,
    var name: String = ""
) {
    constructor(user: FirebaseUser?) : this(user!!.uid, user.displayName!!)

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        other as User
        return id == other.id &&
                name == other.name
    }
}