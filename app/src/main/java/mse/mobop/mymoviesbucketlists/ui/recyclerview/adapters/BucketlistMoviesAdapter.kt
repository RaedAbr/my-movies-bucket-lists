package mse.mobop.mymoviesbucketlists.ui.recyclerview.adapters

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.item_bucketlist_movie_not_watched.view.*
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.model.Movie
import mse.mobop.mymoviesbucketlists.utils.BASE_URL_IMG
import mse.mobop.mymoviesbucketlists.utils.BASE_URL_IMG_BACKDROP

@SuppressLint("DefaultLocale", "InflateParams")
class BucketlistMoviesAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private var movies: List<Movie> = ArrayList()
    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    fun setMovies(movies: List<Movie>) {
        this.movies = movies
        sortMovies()
        notifyDataSetChanged()
    }

    private fun sortMovies() {
        movies = movies.sortedWith(compareBy<Movie> { it.isWatched}
            .thenByDescending { it.addedTimestamp })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return when(viewType) {
            WATCHED -> MovieViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_bucketlist_movie_watched, parent, false
                )
            )
            else -> MovieViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_bucketlist_movie_not_watched, parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MovieViewHolder).bind(movies[position])
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun getItemViewType(position: Int): Int {
        val movie = movies[position]
        return when(movie.isWatched) {
            true -> WATCHED
            else -> NO_WATCHED
        }
    }

    inner class MovieViewHolder(private val movieItemView: View): RecyclerView.ViewHolder(movieItemView) {
        fun bind(movie: Movie) {
            movieItemView.movie_title.text = movie.title
            if (movie.addedBy!!.id == FirebaseAuth.getInstance().currentUser!!.uid) {
                movieItemView.movie_added_by.visibility = View.GONE
            } else {
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
                    onItemClickListener!!.itemClickListener(movie)
                }
            }

            movieItemView.setOnLongClickListener {
                val builder = AlertDialog.Builder(movieItemView.context/*, R.style.TransparentDialog*/)
                val inflater: LayoutInflater = (movieItemView.context as AppCompatActivity).layoutInflater
                val dialogLayout: View = inflater.inflate(R.layout.dialog_movie_details, null)

                val dialog = builder.create()
                dialog.setView(dialogLayout)

                dialog.setOnShowListener {
                    dialogLayout.movie_title.text = movie.title
                    dialogLayout.movie_desc.text = movie.overview
                    if (movie.releaseDate!!.length > 4)
                        dialogLayout.movie_year.text = (movie.releaseDate.substring(0, 4) // we want the year only
                                + " | " + movie.originalLanguage!!.toUpperCase())
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
                dialog.show()

                true
            }
        }
    }

    interface OnItemClickListener {
        fun itemClickListener(movie: Movie)
    }

    companion object {
        private const val WATCHED = 0
        private const val NO_WATCHED = 1
    }
}