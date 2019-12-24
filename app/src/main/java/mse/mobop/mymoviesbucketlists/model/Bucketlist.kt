package mse.mobop.mymoviesbucketlists.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Bucketlist (
    @DocumentId var id: String? = null,
    var name: String = "",
    var createdBy: User? = null,
    @ServerTimestamp var creationTimestamp: Timestamp? = null,
    var sharedWith: List<User> = ArrayList(),
    var sharedWithIds: List<String> = ArrayList(),
    var movies: List<Movie> = ArrayList()
)