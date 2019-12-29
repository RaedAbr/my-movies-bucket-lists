package mse.mobop.mymoviesbucketlists.ui.recyclerview.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import kotlinx.android.synthetic.main.dialog_movie_poster.view.*
import kotlinx.android.synthetic.main.item_list_movie.view.*
import mse.mobop.mymoviesbucketlists.R
import mse.mobop.mymoviesbucketlists.model.Movie
import mse.mobop.mymoviesbucketlists.utils.BASE_URL_IMG
import mse.mobop.mymoviesbucketlists.utils.BASE_URL_IMG_POSTER


@SuppressLint("DefaultLocale", "InflateParams", "SetTextI18n")
class MoviesPaginationAdapter(
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var movieResults: ArrayList<Movie> = ArrayList()
    private var moviesAlreadyAdded: ArrayList<Movie> = ArrayList()
    private var isLoadingAdded = false
    private var lastClickedMoviePosition: Int = -1
    private var itemListener: ItemListener? = null
    fun setOnItemLongClickListener(itemListener: ItemListener) {
        this.itemListener = itemListener
    }

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
                movieVH.bind(movie)
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
        notifyDataSetChanged()
    }

    fun addAll(mcList: List<Movie>) {
        var filteredList = mcList
        moviesAlreadyAdded.forEach { existedMovie ->
            filteredList = filteredList.filter { it.id != existedMovie.id}
        }
        movieResults.addAll(filteredList)
        notifyDataSetChanged()
    }

    fun clear() {
        isLoadingAdded = false
        movieResults.clear()
        notifyDataSetChanged()
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(Movie())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position = movieResults.size - 1
        movieResults.removeAt(position)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Movie {
        return movieResults[position]
    }

    fun setMoviesAlreadyAdded(movies: ArrayList<Movie>) {
        this.moviesAlreadyAdded = movies
    }
    /*
   View Holders
   _________________________________________________________________________________________________
    */
    /**
     * Main list's content ViewHolder
     */
    private inner class MovieVH(private val movieItemView: View): RecyclerView.ViewHolder(movieItemView) {
        private val mMovieTitle: TextView = movieItemView.movie_title
        private val mMovieDesc: TextView = movieItemView.movie_desc
        private val mYear: TextView = movieItemView.movie_year // displays "year | language"
        internal val mPosterImg: ImageView = movieItemView.movie_poster
        internal val mProgress: ProgressBar = movieItemView.movie_progress
        internal val mNoPoster: TextView = movieItemView.movie_no_poster

        fun bind(movie: Movie) {
            mMovieTitle.text = movie.title
            mYear.text = movie.originalLanguage!!.toUpperCase() + " | " + movie.releaseDate
            mMovieDesc.text = movie.overview!!

            movieItemView.movie_selected_checkbox.isChecked = movie.isSelected
            if (movie.isSelected) {
//                itemView.background = itemView.context.getDrawable(R.drawable.background_movie_selected)
                movieItemView.setBackgroundResource(R.drawable.background_movie_selected)
            } else {
//                itemView.background = null
                movieItemView.setBackgroundResource(R.color.white)
            }
            if (movie.isExpanded) {
                mMovieDesc.ellipsize = null
                mMovieDesc.maxLines = Int.MAX_VALUE
                mMovieTitle.ellipsize = null
                mMovieTitle.maxLines = Int.MAX_VALUE
            } else {
                mMovieDesc.maxLines = 3
                mMovieDesc.ellipsize = TextUtils.TruncateAt.END
                mMovieTitle.maxLines = 1
                mMovieTitle.ellipsize = TextUtils.TruncateAt.END
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
                        mProgress.visibility = View.GONE
                        mPosterImg.setOnClickListener(showDialogPoster(movie))
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
                        mPosterImg.setOnClickListener(null)
                        Log.e("onLoadFailed", model.toString())
                        Log.e("onLoadFailed", "id: ${movie.id}\ttitle: ${movie.title}")
                        return false
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL) // cache both original & resized image
                .centerCrop()
                .transition(withCrossFade())
                .into(mPosterImg)

            movieItemView.movie_linear_layout.setOnClickListener {
                if (lastClickedMoviePosition != -1 &&
                    lastClickedMoviePosition != adapterPosition &&
                    movieResults[lastClickedMoviePosition].isExpanded) {
                    movieResults[lastClickedMoviePosition].isExpanded = false
                }
                movieResults[adapterPosition].isExpanded = !movieResults[adapterPosition].isExpanded
                lastClickedMoviePosition = adapterPosition
                notifyDataSetChanged()
            }

            if (itemListener != null) {
                movieItemView.movie_linear_layout.setOnLongClickListener {
                    itemListener!!.onItemLongClick(adapterPosition)
                    true
                }

                // Show trailer Dialog
                mPosterImg.setOnLongClickListener {
                    itemListener!!.onPosterLongClick(movie.id!!)
                    true
                }
            }
        }

        private fun showDialogPoster(movie: Movie): View.OnClickListener? {
            return View.OnClickListener {
                val builder = AlertDialog.Builder(context/*, R.style.TransparentDialog*/)
                val inflater: LayoutInflater = (context as AppCompatActivity).layoutInflater
                val dialogLayout: View = inflater.inflate(R.layout.dialog_movie_poster, null)

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
                                dialogLayout.poster_progress.visibility = View.GONE
                                Log.e("onResourceReadyyyy", model.toString())
                                Log.e(
                                    "onResourceReadyyyy",
                                    "id: ${movie.id}\ttitle: ${movie.title}\t path: ${movie.posterPath}"
                                )
                                return false // return false if you want Glide to handle everything else.
                            }

                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
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
    }

    private inner class LoadingVH(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!)

    interface ItemListener {
        fun onItemLongClick(position: Int)
        fun onPosterLongClick(movieId: Int)
    }

    companion object {
        private const val ITEM = 0
        private const val LOADING = 1
    }
}