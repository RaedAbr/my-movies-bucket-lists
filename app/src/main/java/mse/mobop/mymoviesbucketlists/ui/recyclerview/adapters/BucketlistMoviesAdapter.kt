package mse.mobop.mymoviesbucketlists.ui.recyclerview.adapters

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.dialog_movie_details.view.*
import kotlinx.android.synthetic.main.dialog_movie_details.view.movie_progress
import kotlinx.android.synthetic.main.dialog_movie_details.view.movie_title
import kotlinx.android.synthetic.main.item_bucketlist_movie.view.*
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.model.Movie
import mse.mobop.mymoviesbucketlists.ui.alrertdialog.DisplayMovieTrailerAlertDialog
import mse.mobop.mymoviesbucketlists.utils.BASE_URL_IMG
import mse.mobop.mymoviesbucketlists.utils.BASE_URL_IMG_BACKDROP
import mse.mobop.mymoviesbucketlists.utils.dateConverter

@SuppressLint("DefaultLocale", "InflateParams", "SetTextI18n")
class BucketlistMoviesAdapter:
    ListAdapter<Movie, RecyclerView.ViewHolder?>(object: DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}) {

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    fun setMoviesList(movies: List<Movie>) {
        val (watchedMovies, notWatchedMovies) = movies.partition { it.isWatched }
        val watchedMoviesList: ArrayList<Movie> = ArrayList(
            watchedMovies.sortedWith(
                compareByDescending<Movie> { it.watchedTimestamp }.thenBy { it.title }
            )
        )
        val notWatchedMoviesList: ArrayList<Movie> = ArrayList(
            notWatchedMovies.sortedWith(
                compareByDescending<Movie>{ it.addedTimestamp }.thenBy { it.title }
            )
        )

        if (watchedMoviesList.isNotEmpty()) { // add header item for watched movies
            notWatchedMoviesList.add(Movie())
        }

        submitList(notWatchedMoviesList + watchedMoviesList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM -> MovieViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_bucketlist_movie, parent, false
                )
            )
            else -> HeaderViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_bucketlist_movie_header, parent, false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        val movie = getItem(position)
        return when(movie.id) {
            null, 0 -> HEADER
            else -> ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM) {
            (holder as MovieViewHolder).bind(getItem(position))
        }
    }

    inner class MovieViewHolder(private val movieItemView: View): RecyclerView.ViewHolder(movieItemView) {
        fun bind(movie: Movie) {
            if (movie.isWatched) {
                movieItemView.container_layout.alpha = 0.5f
            } else {
                movieItemView.container_layout.alpha = 1f
            }
            movieItemView.movie_title.text = movie.title
            movieItemView.movie_added_time.text = dateConverter(movie.addedTimestamp!!)
            if (movie.addedBy!!.id == FirebaseAuth.getInstance().currentUser!!.uid) {
                movieItemView.movie_added_by.visibility = View.GONE
            } else {
                movieItemView.movie_added_by.visibility = View.VISIBLE
                movieItemView.movie_added_by.text = movie.addedBy!!.name
            }
            Glide
                .with(movieItemView.context)
                .load(BASE_URL_IMG + movie.posterPath)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean { // image ready, hide progress now
                        movieItemView.movie_progress.visibility = View.GONE
                        Log.e("onResourceReady", model.toString())
                        return false // return false if you want Glide to handle everything else.
                    }
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        movieItemView.movie_progress.visibility = View.GONE
                        movieItemView.movie_no_poster.visibility = View.VISIBLE
                        Log.e("onLoadFailed", model.toString())
                        Log.e("onLoadFailed", "id: ${movie.id}\ttitle: ${movie.title}")
                        return false
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL) // cache both original & resized image
                .centerCrop()
                .transition(withCrossFade())
                .into(movieItemView.movie_poster)

            if (onItemClickListener != null) {
                movieItemView.setOnClickListener {
                    if (adapterPosition > -1) {
                        onItemClickListener!!.itemClickListener(getItem(adapterPosition))
                    }
                }
            }

            movieItemView.setOnLongClickListener {
                val dialog = DisplayMovieTrailerAlertDialog(
                    movieItemView.context,
                    movie.id!!,
                    R.layout.dialog_movie_details,
                    false
                )
                dialog.create()
                val dialogLayout = dialog.view

                dialog.setOnShowListener {
                    dialogLayout.movie_title.text = movie.title
                    dialogLayout.movie_desc.text = movie.overview
                    if (movie.releaseDate!!.length > 4)
                        dialogLayout.movie_year.text = movie.originalLanguage!!.toUpperCase() + " | " + movie.releaseDate
                    else {
                        dialogLayout.movie_year.visibility = View.GONE
                    }
                    Glide
                        .with(dialogLayout.context)
                        .load(BASE_URL_IMG_BACKDROP + movie.backdropPath)
                        .listener(object : RequestListener<Drawable> {
                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean { // image ready, hide progress now
                                dialogLayout.movie_progress.visibility = View.GONE
                                Log.e("onResourceReady", model.toString())
                                return false // return false if you want Glide to handle everything else.
                            }
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                dialogLayout.movie_progress.visibility = View.GONE
                                dialogLayout.no_backdrop.visibility = View.VISIBLE
                                Log.e("onLoadFailed", model.toString())
                                Log.e("onLoadFailed", "id: ${movie.id}\ttitle: ${movie.title}")
                                return false
                            }
                        })
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // cache both original & resized image
                        .centerCrop()
                        .transition(withCrossFade())
                        .fitCenter()
                        .into(dialogLayout.movie_backdrop)
                }
                true
            }
        }
    }

    inner class HeaderViewHolder(headerItemView: View): RecyclerView.ViewHolder(headerItemView)

    interface OnItemClickListener {
        fun itemClickListener(movie: Movie)
    }

    companion object {
        private const val ITEM = 0
        private const val HEADER = 1
    }
}