package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
//import mse.mobop.mymoviesbucketlists.ARG_BUCKETLIST_OBJECT
import mse.mobop.mymoviesbucketlists.database.BucketlistRepository
import mse.mobop.mymoviesbucketlists.model.Bucketlist

class BucketlistViewModel(application: Application): AndroidViewModel(application) {

    private val bucketlistRepository: BucketlistRepository = BucketlistRepository(application)

    lateinit var bucketlist: LiveData<Bucketlist>
    val allBucketlist: LiveData<List<Bucketlist>> = bucketlistRepository.allBucketlist

    fun loadBucketlist(id: Long) {
        bucketlist = bucketlistRepository.selectById(id)
    }

    fun insert(bucketlist: Bucketlist) = bucketlistRepository.insert(bucketlist)
    fun update(bucketlist: Bucketlist) = bucketlistRepository.update(bucketlist)
    fun delete(bucketlist: Bucketlist) = bucketlistRepository.delete(bucketlist)
}