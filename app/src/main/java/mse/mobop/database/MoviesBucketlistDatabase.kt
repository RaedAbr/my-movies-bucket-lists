package mse.mobop.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import mse.mobop.model.MoviesBucketlist
import mse.mobop.model.MoviesBucketlistDao
import org.jetbrains.anko.doAsync

@Database(entities = [MoviesBucketlist::class], version = 1, exportSchema = false)
abstract class MoviesBucketlistDatabase: RoomDatabase() {

    abstract fun moviesBucketlistDao(): MoviesBucketlistDao

    companion object {
        var DB_NAME = "MoviesBucketlist.db"
        private var INSTANCE: MoviesBucketlistDatabase? = null

        fun getInstance(context: Context): MoviesBucketlistDatabase {
            if(INSTANCE == null)
                INSTANCE = create(context)
            return INSTANCE!!
        }

        private fun create(context: Context) =
            Room.databaseBuilder(context, MoviesBucketlistDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .addCallback(roomCallback)
                .build()

        private val roomCallback = object: Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                doAsync {
                    val moviesBucketlistDao = INSTANCE!!.moviesBucketlistDao()
                    moviesBucketlistDao.insert(MoviesBucketlist(
                        1,
                        "OneList 1",
                        "Me",
                        "November 18, 2019",
                        "04:30PM"
                    ))
                    moviesBucketlistDao.insert(MoviesBucketlist(
                        2,
                        "OneList 2",
                        "Alice",
                        "November 18, 2019",
                        "04:30PM"
                    ))
                    moviesBucketlistDao.insert(MoviesBucketlist(
                        3,
                        "OneList 3",
                        "Bob",
                        "November 18, 2019",
                        "04:30PM"
                    ))
                }
            }
        }
    }
}