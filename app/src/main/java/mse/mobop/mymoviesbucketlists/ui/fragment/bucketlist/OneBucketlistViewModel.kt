package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mse.mobop.mymoviesbucketlists.ARG_BUCKETLIST_OBJECT
import mse.mobop.mymoviesbucketlists.database.BucketlistRepository
import mse.mobop.mymoviesbucketlists.model.Bucketlist

class OneBucketlistViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        fun createArguments(bucketlist: Bucketlist): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(ARG_BUCKETLIST_OBJECT, bucketlist)
            return bundle
        }
    }

    private val _bucketlist: MutableLiveData<Bucketlist> = MutableLiveData()
    val bucketlist: LiveData<Bucketlist>
        get() = _bucketlist

    fun loadObjectFromArguments(arguments: Bundle?) {
        if (arguments == null) {
            return
        }
        val book: Bucketlist? = arguments.get(ARG_BUCKETLIST_OBJECT) as Bucketlist?
        _bucketlist.value = book
    }


    private val bucketlistRepository: BucketlistRepository = BucketlistRepository(application)
}