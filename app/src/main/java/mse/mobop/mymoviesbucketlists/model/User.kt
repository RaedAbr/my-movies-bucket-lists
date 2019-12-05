package mse.mobop.mymoviesbucketlists.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class User (var id: String, var name: String) {
    constructor(): this("", "")
}