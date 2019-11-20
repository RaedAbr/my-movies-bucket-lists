package mse.mobop.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MoviesBucketlistDao {
    @Insert
    fun insert(moviesBucketlist: MoviesBucketlist)
    @Update
    fun update(moviesBucketlist: MoviesBucketlist)
    @Delete
    fun delete(moviesBucketlist: MoviesBucketlist)
    @Query("select * from MoviesBucketlist order by creationDate, creationTime desc")
    fun getAllMoviesBucketlists(): LiveData<List<MoviesBucketlist>>
}