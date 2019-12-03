package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import mse.mobop.mymoviesbucketlists.ARG_BUCKETLIST_OBJECT
import mse.mobop.mymoviesbucketlists.database.BucketlistRepository
import mse.mobop.mymoviesbucketlists.model.Bucketlist

class BucketlistsViewModel(application: Application): AndroidViewModel(application) {

    private val bucketlistRepository: BucketlistRepository = BucketlistRepository(application)

    val allBucketlist: LiveData<List<Bucketlist>> = bucketlistRepository.allBucketlist

    fun delete(bucketlist: Bucketlist) = bucketlistRepository.delete(bucketlist)
}