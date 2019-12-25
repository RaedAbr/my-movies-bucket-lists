package mse.mobop.mymoviesbucketlists.ui.recyclerview.adapters

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.item_bucketlist_movie.view.*
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.model.Movie
import mse.mobop.mymoviesbucketlists.utils.BASE_URL_IMG

class BucketlistMoviesAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private var movies: List<Movie> = ArrayList()

    fun setMovies(movies: List<Movie>) {
        this.movies = movies as ArrayList<Movie>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_bucketlist_movie, parent, false
        ))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MovieViewHolder).bind(movies[position])
    }

    override fun getItemCount(): Int {
        return movies.size
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
        }
    }
}