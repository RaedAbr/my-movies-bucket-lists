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

    @Query("select * from Bucketlist where id = :id LIMIT 1")
    abstract fun selectById(id: Long): LiveData<Bucketlist>

    @Query("select * from Bucketlist where createdBy like :currentUsername order by datetime(creationDateTime) desc")
    abstract fun getAllBucketlists(currentUsername: String): LiveData<List<Bucketlist>>
}