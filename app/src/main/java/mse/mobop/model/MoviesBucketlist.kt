package mse.mobop.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MoviesBucketlist (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var createdBy: String,
    var creationDate: String,
    var creationTime: String
//    var sharedWith: ArrayList<String>? = null,
//    var moviesList: ArrayList<String>? = null
) {
//    companion object {
//        val listForTest = ArrayList<MoviesBucketlist>()
//
//        init {
//            listForTest.add(
//                MoviesBucketlist(
//                    1,
//                    "OneList 1",
//                    "Me",
//                    "November 18, 2019",
//                    "04:30PM"
//                )
//            )
//            listForTest.add(
//                MoviesBucketlist(
//                    2,
//                    "OneList 2",
//                    "Bob",
//                    "September 20, 2019",
//                    "00:15AM"
//                )
//            )
//            listForTest.add(
//                MoviesBucketlist(
//                    3,
//                    "OneList 3",
//                    "Alice",
//                    "June 2, 2019",
//                    "10:54AM"
//                )
//            )
//        }
//    }
}