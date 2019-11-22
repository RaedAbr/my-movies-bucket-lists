package mse.mobop.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

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
) : Serializable