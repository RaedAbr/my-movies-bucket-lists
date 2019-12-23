package mse.mobop.mymoviesbucketlists.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import at.blogc.android.views.ExpandableTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import kotlinx.android.synthetic.main.alert_dialog_movie_poster.view.*
import kotlinx.android.synthetic.main.item_list_movie.view.*
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.model.Movie


class MoviesPaginationAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var movieResults: ArrayList<Movie> = ArrayList()
    private var isLoadingAdded = false
    private var lastClickedMovieDescription: ExpandableTextView? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(parent.context)
        viewHolder = when (viewType) {
            ITEM -> MovieVH(inflater.inflate(R.layout.item_list_movie, parent, false))
            // else it's a loading bar
            else -> LoadingVH(inflater.inflate(R.layout.item_progress, parent, false))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie: Movie = movieResults[position]
        when (getItemViewType(position)) {
            ITEM -> {
                val movieVH = holder as MovieVH
                movieVH.bind(movie, context)
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
    private inner class MovieVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        internal val mMovieTitle: TextView = itemView.movie_title
        internal val mMovieDesc: ExpandableTextView = itemView.movie_desc
        internal val mYear: TextView = itemView.movie_year // displays "year | language"
        internal val mPosterImg: ImageView = itemView.movie_poster
        internal val mProgress: ProgressBar = itemView.movie_progress
        internal val mNoPoster: TextView = itemView.movie_no_poster

        @SuppressLint("DefaultLocale")
        fun bind(movie: Movie, context: Context) {
            mMovieTitle.text = movie.title
            if (movie.releaseDate!!.length > 4)
                mYear.text = (movie.releaseDate.substring(0, 4) // we want the year only
                        + " | " + movie.originalLanguage!!.toUpperCase())
            mMovieDesc.text = movie.overview!!

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
                        mProgress.visibility = View.GONE
                        mPosterImg.setOnLongClickListener {
                            Log.e("moviePosterSetOnLongClickListener", movie.toString())
                            showDialogPoster(movie)
                            false
                        }
                        Log.e("onResourceReady", model.toString())
                        return false // return false if you want Glide to handle everything else.
                    }
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        mProgress.visibility = View.GONE
                        mNoPoster.visibility = View.VISIBLE
                        mPosterImg.setOnLongClickListener(null)
                        Log.e("onLoadFailed", model.toString())
                        Log.e("onLoadFailed", "id: ${movie.id}\ttitle: ${movie.title}")
                        return false
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL) // cache both original & resized image
                .centerCrop()
                .transition(withCrossFade())
                .into(mPosterImg)

            itemView.movie_linear_layout.setOnClickListener {
                if (lastClickedMovieDescription != null &&
                    lastClickedMovieDescription != mMovieDesc &&
                    lastClickedMovieDescription!!.isExpanded) {
                    lastClickedMovieDescription!!.toggle()
                }
                mMovieDesc.toggle()
                lastClickedMovieDescription = mMovieDesc
            }
        }

        fun showDialogPoster(movie: Movie) {
            val builder = AlertDialog.Builder(context/*, R.style.TransparentDialog*/)
            val inflater: LayoutInflater = (context as AppCompatActivity).layoutInflater
            val dialogLayout: View = inflater.inflate(R.layout.alert_dialog_movie_poster, null)

            val dialog = builder.create()
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setView(dialogLayout)

            dialog.setOnShowListener {
                Log.e("wahoo", "wahoo")
                Glide
                    .with(context)
                    .load(BASE_URL_IMG_POSTER + movie.posterPath)
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean { // image ready, hide progress now
//                            mProgress.visibility = View.GONE
                            Log.e("onResourceReadyyyy", model.toString())
                            Log.e("onResourceReadyyyy", "id: ${movie.id}\ttitle: ${movie.title}\t path: ${movie.posterPath}")
//                            dialog.window!!.setLayout(resource!!.intrinsicWidth, resource!!.intrinsicHeight)
                            return false // return false if you want Glide to handle everything else.
                        }
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
//                            mProgress.visibility = View.GONE
//                            mNoPoster.visibility = View.VISIBLE
                            Log.e("onLoadFailedddd", model.toString())
                            Log.e("onLoadFailedddd", "id: ${movie.id}\ttitle: ${movie.title}")
                            return false
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // cache both original & resized image
                    .fitCenter()
                    .transition(withCrossFade())
                    .into(dialogLayout.movie_poster_image)
            }
            dialogLayout.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()

        }
    }

    private inner class LoadingVH(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!)

    companion object {
        private const val ITEM = 0
        private const val LOADING = 1
        private const val BASE_URL_IMG = "https://image.tmdb.org/t/p/w154"
        private const val BASE_URL_IMG_POSTER = "https://image.tmdb.org/t/p/w342"
    }
}