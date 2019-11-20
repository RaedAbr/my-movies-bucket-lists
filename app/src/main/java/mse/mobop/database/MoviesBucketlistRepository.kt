package mse.mobop.database

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import mse.mobop.model.MoviesBucketlist
import mse.mobop.model.MoviesBucketlistDao
import org.jetbrains.anko.doAsync

class MoviesBucketlistRepository(application: Application) {
    private var moviesBucketlistDao: MoviesBucketlistDao
    var allMoviesBucketlist: LiveData<List<MoviesBucketlist>>

    init {
        Log.e("context", application.toString())
        val database = MoviesBucketlistDatabase.getInstance(application)
        moviesBucketlistDao = database.moviesBucketlistDao()
        allMoviesBucketlist = moviesBucketlistDao.getAllMoviesBucketlists()
    }

    fun insert(moviesBucketlist: MoviesBucketlist) {
        doAsync {
            moviesBucketlistDao.insert(moviesBucketlist)
        }
    }

    fun update(moviesBucketlist: MoviesBucketlist) {
        doAsync {
            moviesBucketlistDao.update(moviesBucketlist)
        }
    }

    fun delete(moviesBucketlist: MoviesBucketlist) {
        doAsync {
            moviesBucketlistDao.delete(moviesBucketlist)
        }
    }
}