package mse.mobop.mymoviesbucketlists.database

import android.app.Application
import androidx.lifecycle.LiveData
import mse.mobop.mymoviesbucketlists.model.Bucketlist
import mse.mobop.mymoviesbucketlists.model.BucketlistDao
import org.jetbrains.anko.doAsync

class BucketlistRepository(application: Application) {
    private var bucketlistDao: BucketlistDao
    var allBucketlist: LiveData<List<Bucketlist>>

    init {
        val database = MoviesBucketlistDatabase.getInstance(application)
        bucketlistDao = database.moviesBucketlistDao()
        allBucketlist = bucketlistDao.getAllBucketlists()
    }

    fun insert(bucketlist: Bucketlist) {
        doAsync {
            bucketlistDao.insert(bucketlist)
        }
    }

    fun update(bucketlist: Bucketlist) {
        doAsync {
            bucketlistDao.update(bucketlist)
        }
    }

    fun delete(bucketlist: Bucketlist) {
        doAsync {
            bucketlistDao.delete(bucketlist)
        }
    }
}