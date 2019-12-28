package mse.mobop.mymoviesbucketlists.firestore

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import mse.mobop.mymoviesbucketlists.utils.BUCKETLIST_COLLECTION
import mse.mobop.mymoviesbucketlists.model.Bucketlist
import mse.mobop.mymoviesbucketlists.model.Movie
import java.util.*
import kotlin.collections.ArrayList

object BucketlistFirestore {
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val bucketlistsCollRef: CollectionReference
        get() = firestoreInstance.collection(BUCKETLIST_COLLECTION)
    private val snapshotListenersMap = mutableMapOf<String, ListenerRegistration>()

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
            .orderBy("creationTimestamp", Query.Direction.DESCENDING)
    }

    fun getSharedBucketlistsQuery(): Query {
        return bucketlistsCollRef
            .whereArrayContains("sharedWithIds", FirebaseAuth.getInstance().currentUser!!.uid)
            .orderBy("creationTimestamp", Query.Direction.DESCENDING)
    }

    fun getById(id: String?): LiveData<Bucketlist> {
        val bucketlist = MutableLiveData<Bucketlist>()
//        doAsync {
        if (id != null) {
            val registration = bucketlistsCollRef.document(id)
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
            snapshotListenersMap.putIfAbsent(id, registration)
//        }
        }
        return bucketlist
    }

    fun updateBucketlist(bucketlist: Bucketlist) {
        bucketlistsCollRef.document(bucketlist.id!!)
            .set(bucketlist)
            .addOnSuccessListener {
                Log.e("bucketlist",  bucketlist.id + " Updated successfully")
            }
            .addOnFailureListener {
                Log.e("bucketlist",  bucketlist.id + " Error writing document")
            }
    }

    fun updateMoviesList(bucketlistId: String, movies: ArrayList<Movie>) {
        val array = arrayOfNulls<Movie>(movies.size)
        movies.toArray(array)

        bucketlistsCollRef.document(bucketlistId)
            .update("movies", FieldValue.arrayUnion(*array))
            .addOnSuccessListener { Log.e("updateMoviesList", "Movies successfully added!") }
            .addOnFailureListener { e -> Log.e("updateMoviesList", "Error adding movies", e) }
    }

    fun deleteBucketlist(bucketlistId: String) {
        bucketlistsCollRef.document(bucketlistId).delete()
    }

    fun toggleIsMovieWatched(bucketlistId: String, movie: Movie) {
        bucketlistsCollRef.document(bucketlistId)
            .update("movies", FieldValue.arrayRemove(movie))
        movie.apply {
            isWatched = !isWatched
            watchedTimestamp = if (isWatched) Timestamp(Date().time / 1000, 0) else null
        }
        bucketlistsCollRef.document(bucketlistId)
            .update("movies", FieldValue.arrayUnion(movie))
    }

    fun deleteBucketlistMovie(bucketlistId: String, movie: Movie) {
        bucketlistsCollRef.document(bucketlistId)
            .update("movies", FieldValue.arrayRemove(movie))
    }

    fun stopListener(bucketlistId: String) {
        snapshotListenersMap.getValue(bucketlistId).remove()
    }
}