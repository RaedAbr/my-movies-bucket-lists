package mse.mobop.mymoviesbucketlists.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class User (
    @PrimaryKey
    var username: String,
    var password: String
): Serializable {
    override fun equals(other: Any?): Boolean {
        val o = other as User
        return this.username == o.username && this.password == o.password
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + password.hashCode()
        return result
    }
}