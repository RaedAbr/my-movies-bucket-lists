package mse.mobop.mymoviesbucketlists.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.OffsetDateTime

data class Bucketlist (
    var id: String? = null,
    var name: String,
    var createdBy: String,
    var creationDateTime: String? = null
): ModelInterface(id) {
    constructor(): this(null, "", "", null)
}