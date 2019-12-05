package mse.mobop.mymoviesbucketlists.model

import androidx.lifecycle.LiveData
import androidx.room.*
import java.time.OffsetDateTime

@Dao
abstract class BucketlistDao {
    @Insert
    abstract fun insertPrep(bucketlist: Bucketlist_for_room)
    fun insert(bucketlist: Bucketlist_for_room) {
        insertPrep(bucketlist.apply {
            creationDateTime = OffsetDateTime.now()
        })
    }

    @Update
    abstract fun update(bucketlist: Bucketlist_for_room)

    @Delete
    abstract fun delete(bucketlist: Bucketlist_for_room)

    @Query("select * from Bucketlist_for_room where id = :id LIMIT 1")
    abstract fun selectById(id: Long): LiveData<Bucketlist_for_room>

    @Query("select * from Bucketlist_for_room where createdBy like :currentUsername order by datetime(creationDateTime) desc")
    abstract fun getAllBucketlists(currentUsername: String): LiveData<List<Bucketlist_for_room>>
}