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
    abstract fun update(bucketlist: Bucketlist)

    @Delete
    abstract fun delete(bucketlist: Bucketlist)

    @Query("select * from Bucketlist order by datetime(creationDateTime) desc")
    abstract fun getAllBucketlists(): LiveData<List<Bucketlist>>
}