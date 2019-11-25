package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mse.mobop.mymoviesbucketlists.ARG_ADD_EDIT_BUCKETLIST_FRAGMENT_ACTION
import mse.mobop.mymoviesbucketlists.ARG_BUCKETLIST_OBJECT
import mse.mobop.mymoviesbucketlists.BucketlistAction
import mse.mobop.mymoviesbucketlists.database.BucketlistRepository
import mse.mobop.mymoviesbucketlists.model.Bucketlist

class AddEditBucketlistViewModel(application: Application): AndroidViewModel(application) {
    companion object {
        fun createArguments(bucketlist: Bucketlist?, action: BucketlistAction): Bundle {
            val bundle = Bundle()
            if (action == BucketlistAction.EDIT) {
                bundle.putSerializable(ARG_BUCKETLIST_OBJECT, bucketlist)
            }
            bundle.putSerializable(ARG_ADD_EDIT_BUCKETLIST_FRAGMENT_ACTION, action)
            return bundle
        }
    }

    private val _bucketlist: MutableLiveData<Bucketlist> = MutableLiveData()
    val bucketlist: LiveData<Bucketlist>
        get() = _bucketlist

    fun loadArguments(arguments: Bundle?) {
        if (arguments == null) {
            return
        }
        val book: Bucketlist? = arguments.get(ARG_BUCKETLIST_OBJECT) as Bucketlist?
        _bucketlist.value = book
    }


    private val bucketlistRepository: BucketlistRepository = BucketlistRepository(application)

    fun insert(bucketlist: Bucketlist) = bucketlistRepository.insert(bucketlist)
    fun update(bucketlist: Bucketlist) = bucketlistRepository.update(bucketlist)
}