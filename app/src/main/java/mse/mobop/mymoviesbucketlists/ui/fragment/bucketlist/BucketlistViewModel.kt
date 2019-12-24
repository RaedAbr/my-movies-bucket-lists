package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist

import androidx.lifecycle.LiveData
//import mse.mobop.mymoviesbucketlists.ARG_BUCKETLIST_OBJECT
import mse.mobop.mymoviesbucketlists.firestore.BucketlistFirestore
import mse.mobop.mymoviesbucketlists.model.Bucketlist
import mse.mobop.mymoviesbucketlists.model.Movie

class BucketlistViewModel(id: String? = null) {

    var bucketlist: LiveData<Bucketlist> = BucketlistFirestore.getById(id)
//    val allBucketlist: LiveData<List<Bucketlist>>
//        get() = bucketlistRepository.allBucketlist
//
//    fun loadBucketlist(id: String) {
//        bucketlist = BucketlistFirestore.getById(id)
//    }

    fun insert(bucketlist: Bucketlist) = BucketlistFirestore.createBucketlist(bucketlist)
    fun update(bucketlist: Bucketlist) = BucketlistFirestore.updateBucketlist(bucketlist)
    fun addMoviesToBucketlist(bucketlistId: String, movies: ArrayList<Movie>) =
        BucketlistFirestore.updateMoviesList(bucketlistId, movies)

    fun stopSnapshotListener() {
        if (bucketlist.value != null) {
            BucketlistFirestore.stopListener(bucketlist.value!!.id!!)
        }
    }
}