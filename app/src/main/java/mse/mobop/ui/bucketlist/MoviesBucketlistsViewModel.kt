package mse.mobop.ui.bucketlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import mse.mobop.database.MoviesBucketlistRepository
import mse.mobop.model.MoviesBucketlist

class MoviesBucketlistsViewModel(application: Application) : AndroidViewModel(application) {
    private val moviesBucketlistRepository: MoviesBucketlistRepository = MoviesBucketlistRepository(application)
    val allMoviesBucketlist: LiveData<List<MoviesBucketlist>> = moviesBucketlistRepository.allMoviesBucketlist
}