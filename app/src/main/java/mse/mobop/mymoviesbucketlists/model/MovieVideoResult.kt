package mse.mobop.mymoviesbucketlists.model

import com.google.gson.annotations.SerializedName

data class MovieVideoResult(
    @SerializedName("id") val id : Int?,
    @SerializedName("results") val results : List<Video>?
)