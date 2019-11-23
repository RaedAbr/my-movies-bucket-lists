package mse.mobop.database

import android.app.Application
import androidx.lifecycle.LiveData
import mse.mobop.model.Bucketlist
import mse.mobop.model.BucketlistDao
import org.jetbrains.anko.doAsync

class BucketlistRepository(application: Application) {
    private var bucketlistDao: BucketlistDao
    var allBucketlist: LiveData<List<Bucketlist>>

    init {
        val database = MoviesBucketlistDatabase.getInstance(application)
        bucketlistDao = database.moviesBucketlistDao()
        allBucketlist = bucketlistDao.getAllMoviesBucketlists()
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