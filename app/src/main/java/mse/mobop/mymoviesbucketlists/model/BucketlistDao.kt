package mse.mobop.mymoviesbucketlists.model

import androidx.lifecycle.LiveData
import androidx.room.*
import java.time.OffsetDateTime

@Dao
abstract class BucketlistDao {
    @Insert
    abstract fun insertPrep(bucketlist: Bucketlist)
    fun insert(bucketlist: Bucketlist) {
        insertPrep(bucketlist.apply {
            creationDateTime = OffsetDateTime.now()
        })
    }

    @Update
    abstract fun updatePrep(bucketlist: Bucketlist)
    fun update(bucketlist: Bucketlist) {
        updatePrep(bucketlist.apply {
            creationDateTime = OffsetDateTime.now()
        })
    }

    @Delete
    abstract fun delete(bucketlist: Bucketlist)

//    @Query("select * from Bucketlist order by creationDate, creationTime desc")
    @Query("select * from Bucketlist order by datetime(creationDateTime) desc")
    abstract fun getAllMoviesBucketlists(): LiveData<List<Bucketlist>>
}