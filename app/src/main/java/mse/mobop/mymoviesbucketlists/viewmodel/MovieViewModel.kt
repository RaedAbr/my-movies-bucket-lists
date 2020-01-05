package mse.mobop.mymoviesbucketlists.viewmodel

import androidx.lifecycle.MutableLiveData
import mse.mobop.mymoviesbucketlists.model.Movie
import mse.mobop.mymoviesbucketlists.model.MoviesSearchResult
import mse.mobop.mymoviesbucketlists.tmdapi.MovieApi
import mse.mobop.mymoviesbucketlists.tmdapi.MovieService
import retrofit2.Call

class MovieViewModel {
    companion object {
        const val POPULAR = 0
        const val UPCOMING = 1
        const val TOP_RATED = 2
        const val SEARCH = 3
        const val PAGE_START = 1
        private val movieService = MovieApi.client!!.create(MovieService::class.java)
    }

    var currentPage =
        PAGE_START
    var query: String = ""
    var apiCall = ::callGetPopularMoviesApi
    fun setApiCall(type: Int) {
        apiCall = when(type) {
            POPULAR -> ::callGetPopularMoviesApi
            UPCOMING -> ::callGetUpcomingMoviesApi
            TOP_RATED -> ::callGetTopRatedMoviesApi
            else -> ::callSearchMoviesApi
        }
    }

    var movies: MutableLiveData<ArrayList<Movie>> = MutableLiveData(ArrayList())

    var moviesAlreadyAdded: ArrayList<Movie> = ArrayList()
    var selectedMovies: ArrayList<Movie> = ArrayList()

    private fun callGetPopularMoviesApi(): Call<MoviesSearchResult?>? {
        return movieService.getPopularMovies(currentPage)
    }

    private fun callGetTopRatedMoviesApi(): Call<MoviesSearchResult?>? {
        return movieService.getTopRatedMovies(currentPage)
    }

    private fun callGetUpcomingMoviesApi(): Call<MoviesSearchResult?>? {
        return movieService.getUpcomingMovies(currentPage)
    }

    private fun callSearchMoviesApi(): Call<MoviesSearchResult?>? {
        return movieService.searchForMovies(query, currentPage)
    }

    fun addLoadingFooter() {
        movies.value!!.add(Movie(isLoadingItem = true))
        movies.notifyObserver()
    }

    fun removeLoadingFooter() {
        movies.value!!.removeIf { it.isLoadingItem }
        movies.notifyObserver()
    }

    fun addAll(list: ArrayList<Movie>) {
        var filteredList = list
        moviesAlreadyAdded.forEach { existedMovie ->
            filteredList = filteredList.filter { it.id != existedMovie.id} as ArrayList
        }
        filteredList = filteredList.map {
            if (selectedMovies.find { m -> it.id == m.id } != null) {
                it.isSelected = true
            }
            it
        } as ArrayList
        movies.value!!.addAll(filteredList)
        movies.notifyObserver()
    }

    fun clear() {
        movies.value!!.clear()
        movies.notifyObserver()
    }

    fun movieSelected(position: Int) {
        movies.value!![position].isSelected = !movies.value!![position].isSelected
        movies.notifyObserver()
        if (movies.value!![position].isSelected) {
            selectedMovies.add(movies.value!![position])
        } else {
            selectedMovies.remove(movies.value!![position])
        }
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}