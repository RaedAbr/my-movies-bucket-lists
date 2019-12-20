package mse.mobop.mymoviesbucketlists.ui.fragment.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import mse.mobop.mymoviesbucketlists.model.Movie
import org.jetbrains.anko.doAsync

class MovieViewModel : ViewModel() {

    val moviesList: MutableLiveData<List<Movie>> = MutableLiveData()

    fun updateMovisList(query: String) {
//        doAsync {
//            moviesList.value = TMDApi.searchForMovies(query)
//        }
    }
}