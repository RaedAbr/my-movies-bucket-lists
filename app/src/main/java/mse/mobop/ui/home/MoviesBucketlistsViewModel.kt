package mse.mobop.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mse.mobop.database.MoviesBucketlistRepository
import mse.mobop.model.MoviesBucketlist

class MoviesBucketlistsViewModel(application: Application) : AndroidViewModel(application) {
    private val moviesBucketlistRepository: MoviesBucketlistRepository = MoviesBucketlistRepository(application)
    val allMoviesBucketlist: LiveData<List<MoviesBucketlist>> = moviesBucketlistRepository.allMoviesBucketlist

//    private val _allMoviesBucketlist = MutableLiveData<List<MoviesBucketlist>>().apply {
//        value = emptyList()
//    }
//    val allMoviesBucketlist: LiveData<List<MoviesBucketlist>> = _allMoviesBucketlist
}