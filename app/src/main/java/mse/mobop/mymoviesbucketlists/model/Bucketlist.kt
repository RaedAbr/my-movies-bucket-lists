package mse.mobop.mymoviesbucketlists.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.OffsetDateTime

@Entity
data class Bucketlist (
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var name: String,
    var createdBy: String,
    var creationDateTime: OffsetDateTime? = null
//    var sharedWith: ArrayList<String>? = null,
//    var moviesList: ArrayList<String>? = null
) : ModelInterface(id) {
    
    override fun equals(other: Any?): Boolean {
        val o = other as Bucketlist
        return this.id == o.id &&
                this.name == o.name &&
                this.createdBy == o.createdBy &&
                this.creationDateTime == o.creationDateTime
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + createdBy.hashCode()
        result = 31 * result + creationDateTime.hashCode()
        return result
    }
}