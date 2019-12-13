package mse.mobop.mymoviesbucketlists.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import mse.mobop.mymoviesbucketlists.utils.USER_COLLECTION
import mse.mobop.mymoviesbucketlists.model.User
import java.util.*

object UserFirestore {
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val userCollRef: CollectionReference
        get() = firestoreInstance.collection(USER_COLLECTION)

    fun addCurrentUserIfFirstTime(onComplete: () -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser!!
        val currentUserDocRef = userCollRef.document(currentUser.uid)
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newUser = User(currentUser.uid, currentUser.displayName!!)
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            }
            else
                onComplete()
        }
    }

    fun searchUserQuery(searchText: String): Query? {
        if (searchText.isEmpty()) return null
        val searchTextUpper = searchText.toLowerCase(Locale.getDefault())
        return userCollRef
            .orderBy("name")
            .startAt(searchTextUpper)
            .endAt(searchTextUpper + "\uf8ff")
    }
}