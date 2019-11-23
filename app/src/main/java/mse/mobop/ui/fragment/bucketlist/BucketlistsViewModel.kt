package mse.mobop.ui.fragment.bucketlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import mse.mobop.database.BucketlistRepository
import mse.mobop.model.Bucketlist

class BucketlistsViewModel(application: Application): AndroidViewModel(application) {

    private val bucketlistRepository: BucketlistRepository = BucketlistRepository(application)

    val allBucketlist: LiveData<List<Bucketlist>> = bucketlistRepository.allBucketlist

    fun getBucketlistAt(position: Int): Bucketlist = allBucketlist.value!![position]

    fun deleteAt(index: Int) = bucketlistRepository.delete(allBucketlist.value!![index])

}