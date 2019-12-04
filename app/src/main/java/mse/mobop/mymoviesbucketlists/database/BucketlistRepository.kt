package mse.mobop.mymoviesbucketlists.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import mse.mobop.mymoviesbucketlists.model.Bucketlist
import mse.mobop.mymoviesbucketlists.model.BucketlistDao
import org.jetbrains.anko.doAsync

class BucketlistRepository(application: Application) {
    private var bucketlistDao: BucketlistDao
    var allBucketlist: LiveData<List<Bucketlist>>

    init {
        val database = MoviesBucketlistDatabase.getInstance(application)
        bucketlistDao = database.moviesBucketlistDao()
        allBucketlist = bucketlistDao.getAllBucketlists(FirebaseAuth.getInstance().currentUser!!.displayName!!)
    }

    fun selectById(id: Long): LiveData<Bucketlist> = bucketlistDao.selectById(id)

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