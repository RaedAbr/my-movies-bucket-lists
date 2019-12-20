package mse.mobop.mymoviesbucketlists.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import kotlinx.android.synthetic.main.item_list_header.view.*
import kotlinx.android.synthetic.main.item_list_movie.view.*
import mse.mobop.mymoviesbucketlists.model.Movie
import mse.mobop.mymoviesbucketlists.R


class MoviesPaginationAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var movieResults: ArrayList<Movie> = ArrayList()

    private var isLoadingAdded = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(parent.context)
        viewHolder = when (viewType) {
            ITEM -> MovieVH(inflater.inflate(R.layout.item_list_movie, parent, false))
            HEADER -> HeaderVH(inflater.inflate(R.layout.item_list_header, parent, false))
            // else it's a loading bar
            else -> LoadingVH(inflater.inflate(R.layout.item_progress, parent, false))
        }
        return viewHolder
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie: Movie = movieResults[position]
        when (getItemViewType(position)) {
            ITEM -> {
                val movieVH = holder as MovieVH
                movieVH.mMovieTitle.text = movie.title
                if (movie.releaseDate!!.length > 4)
                    movieVH.mYear.text = (movie.releaseDate.substring(0, 4) // we want the year only
                    + " | " + movie.originalLanguage!!.toUpperCase())
                movieVH.mMovieDesc.text = movie.overview!!

                Glide
                    .with(context)
                    .load(BASE_URL_IMG + movie.posterPath)
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean { // image ready, hide progress now
                            movieVH.mProgress.visibility = View.GONE
                            Log.e("onResourceReady", model.toString())
                            return false // return false if you want Glide to handle everything else.
                        }
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean { // TODO: handle failure
                            movieVH.mProgress.visibility = View.GONE
                            movieVH.mNoPoster.visibility = View.VISIBLE
                            Log.e("onLoadFailed", model.toString())
                            Log.e("onLoadFailed", "id: ${movie.id}\ttitle: ${movie.title}")
                            return false
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // cache both original & resized image
                    .centerCrop()
                    .transition(withCrossFade())
                    .into(movieVH.mPosterImg)
            }
            HEADER -> {
                val movieVH = holder as HeaderVH
                movieVH.mHeader.text = movie.title
            }
            LOADING -> { }
        }
    }

    override fun getItemCount(): Int {
        return movieResults.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == movieResults.size - 1 && isLoadingAdded)
            LOADING
        else if (position == 0)
            HEADER
        else
            ITEM
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */
    fun add(mc: Movie) {
        movieResults.add(mc)
//        notifyItemInserted(movieResults.size - 1)
        notifyDataSetChanged()
    }

    fun addAll(mcList: List<Movie>) {
//        for (mc in mcList) {
//            add(mc)
//        }
        movieResults.addAll(mcList)
        notifyDataSetChanged()
    }

//    private fun remove(city: Movie?) {
//        val position = movieResults.indexOf(city)
//        if (position > -1) {
//            movieResults.removeAt(position)
////            notifyItemRemoved(position)
//            notifyDataSetChanged()
//        }
//    }

    fun clear() {
        isLoadingAdded = false
        movieResults.clear()
        notifyDataSetChanged()
//        while (itemCount > 0) {
//            remove(getItem(0))
//        }
    }

    val isEmpty: Boolean
        get() = itemCount == 0

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(Movie())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position = movieResults.size - 1
//        val item: Movie = getItem(position)
        movieResults.removeAt(position)
//        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

//    private fun getItem(position: Int): Movie {
//        return movieResults[position]
//    }
    /*
   View Holders
   _________________________________________________________________________________________________
    */
    /**
     * Main list's content ViewHolder
     */
    private class MovieVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        internal val mMovieTitle: TextView = itemView.movie_title
        internal val mMovieDesc: TextView = itemView.movie_desc
        internal val mYear: TextView = itemView.movie_year // displays "year | language"
        internal val mPosterImg: ImageView = itemView.movie_poster
        internal val mProgress: ProgressBar = itemView.movie_progress
        internal val mNoPoster: TextView = itemView.movie_no_poster
    }
    /**
     * Main list's content ViewHolder
     */
    private class HeaderVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        internal val mHeader: TextView = itemView.movie_header
    }

    private inner class LoadingVH(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!)

    companion object {
        private const val HEADER = 0
        private const val ITEM = 1
        private const val LOADING = 2
        private const val BASE_URL_IMG = "https://image.tmdb.org/t/p/w154"
    }
}