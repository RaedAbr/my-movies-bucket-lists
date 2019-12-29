package mse.mobop.mymoviesbucketlists.tmdapi

import mse.mobop.mymoviesbucketlists.BuildConfig
import mse.mobop.mymoviesbucketlists.model.MovieVideoResult
import mse.mobop.mymoviesbucketlists.model.MoviesSearchResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface MovieService {
    /**
     * Call search for movies with page number in query
     */
    @GET("search/movie")
    fun searchForMovies(
        @Query("query") query: String?,
        @Query("page") pageIndex: Int,
        @Query("api_key") apiKey: String = BuildConfig.THE_MOVIE_DATABASE_API_KEY,
        @Query("include_adult") includeAdpult: Boolean = false,
        @Query("language") language: String? = "en_US"
    ): Call<MoviesSearchResult?>?

    @GET("movie/popular")
    fun getPopularMovies(
        @Query("page") pageIndex: Int,
        @Query("api_key") apiKey: String = BuildConfig.THE_MOVIE_DATABASE_API_KEY,
        @Query("include_adult") includeAdpult: Boolean = false,
        @Query("language") language: String? = "en_US"
    ): Call<MoviesSearchResult?>?

    @GET("movie/upcoming")
    fun getUpcomingMovies(
        @Query("page") pageIndex: Int,
        @Query("api_key") apiKey: String = BuildConfig.THE_MOVIE_DATABASE_API_KEY,
        @Query("include_adult") includeAdpult: Boolean = false,
        @Query("language") language: String? = "en_US"
    ): Call<MoviesSearchResult?>?

    @GET("movie/top_rated")
    fun getTopRatedMovies(
        @Query("page") pageIndex: Int,
        @Query("api_key") apiKey: String = BuildConfig.THE_MOVIE_DATABASE_API_KEY,
        @Query("include_adult") includeAdpult: Boolean = false,
        @Query("language") language: String? = "en_US"
    ): Call<MoviesSearchResult?>?

    @GET("movie/{movie_id}/videos")
    fun getMovieTrailers(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = BuildConfig.THE_MOVIE_DATABASE_API_KEY,
        @Query("language") language: String? = "en_US"
    ): Call<MovieVideoResult?>?
}