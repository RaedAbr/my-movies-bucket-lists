package mse.mobop.ui.bucketlist

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import mse.mobop.model.MoviesBucketlist

class AddEditBucketlistFragmentViewModel: ViewModel() {

    companion object {
        private const val BOOK_ARGUMENT = "moviesBucketlist"

        fun createArguments(moviesBucketlist: MoviesBucketlist): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(BOOK_ARGUMENT, moviesBucketlist)
            bundle.putString(ARG_TITLE, "Edit list")

            return bundle
        }
    }

    val moviesBucketlist: MutableLiveData<MoviesBucketlist> = MutableLiveData()

    fun loadArguments(arguments: Bundle?) {
        if (arguments == null) {
            return
        }

        val book: MoviesBucketlist? = arguments.get(BOOK_ARGUMENT) as MoviesBucketlist?
        this.moviesBucketlist.postValue(book)
    }
}