package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist

import androidx.lifecycle.LiveData
//import mse.mobop.mymoviesbucketlists.ARG_BUCKETLIST_OBJECT
import mse.mobop.mymoviesbucketlists.firestore.BucketlistFirestore
import mse.mobop.mymoviesbucketlists.model.Bucketlist
import mse.mobop.mymoviesbucketlists.model.Movie

class BucketlistViewModel(id: String? = null) {

    var bucketlist: LiveData<Bucketlist> = BucketlistFirestore.getById(id)

    fun insert(bucketlist: Bucketlist) = BucketlistFirestore.createBucketlist(bucketlist)
    fun update(bucketlist: Bucketlist) = BucketlistFirestore.updateBucketlist(bucketlist)
    fun addMoviesToBucketlist(bucketlist: Bucketlist, movies: ArrayList<Movie>) =
        BucketlistFirestore.updateMoviesList(bucketlist.id!!, movies)

    fun stopSnapshotListener() {
        if (bucketlist.value != null) {
            BucketlistFirestore.stopListener(bucketlist.value!!.id!!)
        }
    }

    fun delete(bucketlistId: String) {
        BucketlistFirestore.deleteBucketlist(bucketlistId)
    }
}