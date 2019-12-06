package mse.mobop.mymoviesbucketlists.firestore

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import mse.mobop.mymoviesbucketlists.utils.BUCKETLIST_COLLECTION
import mse.mobop.mymoviesbucketlists.model.Bucketlist
import org.jetbrains.anko.doAsync

object BucketlistFirestore {
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val bucketlistsCollRef: CollectionReference
        get() = firestoreInstance.collection(BUCKETLIST_COLLECTION)

    fun createBucketlist(bucketlist: Bucketlist) {
        bucketlistsCollRef.add(bucketlist)
            .addOnSuccessListener {
                Log.e("bucketlist", "DocumentSnapshot written with ID: ${it.id}")
            }
            .addOnFailureListener { e -> Log.e("bucketlist", "Error adding document", e) }
    }

//    fun getAllBucketlists(): LiveData<List<Bucketlist>> {
//        val allBucketlists = mutableListOf<Bucketlist>()
//        bucketlistsCollRef.whereEqualTo("createdBy.id", FirebaseAuth.getInstance().currentUser!!.uid)
//            .addSnapshotListener { documents, e ->
//                if (e != null) {
//                    Log.w("Bucketlist", "Listen failed.", e)
//                    return@addSnapshotListener
//                }
//                Log.e("bucketlist", "DocumentSnapshot written with ID: ${documents!!.count()}")
//                documents.forEach {
//                    allBucketlists.add(it.toObject(Bucketlist::class.java))
//                }
//            }
//        return MutableLiveData(allBucketlists)
//    }

    fun getOwnedBucketlistsQuery(): Query {
        return bucketlistsCollRef
            .whereEqualTo("createdBy.id", FirebaseAuth.getInstance().currentUser!!.uid)
            .orderBy("creationTimestamp")
    }

    fun getById(id: String?): LiveData<Bucketlist> {
        val bucketlist = MutableLiveData<Bucketlist>()
//        doAsync {
        if (id != null) {
            bucketlistsCollRef.document(id)
//                .get().result?.toObject(Bucketlist::class.java)

                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("getById", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.e("getById", "Current data: ${snapshot.data}")
                        bucketlist.value = snapshot.toObject(Bucketlist::class.java)
                    } else {
                        // data has been deleted by another user
                        Log.e("getById", "Current data: null")
                        bucketlist.value = null
                    }
                }
//        }
        }
        return bucketlist
    }

    fun updateBucketlist(bucketlist: Bucketlist) {
        bucketlistsCollRef.document(bucketlist.id!!)
            .set(bucketlist).addOnSuccessListener {
                Log.e("bucketlist",  bucketlist.id + " Updated successfully")
            }
            .addOnFailureListener {
                Log.e("bucketlist",  bucketlist.id + " Error writing document")
            }
    }
}