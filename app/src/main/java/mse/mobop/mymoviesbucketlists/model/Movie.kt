package mse.mobop.mymoviesbucketlists.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("popularity") val popularity : Double? = 0.0,
    @SerializedName("vote_count") val voteCount : Int? = 0,
    @SerializedName("video") val video : Boolean? = false,
    @SerializedName("poster_path") val posterPath : String? = "",
    @SerializedName("id") val id : Int? = 0,
    @SerializedName("adult") val adult : Boolean? = false,
    @SerializedName("backdrop_path") val backdropPath : String? = "",
    @SerializedName("original_language") val originalLanguage : String? = "",
    @SerializedName("original_title") val originalTitle : String? = "",
    @SerializedName("genre_ids") val genreIds : List<Int>? = null,
    @SerializedName("title") var title : String? = "",
    @SerializedName("vote_average") val voteAverage : Double? = 0.0,
    @SerializedName("overview") val overview : String? = "",
    @SerializedName("release_date") val releaseDate : String? = "",
    var addedBy: User? = null,
    var addedTimestamp: Timestamp? = null,
    var isWatched: Boolean = false,
    var watchedTimestamp: Timestamp? = null,
    @get:Exclude var isSelected: Boolean = false,
    @get:Exclude var isExpanded: Boolean = false,
    @get:Exclude var isLoadingItem: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        other as Movie
        return popularity == other.popularity &&
                voteCount == other.voteCount &&
                video == other.video &&
                posterPath == other.posterPath &&
                id == other.id &&
                adult == other.adult &&
                backdropPath == other.backdropPath &&
                originalLanguage == other.originalLanguage &&
                originalTitle == other.originalTitle &&
                genreIds == other.genreIds &&
                title == other.title &&
                voteAverage == other.voteAverage &&
                overview == other.overview &&
                releaseDate == other.releaseDate &&
                compareUsers(addedBy, other.addedBy) &&
                compareTimestamps(addedTimestamp, other.addedTimestamp) &&
                isWatched == other.isWatched &&
                compareTimestamps(watchedTimestamp, other.watchedTimestamp) &&
                isSelected == other.isSelected &&
                isExpanded == other.isExpanded
    }

    private fun compareTimestamps(t1: Timestamp?, t2: Timestamp?): Boolean {
        if (t1 == null && t2 == null) return true
        if (t1 == null || t2 == null) return false
        return t1.seconds == t2.seconds
    }

    private fun compareUsers(u1: User?, u2: User?): Boolean {
        if (u1 == null && u2 == null) return true
        if (u1 == null || u2 == null) return false
        return u1 == u2
    }

    override fun hashCode(): Int {
        var result = popularity?.hashCode() ?: 0
        result = 31 * result + (voteCount ?: 0)
        result = 31 * result + (video?.hashCode() ?: 0)
        result = 31 * result + (posterPath?.hashCode() ?: 0)
        result = 31 * result + (id ?: 0)
        result = 31 * result + (adult?.hashCode() ?: 0)
        result = 31 * result + (backdropPath?.hashCode() ?: 0)
        result = 31 * result + (originalLanguage?.hashCode() ?: 0)
        result = 31 * result + (originalTitle?.hashCode() ?: 0)
        result = 31 * result + (genreIds?.hashCode() ?: 0)
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (voteAverage?.hashCode() ?: 0)
        result = 31 * result + (overview?.hashCode() ?: 0)
        result = 31 * result + (releaseDate?.hashCode() ?: 0)
        result = 31 * result + (addedBy?.hashCode() ?: 0)
        result = 31 * result + (addedTimestamp?.hashCode() ?: 0)
        result = 31 * result + isWatched.hashCode()
        result = 31 * result + (watchedTimestamp?.hashCode() ?: 0)
        result = 31 * result + isSelected.hashCode()
        result = 31 * result + isExpanded.hashCode()
        return result
    }
}