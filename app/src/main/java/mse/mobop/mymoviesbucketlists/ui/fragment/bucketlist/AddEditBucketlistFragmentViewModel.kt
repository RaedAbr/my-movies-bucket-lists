package mse.mobop.mymoviesbucketlists.ui.fragment.bucketlist

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import mse.mobop.mymoviesbucketlists.ARG_ADD_EDIT_BUCKETLIST_FRAGMENT_ACTION
import mse.mobop.mymoviesbucketlists.ARG_ADD_EDIT_BUCKETLIST_FRAGMENT_TITLE
import mse.mobop.mymoviesbucketlists.ARG_ADD_EDIT_BUCKETLIST_OBJECT
import mse.mobop.mymoviesbucketlists.database.BucketlistRepository
import mse.mobop.mymoviesbucketlists.model.Bucketlist

class AddEditBucketlistFragmentViewModel(application: Application): AndroidViewModel(application) {
    enum class Action {
        ADD, EDIT
    }

    companion object {

        fun createArguments(bucketlist: Bucketlist): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(ARG_ADD_EDIT_BUCKETLIST_OBJECT, bucketlist)
            bundle.putString(ARG_ADD_EDIT_BUCKETLIST_FRAGMENT_TITLE, "Edit list")
            bundle.putSerializable(ARG_ADD_EDIT_BUCKETLIST_FRAGMENT_ACTION, Action.EDIT)

            return bundle
        }
    }

    val bucketlist: MutableLiveData<Bucketlist> = MutableLiveData()

    fun loadArguments(arguments: Bundle?) {
        if (arguments == null) {
            return
        }

        val book: Bucketlist? = arguments.get(ARG_ADD_EDIT_BUCKETLIST_OBJECT) as Bucketlist?
        this.bucketlist.postValue(book)
    }


    private val bucketlistRepository: BucketlistRepository = BucketlistRepository(application)

    fun insert(bucketlist: Bucketlist) = bucketlistRepository.insert(bucketlist)
    fun update(bucketlist: Bucketlist) = bucketlistRepository.update(bucketlist)
}