package mse.mobop.mymoviesbucketlists.firestore

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import mse.mobop.mymoviesbucketlists.model.Bucketlist

object BucketlistFirestore {
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val bucketlistsCollRef: CollectionReference
        get() = firestoreInstance.collection("bucketlists")

    fun createBucketlist(bucketlist: Bucketlist) {
        val bucketlistDocRef = bucketlistsCollRef.document()
        bucketlist.id = bucketlistDocRef.id
        bucketlistDocRef.set(bucketlist)
            .addOnSuccessListener {
                Log.e("bucketlist", "DocumentSnapshot written with ID: ${bucketlistDocRef.id}")
            }
            .addOnFailureListener { e -> Log.e("bucketlist", "Error adding document", e) }
    }

    fun getAllBucketlists(): LiveData<List<Bucketlist>> {
        val allBucketlists = mutableListOf<Bucketlist>()
        bucketlistsCollRef.whereEqualTo("createdBy", FirebaseAuth.getInstance().currentUser!!.uid)
            .addSnapshotListener { documents, e ->
                if (e != null) {
                    Log.w("Bucketlist", "Listen failed.", e)
                    return@addSnapshotListener
                }
                Log.e("bucketlist", "DocumentSnapshot written with ID: ${documents!!.count()}")
                documents.forEach {
                    allBucketlists.add(it.toObject(Bucketlist::class.java))
                }
            }
        return MutableLiveData(allBucketlists)
    }

    fun getAllBucketlistsOptions(): FirestoreRecyclerOptions<Bucketlist> {
        val query = bucketlistsCollRef
            .whereEqualTo("createdBy", FirebaseAuth.getInstance().currentUser!!.uid)

        return FirestoreRecyclerOptions.Builder<Bucketlist>()
            .setQuery(query, Bucketlist::class.java)
            .build()
    }
}